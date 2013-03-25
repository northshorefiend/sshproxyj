/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.security.KeyPair;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
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
public class SshConnectionFactory implements Startable {
	final Logger logger = LoggerFactory.getLogger(SshConnectionFactory.class);
	private SshClient client;
	
	public SshConnectionFactory() {
		client = SshClient.setUpDefaultClient();
	}
	
	/**
	 * 
	 * @since 1.0
	 * @param host
	 * @param port
	 * @param username
	 * @param keyPair
	 * @param command
	 *            "shell" for a shell, other for sending a command
	 * @return
	 * @throws SshProxyJException
	 */
	public SshConnection getSshShell(String host, int port, String username,
			KeyPair keyPair, String command) throws SshProxyJException {
		try {
			ClientSession session = client.connect(host, port).await()
					.getSession();
			if (session.authPublicKey(username, keyPair).await().isFailure()) {
				throw new SshProxyJException("Failed to authenticate: "
						+ username + "@" + host + ":" + port);
			}

			ClientChannel channel = command.equals("shell") ? session
					.createChannel("shell") : session
					.createExecChannel(command);
			return new SshConnection(session, channel);
		} catch (InterruptedException e) {
			throw new SshProxyJException("Failed to start session", e);
		} catch (Exception e) {
			throw new SshProxyJException("Failed to start session", e);
		}
	}

	@Override
	public void startup() throws StartException {
		client.start();
	}

	@Override
	public void shutdown() throws StartException {
		client.stop();
	}
	
}


