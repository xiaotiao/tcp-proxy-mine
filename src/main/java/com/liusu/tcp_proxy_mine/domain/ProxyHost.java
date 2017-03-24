package com.liusu.tcp_proxy_mine.domain;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.liusu.tcp_proxy_mine.App;
import com.liusu.tcp_proxy_mine.base.Constant;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class ProxyHost {
	int localPort;
	String remoteHost;
	int remotePort;

	private static HashMap<Integer, ProxyHost> proxyHosts = new HashMap<Integer, ProxyHost>();

	public static HashMap<Integer, ProxyHost> getProxyHosts() {
		
		return proxyHosts;
	}
	
	
	/**
	 * 
	 * 
	 * @param proxyCode
	 * @return 返回对应代理的代理服务
	 */
	public static HashMap<Integer, ProxyHost> getProxyHosts(int proxyCode) {
		HashMap<Integer, ProxyHost> proxyHosts = new HashMap<Integer, ProxyHost>();
		ArrayList<ProxyHost> hostList = new ArrayList<>();
		if(proxyCode == Constant.PROXY01_CODE){
			hostList = getProxy01HostList();
		}else if(proxyCode == Constant.PROXY02_CODE){
			hostList = getProxy02HostList();
		}
		
		for (ProxyHost host : hostList) {
			proxyHosts.put(host.getLocalPort(), host);
		}
		return proxyHosts;
	}
	
	public static ArrayList<ProxyHost> getProxy01HostList() {
		ArrayList<ProxyHost> proxyHosts = new ArrayList<ProxyHost>();
		List<? extends ConfigObject> hosts = App.getConfig().getObjectList(
				"tcpProxyServer.proxy01_server");
		for (ConfigObject host : hosts) {
			ConfigValue localPort = host.get("localPort");
			ConfigValue remoteHost = host.get("remoteHost");
			ConfigValue remotePort = host.get("remotePort");
			proxyHosts.add(new ProxyHost(Integer.parseInt(localPort.render()),
					(String) remoteHost.unwrapped(), Integer
							.parseInt(remotePort.render())));
		}
		return proxyHosts;
	}
	
	
	public static ArrayList<ProxyHost> getProxy02HostList() {
		ArrayList<ProxyHost> proxyHosts = new ArrayList<ProxyHost>();
		List<? extends ConfigObject> hosts = App.getConfig().getObjectList(
				"tcpProxyServer.proxy02_server");
		for (ConfigObject host : hosts) {
			ConfigValue localPort = host.get("localPort");
			ConfigValue remoteHost = host.get("remoteHost");
			ConfigValue remotePort = host.get("remotePort");
			proxyHosts.add(new ProxyHost(Integer.parseInt(localPort.render()),
					(String) remoteHost.unwrapped(), Integer
							.parseInt(remotePort.render())));
		}
		return proxyHosts;
	}
	
	public static ArrayList<ProxyHost> getProxyHostList(){
		ArrayList<ProxyHost> proxyHosts = new ArrayList<ProxyHost>();
		List<? extends ConfigObject> hosts = App.getConfig().getObjectList(
				"tcpProxyServer.proxy_server");
		for (ConfigObject host : hosts) {
			ConfigValue localPort = host.get("localPort");
			ConfigValue remoteHost = host.get("remoteHost");
			ConfigValue remotePort = host.get("remotePort");
			proxyHosts.add(new ProxyHost(Integer.parseInt(localPort.render()),
					(String) remoteHost.unwrapped(), Integer
							.parseInt(remotePort.render())));
		}
		return proxyHosts;
	}
	
	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public ProxyHost(int localPort, String remoteHost, int remotePort) {
		super();
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
}
