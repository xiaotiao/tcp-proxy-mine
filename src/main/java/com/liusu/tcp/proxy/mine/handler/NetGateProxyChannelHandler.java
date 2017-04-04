package com.liusu.tcp.proxy.mine.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.Objects;
import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.domain.MessageWrap;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;

/**
 * 
 * @author zzh
 *
 * 1. 鎺ユ敹缃戦椄鐨勫搷搴�
 * 2. 灏嗗搷搴斿彂閫佺粰璇锋眰鐨勫鎴风channel
 */
public class NetGateProxyChannelHandler extends DefaultProxyChannelHandler {
	
	public NetGateProxyChannelHandler(ProxyRule rule){
		this.rule = rule;
	}
	
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		if(Objects.isNull(rule)){
			 throw new IllegalArgumentException(" NetGateProxyChannelHandler [channelRead] error!,please define your ProxyRule..");
		}
		
		InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
		if(rule.isValid(localAddress.getPort())){
			//鏄綉闂歌姹傦紝鎷嗗紑鏁版嵁鍖咃紝鎵惧埌瀵瑰簲channel
			if(msg instanceof MessageWrap){
				 MessageWrap mes = (MessageWrap) msg;
				 
				 String channelID = mes.getDestChannelID();
				 Channel destChannel = Constant.inboundChannles.get(channelID);
				 if(!Objects.isNull(destChannel) && destChannel.isActive()){
					 destChannel.writeAndFlush(mes.getMessage()).addListener(new ChannelFutureListener(){

						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
							 if (future.isSuccess()) {
			                        // was able to flush out data, start to read the next chunk
			                        ctx.channel().read();
			                    } else {
			                        future.channel().close();
			                 }
						}
						 
					 });
				 }
			}
		}else{
			//浣跨敤rule杩涜鏍￠獙,涓嶆弧瓒虫潯浠剁殑涓嶅鐞�鐩存帴鍚戜笅涓�釜handler浼犻�
			ctx.fireChannelRead(msg);
		}
	}
}
