package com.liusu.tcp.proxy.mine.handler;

import java.util.regex.Pattern;
import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.codec.MessageWrapDecoder;
import com.liusu.tcp.proxy.mine.codec.MessageWrapEncoder;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProxyFrontendHandler extends ChannelInitializer<Channel>{

	private static final String NETGATE_JOINT_PORT = Constant.paramProp.getProperty("NETGATE_JOINT_PORT");
	
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		StringBuilder sbCommon = new StringBuilder();
		sbCommon.append("(?:6553[0-6]|655[012][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|999[0-8]|99[0-8][0-9]|9[0-8][0-9]{2}|[1-8][0-9]{0,})");
	    
		ProxyRule mqRule = new ProxyRule(Pattern.compile(Constant.paramProp.getProperty("MQ_HANDLER_PORT")));
		
		ProxyRule commonRule = new ProxyRule(Pattern.compile(sbCommon.toString()));
		
		StringBuilder sbGate = new StringBuilder();
		sbGate.append(NETGATE_JOINT_PORT);
		     
		ProxyRule netGateRule = new ProxyRule(Pattern.compile(sbGate.toString()));
		
		p.addLast( new LoggingHandler(LogLevel.INFO));
		// MQ的handler决定是否先处理
		p.addLast(new MQProxyChannelHandler(mqRule));
		p.addLast(new CommonProxyChannelHandler(commonRule));

		p.addLast(new MessageWrapEncoder());
		p.addLast(new MessageWrapDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
		
		p.addLast(new NetGateProxyChannelHandler(netGateRule));
	}

}
