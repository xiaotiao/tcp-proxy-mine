package com.liusu.tcp.proxy.mine;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FileTest {

	@Test
	public void testResource(){
		Resource res = new ClassPathResource("reference.conf");
		try(
				InputStream is = res.getInputStream();
				){
			
			byte[] buf = new byte[is.available()];
			is.read(buf);
			
			System.out.println(new String(buf,Charset.forName("UTF-8")));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
