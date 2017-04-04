package com.liusu.tcp.proxy.mine.task;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Objects;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.server.ServerBootstrapFactory;
import com.liusu.tcp.proxy.mine.util.ProxyUtil;
import com.wondersoft.message.queue.base.mq.AbstractMessageQueue;

/**
 * 
 * @author zzh
 * 
 * 1. 负责从消息队列中取消息
 * 2. 模拟网闸,与代理建立连接
 */
public class MessageWrapListener implements Runnable{
	
	private static final String channelFlag = "MessageWrapListener";
	
	private AbstractMessageQueue<Serializable> messageQueue;
	
	public MessageWrapListener(AbstractMessageQueue<Serializable> messageQueue){
		this.messageQueue = messageQueue;
	}

	@Override
	public void run() {
		
		while(true){
			try {
				//1. 从消息队列中取消息
				String topic = ProxyUtil.getTopicListen(Constant.LOCAL_PROXY_CODE);
				
				// 此处主要采用阻塞写法
				Serializable message = messageQueue.receiveObjectMessage(topic);
				
				//2. 将消息写入代理
				writeMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void writeMessage(final Serializable mess){
		Channel channel = Constant.inboundChannles.get(channelFlag);
		
		if(Objects.isNull(channel) || !channel.isActive()){
			Bootstrap b = new Bootstrap(); 
			b.group(ServerBootstrapFactory.getWorkergroup())
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.TCP_NODELAY, true);
			
			InetSocketAddress address = ProxyUtil.getAddressForProxy(Constant.LOCAL_PROXY_CODE);
			b.connect(address).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						Constant.inboundChannles.put(channelFlag, future.channel());
						future.channel().write(mess);
					}
				}
			});
			
		}else{
			channel.writeAndFlush(mess);
		}
		
		 
	}

}
