package com.liusu.tcp_proxy_mine.lang;

import java.nio.ByteBuffer;

import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;

import org.junit.Test;

public class IntegerTest {

	@Test
	public void testIntegerMaxValue(){
		System.out.println(Integer.MAX_VALUE);
	}
	
	/**
	 *memoryAddress 458096704
	 */
	@Test
	public void test02(){
		System.out.println(-2126315261 & 0xFFFFFFFFL);
	}
	
	/**
	 * memoryAddress 478675008
	 * 
	 */
	@Test
	public void test03(){
		System.out.println(261 & 0xFFFFFFFFL);
	}
	
	@Test
	public void test04(){
		long value = PlatformDependent.directBufferAddress(ByteBuffer.allocate(458096704));
		System.out.println(value);
	}
}
