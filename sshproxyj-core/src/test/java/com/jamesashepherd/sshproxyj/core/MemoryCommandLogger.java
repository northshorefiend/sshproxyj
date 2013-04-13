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
public class MemoryCommandLogger extends AbstractCommandLogger {
	public final Logger logger = LoggerFactory.getLogger(MemoryCommandLogger.class);
	private ArrayList<String> buffers = new ArrayList<String>();
	private MemoryCommandLoggerFactory factory;
	private boolean hasEnded = false;
	
	MemoryCommandLogger(MemoryCommandLoggerFactory factory, int bufferCapacity) {
		super(bufferCapacity);
		this.factory = factory;
	}

	@Override
	protected void logBuffer(byte[] b, int off, int len, boolean isEnd) {
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
		super.logEnd();
		if (!hasEnded) {
			buffers.add("--ENDING--");
			factory.setLastLog(buffers);
			hasEnded = true;
		}
	}
}
