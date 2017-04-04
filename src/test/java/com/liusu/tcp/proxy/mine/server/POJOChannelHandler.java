
package com.liusu.tcp.proxy.mine.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileInputStream;
import java.nio.ByteBuffer;

import com.liusu.tcp.proxy.mine.domain.MessageWrap;

/**
 * @author zzh
 * 
 *  模拟一个简单的输入回送server
 */
public class POJOChannelHandler extends ChannelInboundHandlerAdapter {

	static String content;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("POJOChannelHandler [channelActive]...");
	}
	
	static{
//		try(FileInputStream is = new FileInputStream("E:\\company\\wondersoft\\工作\\工作\\工作簿\\04\\28\\testfile\\50KB.txt")){
//			
//			ByteBuffer buffer = ByteBuffer.allocate(is.available());
//			is.getChannel().read(buffer);
//			
//			buffer.flip();
//			content = new String(buffer.array());
//		} catch (Exception e) {
//			
//		}
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
	    throws Exception {
    	System.out.println("POJOChannelHandler [channelRead] received: "+msg);
    
    	ctx.writeAndFlush(msg);
    	
    }
    

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	  ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
	  ctx.close();
    }
}
