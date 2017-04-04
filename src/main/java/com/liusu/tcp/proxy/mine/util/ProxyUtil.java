package com.liusu.tcp.proxy.mine.util;

import java.net.InetSocketAddress;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.base.Constant.ProxyEnum;

public final class ProxyUtil {

	private ProxyUtil(){
		
	}
	
	public static String getTopic(int proxyCode) {
		ProxyEnum[] values = Constant.ProxyEnum.values();

		for (ProxyEnum e : values) {
			if(e.getProxyCode() == proxyCode){
				 return e.getProxyName();
			}
		}
		
		throw new IllegalArgumentException("ProxyUtil [getTopic] invalid proxyCode!");
	}
	
	
	public static String getTopicListen(int proxyCode) {
		ProxyEnum[] values = Constant.ProxyEnum.values();

		for (ProxyEnum e : values) {
			if(e.getProxyCode() != proxyCode){
				 return e.getProxyName();
			}
		}
		
		throw new IllegalArgumentException("ProxyUtil [getTopic] invalid proxyCode!");
	}
	
	public static InetSocketAddress getAddressForProxy(int proxyCode){
		String host = "";
		int port = 0;
		
		if(proxyCode == Constant.ProxyEnum.PROXY01.getProxyCode()){
			host = Constant.paramProp.getProperty("LOW_PORXY_LISTEN_HOST");
			port = Integer.parseInt(Constant.paramProp.getProperty("LOW_PORXY_LISTEN_PORT"));
		}else{
			host = Constant.paramProp.getProperty("TETTY_PORXY_LISTEN_HOST");
			port = Integer.parseInt(Constant.paramProp.getProperty("TETTY_PORXY_LISTEN_PORT"));
		}
		
		return new InetSocketAddress(host, port);
	}
}
