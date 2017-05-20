package com.liusu.tcp_proxy_mine.handler;

import java.util.regex.Pattern;
import com.liusu.tcp_proxy_mine.codec.MessageWrapDecoder;
import com.liusu.tcp_proxy_mine.codec.MessageWrapEncoder;
import com.liusu.tcp_proxy_mine.domain.ProxyHost;
import com.liusu.tcp_proxy_mine.domain.ProxyRule;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProxyBackendHandler extends ChannelInitializer<Channel>{

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//与server交互的代理接入点只有网闸1,此处接入端口应是网闸1的端口
		ProxyHost host = ProxyHost.getProxy02HostList().get(0);
		ProxyRule commonBackendRule = new ProxyRule(Pattern.compile(host.getLocalPort()+""));
//		p.addLast(new ObjectEncoder());
//		p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
//		p.addLast( new LoggingHandler(LogLevel.INFO));
		p.addLast(new MessageWrapEncoder());
		p.addLast(new MessageWrapDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
		
		p.addLast(new NetGateProxyBackendChannelHandler(commonBackendRule));
	}

}
