package com.liusu.tcp.proxy.mine.handler;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.base.Constant.ProxyEnum;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;
import com.liusu.tcp.proxy.mine.util.ProxyUtil;
import com.wondersoft.message.queue.base.mq.AbstractMessageQueue;
import com.wondersoft.message.queue.util.MessageQueueFactory;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author zzh
 * 1. 代理环回自连本身,发送消息
 * 
 */
@SuppressWarnings("all")
public class MQProxyChannelHandler extends DefaultProxyChannelHandler {
	
	private static final String topic;
	
	static{
		
		topic = ProxyUtil.getTopic(Constant.LOCAL_PROXY_CODE);
	}

	private RedisTemplate<String, Serializable> redisTemplate = Constant.APPLICATION_CONTEXT
			.getBean(RedisTemplate.class);
	
	public MQProxyChannelHandler(ProxyRule rule) {
		this.rule = rule;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		if (Objects.isNull(rule)) {
			throw new IllegalArgumentException(
					" MQProxyChannelHandler [channelRead],please define your ProxyRule..");
		}

		InetSocketAddress localAddress = (InetSocketAddress) ctx.channel()
				.localAddress();

		if (rule.isValid(localAddress.getPort())) {

			AbstractMessageQueue redisMessageQueue = MessageQueueFactory
					.redisMessageQueueCreate(redisTemplate);
			redisMessageQueue.sendObjectMessage(topic, (Serializable)msg);

		} else {
			// 使用rule进行校验,不满足条件的不处理,直接向下一个handler传递
			ctx.fireChannelRead(msg);
		}

	}

	
}
