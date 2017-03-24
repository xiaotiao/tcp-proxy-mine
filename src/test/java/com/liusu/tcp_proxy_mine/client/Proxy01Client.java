package com.liusu.tcp_proxy_mine.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import org.junit.Before;
import org.junit.Test;


/**
 * 模拟客户端连接代理01
 */
public class Proxy01Client {

	Bootstrap b = new Bootstrap();

	EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Before
	public void setUp() {
		b.group(workerGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new Proxy01ClientHandler());
					}
				});
	}

	@Test
	public void testSendMessage() throws InterruptedException {
		String host = "localhost";
		int port = 50088;

		String msg = "Hello World!" + System.getProperty("line.separator");
		ChannelFuture f = b.connect(host, port).sync();

		f.channel().writeAndFlush(Unpooled.wrappedBuffer(msg.getBytes()));

		f.channel().closeFuture().sync();
	}

	public class Proxy01ClientHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {

			System.out.println("Proxy01ClientHandler [channelRead] received: "
					+ msg);
		}
	}

}
