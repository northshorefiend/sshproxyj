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
public interface RemoteUserCredentialsService {

	/**
	 * 
	 * @since 1.0
	 * @param username
	 * @param command
	 * @return Credentials needed for login or null if denied
	 */
	ProxyCredentials lookupUserCommand(String username, String command);

}
