package com.liusu.tcp_proxy_mine.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import java.net.InetSocketAddress;
import java.util.Objects;
import com.liusu.tcp_proxy_mine.base.Constant;
import com.liusu.tcp_proxy_mine.codec.MessageWrapDecoder;
import com.liusu.tcp_proxy_mine.codec.MessageWrapEncoder;
import com.liusu.tcp_proxy_mine.domain.MessageWrap;
import com.liusu.tcp_proxy_mine.domain.ProxyHost;
import com.liusu.tcp_proxy_mine.domain.ProxyRule;

/**
 * 
 * @author zzh
 * 
 *         1. 接收客户端的请求 2. 将请求发送给网闸
 */
public class CommonProxyChannelHandler extends DefaultProxyChannelHandler {

	private static final String LOW_PORXY_NETGATE_WRITE_HOST = Constant.paramProp.getProperty("LOW_PORXY_NETGATE_WRITE_HOST");

	private static final int LOW_PORXY_NETGATE_WRITE_PORT = Integer.parseInt(Constant.paramProp.
															getProperty("LOW_PORXY_NETGATE_WRITE_PORT"));

	public CommonProxyChannelHandler(ProxyRule rule) {
		this.rule = rule;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		
		InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
		int type = MessageWrap.MessageType.Active.getType();
		String channelID = ctx.channel().id().asShortText();
		MessageWrap mess = MessageWrap.createEmptyMessageWrap(channelID, localAddress, type);
		
		sendToNetGate(ctx.channel().eventLoop(),mess);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		if (Objects.isNull(rule)) {
			throw new IllegalArgumentException(
					" CommonProxyChannelHandler [channelRead0],please define your ProxyRule..");
		}

		InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();

		if (rule.isValid(localAddress.getPort())) {
			ProxyHost proxyHost = ProxyHost.getProxyHosts(Constant.PROXY01_CODE).get(localAddress.getPort());
			String remoteHost = proxyHost.getRemoteHost();
			int remotePort = proxyHost.getRemotePort();

			NioSocketChannel channel = (NioSocketChannel) ctx.channel();

			final MessageWrap mess = new MessageWrap(channel.id().asShortText(), msg,new InetSocketAddress(remoteHost, remotePort));

			// 代理01和网闸之间一个channel即可满足需求,避免网闸端socket资源不够
			
            sendToNetGate(ctx.channel().eventLoop(), mess);


		} else {
			// 使用rule进行校验,不满足条件的不处理,直接向下一个handler传递
			ctx.fireChannelRead(msg);
		}

	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		
		InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
		int type = MessageWrap.MessageType.InActive.getType();
		String channelID = ctx.channel().id().asShortText();
		MessageWrap mess = MessageWrap.createEmptyMessageWrap(channelID, localAddress, type);
		
		sendToNetGate(ctx.channel().eventLoop(),mess);
	}
	
	
	public void sendToNetGate(EventLoop group,final MessageWrap mess){
		Channel c = Constant.inboundChannles
				.get(SendToNetGateChannelHandler.flag);
		if (Objects.isNull(c) || !c.isActive()) {
			Bootstrap b = new Bootstrap();
			b.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.AUTO_READ, false)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch)
								throws Exception {
							ch.pipeline().addLast(new MessageWrapEncoder());
							ch.pipeline().addLast(new MessageWrapDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
							ch.pipeline().addLast(new SendToNetGateChannelHandler());
						}
					});
			// 将消息写入网闸
			ChannelFuture f = b.connect(LOW_PORXY_NETGATE_WRITE_HOST,
					LOW_PORXY_NETGATE_WRITE_PORT).addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							future.channel().writeAndFlush(mess);
						}
					});
			c = f.channel();
		}else{
			c.writeAndFlush(mess);
		}
	}

	/**
	 * 
	 * 用于代理01发送请求给网闸01 目的：所有经由代理01发送给网闸01的请求均可使用同一个channel
	 * 
	 * 拓展：为了提高传输速度,可以使用一定数量的channel构成池
	 */
	@SuppressWarnings("all")
	private static class SendToNetGateChannelHandler extends
			DefaultProxyChannelHandler {

		static final String flag = "SendToNetGateChannelHandler";

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {

			Constant.inboundChannles.put(flag, ctx.channel());
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {

			Constant.inboundChannles.remove(flag);
		}
	}

}
