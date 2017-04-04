package com.liusu.tcp.proxy.mine;

import java.io.Serializable;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.server.ServerBootstrapFactory;
import com.liusu.tcp.proxy.mine.task.MessageWrapListener;
import com.sun.glass.ui.Application;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.wondersoft.message.queue.base.mq.AbstractMessageQueue;
import com.wondersoft.message.queue.util.MessageQueueFactory;

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
		// 开启监听MQ
		RedisTemplate<String, Serializable> redisTemplate = Constant.APPLICATION_CONTEXT.getBean("redisTemplate",RedisTemplate.class);
		AbstractMessageQueue<Serializable> messageQueue = MessageQueueFactory.redisMessageQueueCreate(redisTemplate);
		Executors.newSingleThreadExecutor().execute(new MessageWrapListener(messageQueue));
		
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
