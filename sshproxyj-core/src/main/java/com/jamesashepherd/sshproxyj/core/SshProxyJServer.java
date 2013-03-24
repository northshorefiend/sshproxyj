/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.IOException;
import java.security.KeyPair;
import java.util.List;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;
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
	private List<ClientSession> clientSessions;
	private int connectTimeout;

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
			for (ClientSession session : getClientSessions()) {
				session.close(false);
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

	public List<ClientSession> getClientSessions() {
		return clientSessions;
	}

	public void setClientSessions(List<ClientSession> clientSessions) {
		this.clientSessions = clientSessions;
	}

	public SshShell getSshShell(String host, int port, String username,
			KeyPair keyPair) throws SshProxyJException {
		ClientSession session;
		ClientChannel channel;
		try {
			session = getSshClient().connect(host, port).await().getSession();
			getClientSessions().add(session);
			session.authPublicKey(username, keyPair);

			int ret = session.waitFor(ClientSession.CLOSED
					| ClientSession.AUTHED, getConnectTimeoutSeconds() * 1000);

			if ((ret & ClientSession.CLOSED) != 0) {
				throw new SshProxyJException("Failed to connect to: "
						+ username + "@" + host + ":" + port);
			}

			channel = session.createChannel("shell");
		} catch (InterruptedException e) {
			throw new SshProxyJException("Failed to start session", e);
		} catch (Exception e) {
			throw new SshProxyJException("Failed to start session", e);
		}

		return new SshShell(session, channel, getClientSessions());
	}

	public int getConnectTimeoutSeconds() {
		return connectTimeout;
	}

	public void setConnectTimeoutSeconds(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
}
