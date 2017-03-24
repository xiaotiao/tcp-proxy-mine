package com.liusu.tcp_proxy_mine.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.regex.Pattern;

import com.liusu.tcp_proxy_mine.domain.ProxyHost;
import com.liusu.tcp_proxy_mine.domain.ProxyRule;

public class ProxyBackendHandler extends ChannelInitializer<Channel>{

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//与server交互的代理接入点只有网闸1,此处接入端口应是网闸1的端口
		ProxyHost host = ProxyHost.getProxy02HostList().get(0);
		ProxyRule commonBackendRule = new ProxyRule(Pattern.compile(host.getLocalPort()+""));
		p.addLast(new ObjectEncoder());
		p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		p.addLast(new NetGateProxyBackendChannelHandler(commonBackendRule));
	}

}
