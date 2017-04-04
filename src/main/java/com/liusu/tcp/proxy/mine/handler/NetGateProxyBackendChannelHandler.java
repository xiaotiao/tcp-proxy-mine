package com.liusu.tcp.proxy.mine.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.util.Objects;
import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.codec.MessageWrapDecoder;
import com.liusu.tcp.proxy.mine.codec.MessageWrapEncoder;
import com.liusu.tcp.proxy.mine.domain.MessageWrap;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;

public class NetGateProxyBackendChannelHandler extends NetGateProxyChannelHandler {
	
	public NetGateProxyBackendChannelHandler(ProxyRule rule){
		super(rule);
		this.rule = rule;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(Objects.isNull(rule)){
			 throw new IllegalArgumentException(" NetGateProxyBackendChannelHandler [channelRead],please define your ProxyRule..");
		}
		
		if(msg instanceof MessageWrap){
			final MessageWrap mess = (MessageWrap)msg;
			String searchFlag = new StringBuilder().append(mess.getDestChannelID()).toString();
			Channel channel = Constant.inboundChannles.get(searchFlag);
			
			int type = mess.getType();
			int inActiveType = MessageWrap.MessageType.InActive.getType();
			//断开连接
			if(type == inActiveType){
				if(!Objects.isNull(channel) && channel.isActive()){
					Constant.inboundChannles.remove(searchFlag);
					channel.close();
				}
			}else{
				if(Objects.isNull(channel) || !channel.isActive()){
					Bootstrap b = new Bootstrap(); 
					b.group(ctx.channel().eventLoop())
					 .channel(NioSocketChannel.class)
					 .option(ChannelOption.TCP_NODELAY, true)
					 .handler(new MessageWrapEncoder())
					 .handler(new AcceptServerSideChannelHandler(searchFlag.toString(),mess));
					
					 ChannelFuture f = b.connect(mess.getRemoteAddress()).addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
//							Object obj = MessageWrap.wrapMessageByteBuf(mess);
//							if(obj instanceof ByteBuf){
//								ByteBuf buf = (ByteBuf) obj;
//								if(buf.readableBytes()>0){
//									future.channel().writeAndFlush(obj);
//								}
//							}
							if(mess.getType() == MessageWrap.MessageType.Application.getType()){
								future.channel().writeAndFlush(mess.getMessage());
							}
//							future.channel().writeAndFlush(MessageWrap.wrapMessageByteBuf(mess));
						}
					});
					 channel = f.channel();
				}else{
//					Object obj = MessageWrap.wrapMessageByteBuf(mess);
//					if(obj instanceof ByteBuf){
//						ByteBuf buf = (ByteBuf) obj;
//						if(buf.readableBytes()>0){
//							channel.writeAndFlush(MessageWrap.wrapMessageByteBuf(mess));
//						}
//					}
					
					if(mess.getType() == MessageWrap.MessageType.Application.getType()){
						channel.writeAndFlush(mess.getMessage());
					}
				}
			}
				
				
			
			
		}else{
			ctx.fireChannelRead(msg);
		}

	}
	
	
	/**
	 * 
	 * 鐢ㄤ簬涓巗erver绔缓绔嬭繛鎺�浠ュ強瀵瑰簲浜嬩欢鐨勫鐞�
	 *
	 */
	private static class AcceptServerSideChannelHandler extends DefaultProxyChannelHandler{
		
		private static final String TETTY_PORXY_NETGATE_WRITE_HOST = Constant.paramProp.getProperty("TETTY_PORXY_NETGATE_WRITE_HOST");
		private static final int TETTY_PORXY_NETGATE_WRITE_PORT = Integer.parseInt(Constant.paramProp.getProperty("TETTY_PORXY_NETGATE_WRITE_PORT"));
		
		private String searchFlag;
		private MessageWrap mess;
		
		public AcceptServerSideChannelHandler(String searchFlag,MessageWrap mess) {
			this.searchFlag = searchFlag;
			this.mess = mess;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			 Constant.inboundChannles.put(searchFlag, ctx.channel());
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			mess.setMessage(msg);
//			mess = MessageWrap.wrapMessageByteArray(mess.getDestChannelID(), msg, mess.getRemoteAddress());
			//姝ゅ鍙彂鐢無utbound浜嬩欢
			Channel c = Constant.inboundChannles.get(SendToNetGateChannelHandler.flag);
			if(Objects.isNull(c) || !c.isActive()){
				Bootstrap b = new Bootstrap(); 
				b.group(ctx.channel().eventLoop())
				 .channel(NioSocketChannel.class)
				 .option(ChannelOption.TCP_NODELAY, true)
				 .handler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
//						p.addLast(new ObjectEncoder());
//						p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
						
						p.addLast(new MessageWrapEncoder());
						p.addLast(new MessageWrapDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
						
						p.addLast(new SendToNetGateChannelHandler());
					}
				});
				
				//杩炴帴缃戦椄
				ChannelFuture f = b.connect(TETTY_PORXY_NETGATE_WRITE_HOST,TETTY_PORXY_NETGATE_WRITE_PORT).addListener(new ChannelFutureListener() {
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
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			
			Constant.inboundChannles.remove(searchFlag);
		}
	}
	
	
	
	/**
	 * 
	 * 鐢ㄤ簬浠ｇ悊02鍙戦�璇锋眰缁欑綉闂�2 鐩殑锛氭墍鏈夌粡鐢变唬鐞�2鍙戦�缁欑綉闂�2鐨勮姹傚潎鍙娇鐢ㄥ悓涓�釜channel
	 * 
	 * 鎷撳睍锛氫负浜嗘彁楂樹紶杈撻�搴�鍙互浣跨敤涓�畾鏁伴噺鐨刢hannel鏋勬垚姹�
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
