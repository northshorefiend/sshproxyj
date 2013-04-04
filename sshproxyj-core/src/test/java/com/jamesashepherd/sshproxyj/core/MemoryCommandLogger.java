/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class MemoryCommandLogger implements CommandLogger {
	final Logger logger = LoggerFactory.getLogger(MemoryCommandLogger.class);
	private ArrayList<String> buffers = new ArrayList<String>();
	final private byte nl = (byte) '\n';
	private byte[] buffer;
	private int len = 0;
	private MemoryCommandLoggerFactory factory;
	private boolean hasEnded = false;

	MemoryCommandLogger(MemoryCommandLoggerFactory factory, int bufferCapacity) {
		this.factory = factory;
		buffer = new byte[bufferCapacity];

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.sshproxyj.core.CommandLogger#log(byte[])
	 */
	@Override
	synchronized public void log(byte[] bytes) {
		int end = 0;
		int off = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != nl)
				end = i;
			else {
				if (end - off > 0) {
					try {
						buffers.add(new String(buffer, 0, len, "UTF-8")
								+ new String(bytes, off, end + 1 - off, "UTF-8")
								+ "\n");
						logger.debug("LINE ENDED");
					} catch (UnsupportedEncodingException e) {
						logger.info("UTF-8", e);
					}

					off = i + 1;
					len = 0;
				} else {
					buffers.add("\n");
				}
			}
		}
		int left = end + 1 - off;
		if (left > 0) {
			logger.debug("ADDING TO BUFFER: {}", left);
			System.arraycopy(bytes, off, buffer, len, left);
			len += left;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.sshproxyj.core.CommandLogger#logStart()
	 */
	@Override
	synchronized public void logStart() {
		buffers.add("--STARTING--");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.sshproxyj.core.CommandLogger#logEnd()
	 */
	@Override
	synchronized public void logEnd() {
		if (!hasEnded) {
			if (len > 0) {
				try {
					buffers.add(new String(buffer, 0, len, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.info("UTF-8", e);
				}
			}
			buffers.add("--ENDING--");
			factory.setLastLog(buffers);
			hasEnded = true;
		}
	}
}
