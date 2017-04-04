package com.liusu.tcp.proxy.mine.codec;

import java.io.Serializable;

import com.liusu.tcp.proxy.mine.domain.MessageWrap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class MessageWrapEncoder extends ObjectEncoder{

	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable msg,
			ByteBuf out) throws Exception {
		if(msg instanceof MessageWrap){
			MessageWrap mess = (MessageWrap) msg;
			if(mess.getMessage() instanceof ByteBuf){
				ByteBuf buf = (ByteBuf)mess.getMessage();
				byte[] remain = new byte[buf.readableBytes()];
				buf.readBytes(remain);
				buf.release();
				mess.setMessage(remain);
			}
			
		}
		super.encode(ctx, msg, out);
	}
	
	
}
