/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.util.List;

import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class ProxyCommandFactory implements CommandFactory {
	final Logger logger = LoggerFactory.getLogger(ProxyCommandFactory.class);
	private SshClient client;
	private List<ClientSession> clientSessions;
	private RemoteUserCredentialsService credentialsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sshd.server.CommandFactory#createCommand(java.lang.String)
	 */
	@Override
	public Command createCommand(String command) {
		ProxyCommand c = new ProxyCommand(command);
		c.setSshClient(getSshClient());
		c.setClientSessions(getClientSessions());
		c.setRemoteUserCredentialsService(getRemoteUserCredentialsService());
		return c;
	}

	public SshClient getSshClient() {
		return client;
	}

	public void setSshClient(SshClient client) {
		this.client = client;
	}

	public List<ClientSession> getClientSessions() {
		return clientSessions;
	}

	public void setClientSessions(List<ClientSession> clientSessions) {
		this.clientSessions = clientSessions;
	}

	public RemoteUserCredentialsService getRemoteUserCredentialsService() {
		return credentialsService;
	}

	public void setRemoteUserCredentialsService(RemoteUserCredentialsService credentialsService) {
		this.credentialsService = credentialsService;
	}
}
