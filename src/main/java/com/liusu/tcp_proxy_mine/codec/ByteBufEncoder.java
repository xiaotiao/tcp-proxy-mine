package com.liusu.tcp_proxy_mine.codec;

import com.liusu.tcp_proxy_mine.domain.MessageWrap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ByteBufEncoder  extends MessageToByteEncoder<MessageWrap>{

//	@Override
//	protected void encode(ChannelHandlerContext ctx,ByteBuf msg, ByteBuf out)
//			throws Exception {
//		byte[] remain = new byte[msg.readableBytes()];
//		msg.readBytes(remain);
//		
//		out.writeByte((byte)'F');
//		out.writeInt(remain.length);
//		out.writeBytes(remain);
//	}

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageWrap msg,
			ByteBuf out) throws Exception {
//		out.wr
		
	}

	

}
