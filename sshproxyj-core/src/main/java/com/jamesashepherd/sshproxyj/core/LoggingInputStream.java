/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class LoggingInputStream extends InputStream {
	final Logger logger = LoggerFactory.getLogger(LoggingInputStream.class);
	private InputStream in;
	
	public LoggingInputStream(InputStream in) {
		this.in = in;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return in.read();
	}
}


