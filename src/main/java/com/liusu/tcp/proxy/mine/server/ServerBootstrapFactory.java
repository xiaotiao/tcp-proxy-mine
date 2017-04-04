package com.liusu.tcp.proxy.mine.server;

import java.util.ArrayList;
import java.util.Objects;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import com.liusu.tcp.proxy.mine.App;
import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.domain.ProxyHost;
import com.liusu.tcp.proxy.mine.handler.ProxyBackendHandler;
import com.liusu.tcp.proxy.mine.handler.ProxyFrontendHandler;

public final class ServerBootstrapFactory {

	private static final InternalLogger log = InternalLoggerFactory
			.getInstance(App.class);

	private static final int PROXY_CODE = Integer.parseInt(Constant.paramProp
			.getProperty("PROXY_CODE"));

	private static final int BOSS_GROUP_THREADS = Integer
			.parseInt(Constant.paramProp.getProperty("BOSS_GROUP_THREADS"));

	private static final int WORKER_GROUP_THREADS = Integer
			.parseInt(Constant.paramProp.getProperty("WORKER_GROUP_THREADS"));

	private static ServerBootstrap serBootstrap = null;

	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(
			BOSS_GROUP_THREADS);

	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(
			WORKER_GROUP_THREADS);

	private ServerBootstrapFactory() {

	}

	public static synchronized ServerBootstrap getServerBootstrap() {
		return getServerBootstrap(PROXY_CODE);
	}

	public static synchronized ServerBootstrap getServerBootstrap(int proxyCode) {

		if (!Objects.isNull(serBootstrap)) {
			return serBootstrap;
		}

		ChannelHandler handler;
		ArrayList<ProxyHost> hostList = new ArrayList<>();

		if (proxyCode == Constant.PROXY01_CODE) {
			handler = new ProxyFrontendHandler();
			hostList = ProxyHost.getProxy01HostList();
		} else if (proxyCode == Constant.PROXY02_CODE) {
			handler = new ProxyBackendHandler();
			hostList = ProxyHost.getProxy02HostList();
		} else {
			throw new IllegalArgumentException(
					"ServerBootstrapFactory [getServerBootstrap] PROXY_CODE is error! the range is [1 - 2]..");
		}
		serBootstrap = createServerBootstrapProxy(handler);
		bindServerBootstrapPort(serBootstrap, hostList);

		return serBootstrap;
	}

	private static ServerBootstrap createServerBootstrapProxy(
			ChannelHandler handler) {

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.option(ChannelOption.SO_REUSEADDR, false)
				.channel(NioServerSocketChannel.class)
				// .handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(handler)//.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_BACKLOG, Integer.MAX_VALUE);

		// .childOption(ChannelOption.AUTO_READ, true);

		return bootstrap;
	}

	/**
	 * 绑定服务端口
	 */
	private static void bindServerBootstrapPort(ServerBootstrap serBootstrap,
			ArrayList<ProxyHost> hostList) {
		try {

			log.info("TcpProxy server ready for connections...");

			ArrayList<Channel> allchannels = new ArrayList<Channel>();
			log.info("TcpProxy config: ");
			for (ProxyHost host : hostList) {
				ProxyHost.getProxyHosts().put(host.getLocalPort(), host);
				log.info("local port = " + host.getLocalPort()
						+ "|remote host=" + host.getRemoteHost()
						+ "|remote port=" + host.getRemotePort());
				Channel ch = serBootstrap.bind(host.getLocalPort()).sync()
						.channel();
				allchannels.add(ch);
			}
			for (Channel ch : allchannels) {
				ch.closeFuture().sync();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
