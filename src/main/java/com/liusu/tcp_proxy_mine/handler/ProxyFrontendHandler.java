package com.liusu.tcp_proxy_mine.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.regex.Pattern;

import com.liusu.tcp_proxy_mine.base.Constant;
import com.liusu.tcp_proxy_mine.codec.MessageWrapDecoder;
import com.liusu.tcp_proxy_mine.codec.MessageWrapEncoder;
import com.liusu.tcp_proxy_mine.domain.ProxyRule;

public class ProxyFrontendHandler extends ChannelInitializer<Channel>{

	private static final String NETGATE_JOINT_PORT = Constant.paramProp.getProperty("NETGATE_JOINT_PORT");
	
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		StringBuilder sbCommon = new StringBuilder();
		//鍖归厤涓嶆槸9999鐨勭鍙�^(?!(9999))[0-65536]{1,5}
//		sbCommon.append("^(?!(")
//	      .append(NETGATE_JOINT_PORT)
//	      .append("))[0-65536]{1,5}");
//		sbCommon.append("3307");
		sbCommon.append("(?:6553[0-6]|655[012][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|999[0-8]|99[0-8][0-9]|9[0-9][0-9]|[1-8][0-9]{0,})");
	      
		
		ProxyRule commonRule = new ProxyRule(Pattern.compile(sbCommon.toString()));
		
		StringBuilder sbGate = new StringBuilder();
		sbGate.append(NETGATE_JOINT_PORT);
		     
		ProxyRule netGateRule = new ProxyRule(Pattern.compile(sbGate.toString()));
		
//		p.addLast( new LoggingHandler(LogLevel.INFO));
//		p.addLast(new ByteBufEncoder());
//		p.addLast(new ByteBufDecoder());
		p.addLast(new CommonProxyChannelHandler(commonRule));
//		p.addLast(new ObjectEncoder());
//		p.addLast(new ObjectDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
//		
		p.addLast(new MessageWrapEncoder());
//		p.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
		p.addLast(new MessageWrapDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
//		p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
		p.addLast(new NetGateProxyChannelHandler(netGateRule));
	}

}
