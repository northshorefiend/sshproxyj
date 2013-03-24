/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.security.PublicKey;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class MemoryUserPublicKeyService implements UserPublicKeyService {
	final Logger logger = LoggerFactory.getLogger(MemoryUserPublicKeyService.class);
	private HashMap<String, PublicKey> map = new HashMap<String, PublicKey>();

	/* (non-Javadoc)
	 * @see com.jamesashepherd.sshproxyj.core.UserPublicKeyService#lookupUser(java.lang.String)
	 */
	@Override
	public PublicKey lookupUser(String username) throws SshProxyJException {
		return map.get(username);
	}
	
	public void addUser(String username, PublicKey publicKey) {
		map.put(username, publicKey);
	}
}


