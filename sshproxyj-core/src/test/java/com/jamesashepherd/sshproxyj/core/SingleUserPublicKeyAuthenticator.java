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
public class SingleUserPublicKeyAuthenticator implements PublickeyAuthenticator {
	final Logger logger = LoggerFactory
			.getLogger(SingleUserPublicKeyAuthenticator.class);
	private String username;
	private PublicKey publickey;

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
			return username.equals(this.username)
					&& KeyUtils.isSame(key, publickey);
		} catch (SshProxyJException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPublicKey(PublicKey publickey) {
		this.publickey = publickey;
	}
}
