package com.liusu.tcp.proxy.mine.server;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

public abstract class AbstractProxyServer implements ProxyServer {

	private Channel serverChannel;
	
	public void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
		
		serverChannel = doStart(bossGroup, workerGroup);
	}
	
	protected abstract Channel doStart(EventLoopGroup bossGroup, EventLoopGroup workerGroup);
}
