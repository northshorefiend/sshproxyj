package com.jamesashepherd.sshproxyj.core;

/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class ProxySession {
	final Logger logger = LoggerFactory.getLogger(ProxySession.class);
	
	private ClientSession session;
	private ClientChannel channel;
	private ProxyCredentials credentials;
	
	public ClientSession getClientSession() {
		return session;
	}

	public void setClientSession(ClientSession session) {
		this.session = session;
	}

	public ClientChannel getClientChannel() {
		return channel;
	}

	public void setClientChannel(ClientChannel channel) {
		this.channel = channel;
	}

	public ProxyCredentials getProxyCredentials() {
		return credentials;
	}

	public void setProxyCredentials(ProxyCredentials credentials) {
		this.credentials = credentials;
	}
}
