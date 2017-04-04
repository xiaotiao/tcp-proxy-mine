package com.liusu.tcp.proxy.mine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;

public  class  DefaultProxyChannelHandler extends ChannelInboundHandlerAdapter{

	protected ProxyRule rule;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		Constant.inboundChannles.put(ctx.channel().id().asShortText(), ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
//		System.out.println(ctx.channel() + "inactive...");
		 Constant.inboundChannles.remove(ctx.channel().id().asShortText());
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
}
