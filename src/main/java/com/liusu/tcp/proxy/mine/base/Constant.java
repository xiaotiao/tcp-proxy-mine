package com.liusu.tcp.proxy.mine.base;

import io.netty.channel.Channel;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Constant {

	public static final ApplicationContext APPLICATION_CONTEXT;
	
	public static final int PROXY01_CODE = 1;
	public static final int PROXY02_CODE = 2;
	
	private static final String SPRING_CONFIG_PATH = "spring.xml";
	
	public static Map<String, Channel> inboundChannles = new ConcurrentHashMap<>();

	public static Properties paramProp = new Properties();
	
	static{
		//加载spring配置
		APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(SPRING_CONFIG_PATH);
	}

	static {
		Resource res = new ClassPathResource("param.properties");
		try (InputStream is = res.getInputStream();) {
			paramProp.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final int LOCAL_PROXY_CODE = Integer.parseInt(Constant.paramProp.getProperty("PROXY_CODE"));
	
	public static enum ProxyEnum {
		
		PROXY01{
			@Override
			public int getProxyCode() {
				return Constant.PROXY01_CODE;
			}
			
			@Override
			public String getProxyName() {
				return "PROXY01";
			}
		},
		
		PROXY02{
			@Override
			public int getProxyCode() {
				return Constant.PROXY02_CODE;
			}
			
			@Override
			public String getProxyName() {
				return "PROXY02";
			}
		};
		
		public int getProxyCode(){
			throw new AbstractMethodError();
		}
		
		public String getProxyName(){
			throw new AbstractMethodError();
		}
	}
	
}
