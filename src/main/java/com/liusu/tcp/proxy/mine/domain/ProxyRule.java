package com.liusu.tcp.proxy.mine.domain;


import java.util.regex.Pattern;

public class ProxyRule {
 
	private Pattern pattern;
	
	public ProxyRule(Pattern pattern) {
		this.pattern = pattern;
	}

	public boolean isValid(int port){
		
		return  pattern.matcher(Integer.toString(port)).matches();
	}
	
	public static void main(String[] args) {
//		ProxyRule rule = new ProxyRule(Pattern.compile("1234"));
//		System.out.println(rule.isValid(msg));
		
		boolean flag = Pattern.compile("^/d+$").matcher("1234").find();
		System.out.println(flag);
	}
}
