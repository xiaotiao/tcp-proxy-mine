package com.liusu.tcp_proxy_mine.client;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;




import com.liusu.tcp_proxy_mine.domain.MessageWrap;


/**
 * POJO client
 */
public class POJOClient {

	Bootstrap b = new Bootstrap();

	EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Before
	public void setUp() {
		b.group(workerGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ObjectEncoder());
						ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
						ch.pipeline().addLast(new Proxy01ClientHandler());
					}
				});
	}

	@Test
	public void testSendMessage() throws InterruptedException {
		String host = "localhost";
		int port = 8080;

		MessageWrap mess = new MessageWrap();
		Object obj = Unpooled.wrappedBuffer("Hello World!".getBytes());
		mess = MessageWrap.wrapMessageByteArray("123", "Hello World!".getBytes(), new InetSocketAddress("localhost",50088));
		
		ChannelFuture f = b.connect(host, port).sync();

		f.channel().writeAndFlush(mess);

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
