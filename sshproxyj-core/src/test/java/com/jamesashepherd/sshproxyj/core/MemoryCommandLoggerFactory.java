/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

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
public class MemoryCommandLoggerFactory implements CommandLoggerFactory {
	final Logger logger = LoggerFactory
			.getLogger(MemoryCommandLoggerFactory.class);
	private List<String> lastLog;
	private int len;
	
	/* (non-Javadoc)
	 * @see com.jamesashepherd.sshproxyj.core.CommandLoggerFactory#createCommandLogger(com.jamesashepherd.sshproxyj.core.ProxyCredentials)
	 */
	@Override
	public CommandLogger createCommandLogger(ProxyCredentials pc) {
		return new MemoryCommandLogger(this, len);
	}

	public List<String> getLastLog() {
		return lastLog;
	}

	public void setLastLog(List<String> lastLog) {
		this.lastLog = lastLog;
	}

	@Override
	public void setBufferLength(int len) {
		this.len = len;
	}
}


