package com.liusu.tcp_proxy_mine.domain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class MessageWrap implements Serializable{

	private String destChannelID;
	private InetSocketAddress remoteAddress;
	private InetSocketAddress preAddress;
	//存储二进制数据
	private Object message;
	
	
	public static MessageWrap wrapMessageByteArray(String destChannelID,Object message,InetSocketAddress... adresses){
		MessageWrap mes = new MessageWrap();
		mes.setDestChannelID(destChannelID);
		
		if(message instanceof ByteBuf){
			ByteBuf buf = (ByteBuf)message;
			byte[] remain = new byte[buf.readableBytes()];
			buf.readBytes(remain);
			mes.setMessage(remain);
		}else{
			mes.setMessage(message);
		}
		
		mes.setRemoteAddress(adresses[0]);
		if(adresses.length > 1){
			mes.setPreAddress(adresses[1]);
		}
		
		return mes;
	}
	
	public static ByteBuf wrapMessageByteBuf(MessageWrap mess){
		
		if(mess.getMessage() instanceof byte[]){
			return Unpooled.wrappedBuffer((byte[])mess.getMessage());
		}
		
		return (ByteBuf) mess.getMessage();
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
	
	
	@Override
	public String toString() {
		return "MessageWrap [destChannelID=" + destChannelID
				+ ", remoteAddress=" + remoteAddress + ", preAddress="
				+ preAddress + ", message=" + message + "]";
	}
	
	
}
