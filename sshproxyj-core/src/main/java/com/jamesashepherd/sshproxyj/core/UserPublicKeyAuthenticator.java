/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.security.PublicKey;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;
import com.jamesashepherd.sshproxyj.utils.KeyUtils;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class UserPublicKeyAuthenticator implements PublickeyAuthenticator {
	final Logger logger = LoggerFactory
			.getLogger(UserPublicKeyAuthenticator.class);

	private UserPublicKeyService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sshd.server.PublickeyAuthenticator#authenticate(java.lang.
	 * String, java.security.PublicKey,
	 * org.apache.sshd.server.session.ServerSession)
	 */
	@Override
	public boolean authenticate(String username, PublicKey key,
			ServerSession session) {
		try {
			PublicKey pk = getUserPublicKeyService().lookupUser(username);
			if (pk != null) {
				logger.debug("Got PublicKey for '{}'", username);
				boolean b = KeyUtils.isSame(pk, key);
				logger.debug("Keys match: {}", b);
				return b;
			}
			logger.debug("Failed to find PublicKey for username '{}'", username);
		} catch (SshProxyJException e) {
			logger.info("Failed to check public key for user '" + username
					+ "'", e);
		}
		return false;
	}

	public UserPublicKeyService getUserPublicKeyService() {
		return userService;
	}

	public void setUserPublicKeyService(UserPublicKeyService userService) {
		this.userService = userService;
	}
}
