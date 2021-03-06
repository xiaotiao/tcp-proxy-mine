package com.liusu.tcp.proxy.mine.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.regex.Pattern;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.codec.MessageWrapDecoder;
import com.liusu.tcp.proxy.mine.codec.MessageWrapEncoder;
import com.liusu.tcp.proxy.mine.domain.ProxyHost;
import com.liusu.tcp.proxy.mine.domain.ProxyRule;

public class ProxyBackendHandler extends ChannelInitializer<Channel>{

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//与server交互的代理接入点只有网闸1,此处接入端口应是网闸1的端口
		ProxyHost host = ProxyHost.getProxy02HostList().get(0);
		ProxyRule mqRule = new ProxyRule(Pattern.compile(Constant.paramProp.getProperty("MQ_HANDLER_PORT")));
		ProxyRule commonBackendRule = new ProxyRule(Pattern.compile(host.getLocalPort()+""));
//		p.addLast(new ObjectEncoder());
//		p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		
		p.addLast(new MessageWrapEncoder());
		p.addLast(new MessageWrapDecoder(Integer.MAX_VALUE,ClassResolvers.cacheDisabled(null)));
		
		p.addLast(new MQProxyChannelHandler(mqRule));
		p.addLast(new NetGateProxyBackendChannelHandler(commonBackendRule));
	}

}
