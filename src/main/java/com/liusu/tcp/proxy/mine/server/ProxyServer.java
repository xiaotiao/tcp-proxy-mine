package com.liusu.tcp.proxy.mine.server;

import io.netty.channel.EventLoopGroup;

public interface ProxyServer {

	void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup);
}
