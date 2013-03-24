/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class UtilsTest {
	final Logger logger = LoggerFactory.getLogger(UtilsTest.class);
	
	/**
	 * @since 1.0
	 * @return
	 * @throws IOException
	 */
	private static String stringFromResource(String resourcePath) throws IOException {
		InputStream is = UtilsTest.class.getResourceAsStream(resourcePath);
		return IOUtils.toString(is);
	}

	static public String testPublicKey() throws IOException {
		return stringFromResource("id_rsa.pub");
	}

	
	static public String testPrivateKey() throws IOException {
		return stringFromResource("id_rsa");
	}
	
	static public String test2PublicKey() throws IOException {
		return stringFromResource("id_rsa2.pub");
	}

	
	static public String test2PrivateKey() throws IOException {
		return stringFromResource("id_rsa2");
	}
}


