package com.liusu.tcp.proxy.mine.lang;

import org.junit.Test;

import com.liusu.tcp.proxy.mine.base.Constant;
import com.liusu.tcp.proxy.mine.base.Constant.ProxyEnum;

public class EnumTest {

	@Test
	public void testTravel(){
		ProxyEnum[] values = Constant.ProxyEnum.values();
		
		for(ProxyEnum e : values){
			System.out.println(e.getProxyName());
		}
	}
}
