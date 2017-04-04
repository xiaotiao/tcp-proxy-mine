package com.liusu.tcp.proxy.mine.domain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.net.InetSocketAddress;

import javax.swing.text.AbstractDocument.DefaultDocumentEvent;

import com.liusu.tcp.proxy.mine.base.Constant;

@SuppressWarnings("all")
public class MessageWrap implements Serializable{

	private static final int DEFAULT_TYPE = MessageType.Application.getType();
	
	private String destChannelID;
	private InetSocketAddress remoteAddress;
	private InetSocketAddress preAddress;
	//存储二进制数据
	private Object message;
	
	private int type;
	
	public MessageWrap(){
		this.type = DEFAULT_TYPE;
	}
	
	public MessageWrap(String destChannelID,Object message,InetSocketAddress... adresses){
		this.type = DEFAULT_TYPE;
		this.destChannelID = destChannelID;
		this.message = message;
		this.remoteAddress = adresses[0];
		
		if(adresses.length > 1){
			this.preAddress = adresses[1];
		}
	}
	
	
	
//	public static MessageWrap wrapMessageByteArray(String destChannelID,Object message,InetSocketAddress... adresses){
//		MessageWrap mes = new MessageWrap();
//		
//		mes.setDestChannelID(destChannelID);
//		mes.setMessage(message);
//		mes.setRemoteAddress(adresses[0]);
//		
//		if(adresses.length > 1){
//			mes.setPreAddress(adresses[1]);
//		}
//		
//		return mes;
//	}
	
//	public static MessageWrap wrapMessageByteBuf(MessageWrap mess){
//		
////		if(mess.getMessage() instanceof byte[]){
////			return Unpooled.wrappedBuffer((byte[])mess.getMessage());
////		}
////		
////		return (ByteBuf) mess.getMessage();
//		
//		return mess;
//	}
	
	
	/**
	 * 不包含消息
	 * @param channelID
	 * @return
	 */
	public static MessageWrap createEmptyMessageWrap(String channelID,InetSocketAddress localAddress,int type){
		Object msg = Unpooled.wrappedBuffer(new byte[]{});
	
		ProxyHost proxyHost = ProxyHost.getProxyHosts(Constant.PROXY01_CODE).get(localAddress.getPort());
		String remoteHost = proxyHost.getRemoteHost();
		int remotePort = proxyHost.getRemotePort();
		// 包装消息
//		final MessageWrap mess = MessageWrap.wrapMessageByteArray(ctx.channel().id().asShortText(), msg,new InetSocketAddress(remoteHost, remotePort));
		
		MessageWrap mess = new MessageWrap(channelID, msg, new InetSocketAddress(remoteHost, remotePort));
		mess.setType(type); 
		
		return mess;
	}
	
	/**
	 * 定义MessageWrap的类型
	 * 
	 * Active 1
	 * 
	 * Inactive 2
	 * 
	 * Application 3
	 *
	 */
	public enum MessageType{
		Active{
			@Override
			public int getType() {
				return 1;
			}
		},
		
		InActive{
			@Override
			public int getType() {
				return 2;
			}
		},
		
		Application{
			@Override
			public int getType() {
				return 3;
			}
		};
		
		public int getType(){
			 throw new AbstractMethodError();
		}
	}
	
	public String getDestChannelID() {
		return destChannelID;
	}
	public void setDestChannelID(String destChannelID) {
		this.destChannelID = destChannelID;
	}
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public InetSocketAddress getPreAddress() {
		return preAddress;
	}
	public void setPreAddress(InetSocketAddress preAddress) {
		this.preAddress = preAddress;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MessageWrap [destChannelID=" + destChannelID
				+ ", remoteAddress=" + remoteAddress + ", preAddress="
				+ preAddress + ", message=" + message + ", type=" + type + "]";
	}
	
	
}
