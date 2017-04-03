package com.liusu.tcp_proxy_mine.codec;

import com.liusu.tcp_proxy_mine.domain.MessageWrap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class MessageWrapDecoder extends ObjectDecoder{

	public MessageWrapDecoder(ClassResolver classResolver) {
		super(classResolver);
	}
	
	public MessageWrapDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
    }
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		
		Object obj = super.decode(ctx, in);
		if(obj instanceof MessageWrap){
			MessageWrap mess = (MessageWrap) obj;
			if(mess.getMessage() instanceof byte[]){
				mess.setMessage(Unpooled.wrappedBuffer((byte[])mess.getMessage()));
			}
		}
		
		return obj;
	}

}
