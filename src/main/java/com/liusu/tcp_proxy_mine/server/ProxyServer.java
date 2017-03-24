package com.liusu.tcp_proxy_mine.server;

import io.netty.channel.EventLoopGroup;

public interface ProxyServer {

	void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup);
}
