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
	private int blen = 0;
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
	synchronized public void log(byte[] bytes, int start, int len) {
		int end = 0;
		int off = start;
		for (int i = off; i < start + len; i++) {
			if (buffer.length == end + 2 - off) {
				System.arraycopy(bytes, off, buffer, blen, end + 2 - off);
				logBuffer(buffer, 0, buffer.length, bytes[i] == nl);
				off = i + 1;
				blen = 0;
				logger.debug("BUFFER FULL");
			} else {
				if (bytes[i] != nl) {
					end = i;
				} else {
					if (end - off > 0) {
						System.arraycopy(bytes, off, buffer, blen, end + 1 - off);
						buffer[blen] = nl;
						logBuffer(buffer, 0, blen + 1, true);
						logger.debug("LINE ENDED");
						off = i + 1;
						blen = 0;
					} else {
						logBuffer(new byte[] { nl }, 0, 1, true);
					}
				}
			}
		}
		int left = end + 1 - off;
		if (left > 0) {
			logger.debug("ADDING TO BUFFER: {}", left);
			System.arraycopy(bytes, off, buffer, blen, left);
			blen += left;
		}
	}

	void logBuffer(byte[] b, int off, int len, boolean isEnd) {
		try {
			String s = new String(b, off, len, "UTF-8");
			logger.debug("LOGGING {}", s);
			buffers.add(s);
		} catch (UnsupportedEncodingException e) {
			logger.info("UTF-8", e);
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
			if (blen > 0) {
				try {
					buffers.add(new String(buffer, 0, blen, "UTF-8"));
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
