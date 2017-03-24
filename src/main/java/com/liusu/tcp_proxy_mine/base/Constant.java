package com.liusu.tcp_proxy_mine.base;

import io.netty.channel.Channel;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Constant {

	public static Map<String, Channel> inboundChannles = new ConcurrentHashMap<>();
	
	public static final int PROXY01_CODE = 1;
	
	public static final int PROXY02_CODE = 2;

	public static Properties paramProp = new Properties();

	static {
		Resource res = new ClassPathResource("param.properties");
		try (InputStream is = res.getInputStream();) {
			paramProp.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
