/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public interface CommandLoggerFactory {

	CommandLogger createCommandLogger(ProxyCredentials pc);
	
	void setBufferLength(int len);
}
