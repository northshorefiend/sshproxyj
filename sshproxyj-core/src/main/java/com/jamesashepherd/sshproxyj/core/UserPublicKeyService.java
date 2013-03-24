/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.security.PublicKey;

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public interface UserPublicKeyService {

	/**
	 * 
	 * @since 1.0
	 * @param username
	 * @return PublicKey for this user or null
	 * @throws SshProxyJException
	 */
	PublicKey lookupUser(String username) throws SshProxyJException;
}


