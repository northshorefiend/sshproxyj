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

	/**
	 * 
	 * @since 1.0
	 * @param host
	 * @param port
	 * @param username
	 * @param keyPair
	 * @param command shell for a shell, other for sending a command
	 * @return
	 * @throws SshProxyJException
	 */
	public SshShell getSshShell(String host, int port, String username,
			KeyPair keyPair, String command) throws SshProxyJException {
		try {
			ClientSession session = getSshClient().connect(host, port).await()
					.getSession();
			if(session.authPublicKey(username, keyPair).await().isFailure()) {
				throw new SshProxyJException("Immediately failed to authenticate: "
						+ username + "@" + host + ":" + port);
			}

			ClientChannel channel = command.equals("shell") ? session
					.createChannel("shell") : session
					.createExecChannel(command);
			getClientSessions().add(session);
			return new SshShell(session, channel, getClientSessions());
		} catch (InterruptedException e) {
			throw new SshProxyJException("Failed to start session", e);
		} catch (Exception e) {
			throw new SshProxyJException("Failed to start session", e);
		}
	}
}
