/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.IOException;
import java.util.Set;

import org.apache.sshd.SshClient;
import org.apache.sshd.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.start.StartException;
import com.jamesashepherd.start.Startable;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class SshProxyJServer implements Startable {
	final Logger logger = LoggerFactory.getLogger(SshProxyJServer.class);
	private SshClient client;
	private SshServer sshd;
	private Set<ProxySession> proxySessions;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.start.Startable#startup()
	 */
	@Override
	public void startup() throws StartException {
		getSshClient().start();
		try {
			getSshServer().start();
		} catch (IOException e) {
			throw new StartException("Failed to start SSH Server", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.start.Startable#shutdown()
	 */
	@Override
	public void shutdown() throws StartException {
		try {
			for (ProxySession session : getProxySessions()) {
				if (session.getClientSession() != null)
					session.getClientSession().close(false);
			}
			getSshClient().stop();
			getSshServer().stop();
		} catch (InterruptedException e) {
			throw new StartException("Failed to stop SshServer", e);
		}
	}

	public SshClient getSshClient() {
		return client;
	}

	public void setSshClient(SshClient client) {
		this.client = client;
	}

	public SshServer getSshServer() {
		return sshd;
	}

	public void setSshServer(SshServer sshd) {
		this.sshd = sshd;
	}

	public Set<ProxySession> getProxySessions() {
		return proxySessions;
	}

	public void setProxySessions(Set<ProxySession> proxySessions) {
		this.proxySessions = proxySessions;
	}
}
