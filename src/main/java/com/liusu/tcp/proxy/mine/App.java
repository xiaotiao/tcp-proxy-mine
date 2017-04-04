package com.liusu.tcp.proxy.mine;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import com.liusu.tcp.proxy.mine.server.ServerBootstrapFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * 代理启动类
 * 
 */
@SuppressWarnings("all")
public class App {
	private static final InternalLogger log = InternalLoggerFactory
			.getInstance(App.class);

	private static Config conf = ConfigFactory.load();

	public static Config getConfig() {
		return conf;
	}

	private void run() throws Exception {
		ServerBootstrap b = ServerBootstrapFactory.getServerBootstrap();
		
	}

	public static void main(String[] args) throws Exception {
		new App().run();
	}

	public static boolean isDebug() {
		boolean debug = App.getConfig().getBoolean("tcpProxyServer.debug");
		return debug;
	}
}
