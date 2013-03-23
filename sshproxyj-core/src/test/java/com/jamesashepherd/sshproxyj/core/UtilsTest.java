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
	
	static public String testPublicKey() throws IOException {
		InputStream is = UtilsTest.class.getResourceAsStream("id_rsa.pub");
		return IOUtils.toString(is);
	}
}


