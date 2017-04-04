package com.liusu.tcp.proxy.mine.codec;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.domain.MessageWrap;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;

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
		
		// 如果为环回连接,放弃解码
		InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
		String mqPort = Constant.paramProp.getProperty("MQ_HANDLER_PORT");
		ProxyRule rule = new ProxyRule(Pattern.compile(mqPort));
		if(rule.isValid(address.getPort())){
			return in;
		}
		
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
