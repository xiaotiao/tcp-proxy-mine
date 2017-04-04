package com.liusu.tcp.proxy.mine;

import java.util.regex.Pattern;

import org.junit.Test;

public class PatternTest {

	/**
	 * 鍖归厤鏌愪竴瀛楃涓�
	 */
	@Test
	public void testPattern(){
		Pattern pattern = Pattern.compile("9999");
		
		System.out.println(pattern.matcher("999").matches());
	}
	
	
	/**
	 * 鍖归厤闈炶瀛楃涓�
	 */
	@Test
	public void testPattern02(){
		Pattern pattern = Pattern.compile("^(?!(9999))[0-65536]{1,5}");
		
		System.out.println(pattern.matcher("9998").matches());
	}
	
	
	/**
	 * 鍖归厤鏌愪釜鑼冨洿鐨勬暟瀛�
	 */
	@Test
	public void testPattern03(){
		Pattern pattern = Pattern.compile("(?:6553[0-6]|655[012][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|999[0-8]|99[0-8][0-9]|9[0-8][0-9]{2}|[1-8][0-9]{0,})");
		
		System.out.println(pattern.matcher("1").matches());
	}
	
	
	
}
