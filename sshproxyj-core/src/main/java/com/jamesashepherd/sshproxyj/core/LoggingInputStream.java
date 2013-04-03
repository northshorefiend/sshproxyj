/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps an InputStream and sends all data to the CommandLogger.
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class LoggingInputStream extends InputStream {
	final Logger logger = LoggerFactory.getLogger(LoggingInputStream.class);
	private InputStream in;
	private CommandLogger commandLogger;
	private boolean hasStarted = false;

	public LoggingInputStream(InputStream in, CommandLogger commandLogger) {
		this.in = in;
		this.commandLogger = commandLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		checkStarted();
		int b = in.read();
		
		if (b == -1)
			commandLogger.logEnd();
		else
			commandLogger.log(new byte[] { (byte) b });
		
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte b[], int off, int len) throws IOException {
		checkStarted();
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int ret = in.read(b, off, len);

		if (ret == -1)
			commandLogger.logEnd();
		else {
			byte[] b1 = new byte[ret];
			System.arraycopy(b, off, b1, 0, ret);
			commandLogger.log(b1);
		}

		return ret;
	}

	@Override
	public void close() throws IOException {
		checkStarted();
		commandLogger.logEnd();
		in.close();
	}
	
	public void checkStarted() {
		if(!hasStarted) {
			commandLogger.logStart();
			hasStarted = true;
		}
			
	}
}
