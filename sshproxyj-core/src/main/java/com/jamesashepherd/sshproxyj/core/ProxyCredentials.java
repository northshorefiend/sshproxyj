/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.security.KeyPair;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public interface ProxyCredentials {

	/**
	 * 
	 * @since 1.0
	 * @return Original command requested by the user
	 */
	String getCommand();

	/**
	 * 
	 * @since 1.0
	 * @return Username of sshproxyj user
	 */
	String getUsername();

	/**
	 * 
	 * @since 1.0
	 * @return Username of machine we are proxying to
	 */
	String getRemoteUsername();

	/**
	 * 
	 * @since 1.0
	 * @return Hostname of machine we are proxying to
	 */
	String getRemoteHost();

	/**
	 * 
	 * @since 1.0
	 * @return port on machine we are proxying to
	 */
	int getRemotePort();

	/**
	 * 
	 * @since 1.0
	 * @return KeyPair we are going to use to log in to machine we are proxying
	 *         to
	 */
	KeyPair getKeyPair();
}
