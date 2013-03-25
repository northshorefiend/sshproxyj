/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.SshClient;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class ProxyCommand implements Command {
	final Logger logger = LoggerFactory.getLogger(ProxyCommand.class);
	private SshClient client;
	private String command;
	private InputStream in;
	private OutputStream out;
	private OutputStream err;
	private ExitCallback exitCallback;
	private RemoteUserCredentialsService credentialsService;
	private ProxySession proxySession;
	private Set<ProxySession> proxySessions;

	public ProxyCommand(String command) {
		this.command = command;
		this.proxySession = new ProxySession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setInputStream(java.io.InputStream)
	 */
	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setOutputStream(java.io.OutputStream)
	 */
	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setErrorStream(java.io.OutputStream)
	 */
	@Override
	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sshd.server.Command#setExitCallback(org.apache.sshd.server
	 * .ExitCallback)
	 */
	@Override
	public void setExitCallback(ExitCallback callback) {
		this.exitCallback = callback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sshd.server.Command#start(org.apache.sshd.server.Environment)
	 */
	@Override
	public void start(Environment env) throws IOException {
		String username = env.getEnv().get(Environment.ENV_USER);

		ProxyCredentials pc = getRemoteUserCredentialsService()
				.lookupUserCommand(username, command);

		proxySession.setProxyCredentials(pc);
		
		if (pc != null) {
			try {
				proxySession.setClientSession(client
						.connect(pc.getRemoteHost(), pc.getRemotePort())
						.await().getSession());
				logger.info("Started Remote Session {}@{}:{} for {}",
						pc.getRemoteUsername(), pc.getRemoteHost(),
						pc.getRemotePort(), pc.getUsername());
				if (proxySession.getClientSession()
						.authPublicKey(pc.getRemoteUsername(), pc.getKeyPair())
						.await().isFailure()) {
					logger.info("Failed to authenticate {}@{}:{} for {}",
							pc.getRemoteUsername(), pc.getRemoteHost(),
							pc.getRemotePort(), pc.getUsername());
					throw new IOException("Failed to authenticate: "
							+ pc.getRemoteUsername() + "@" + pc.getRemoteHost()
							+ ":" + pc.getRemotePort() + " for "
							+ pc.getUsername());
				}

				logger.debug("KeyPair Accepted");

				proxySessions.add(proxySession);

				proxySession.setClientChannel(proxySession.getClientSession()
						.createChannel("shell"));
				proxySession.getClientChannel().setIn(in);
				proxySession.getClientChannel().setOut(out);
				proxySession.getClientChannel().setErr(out);
				proxySession.getClientChannel().open();
				new Thread(new Runnable() {

					public void run() {
						proxySession.getClientChannel().waitFor(
								ClientChannel.CLOSED, 0);
						exitCallback.onExit(proxySession.getClientChannel()
								.getExitStatus() == null ? 1 : proxySession
								.getClientChannel().getExitStatus());
						proxySession.getClientSession().close(false);
						proxySessions.remove(proxySession);
					}

				}).start();
			} catch (InterruptedException e) {
				logger.info("Failed to connect: " + pc.getRemoteUsername()
						+ "@" + pc.getRemoteHost() + ":" + pc.getRemotePort()
						+ " for " + pc.getUsername(), e);
				throw new IOException(
						"Failed to connect: " + pc.getRemoteUsername() + "@"
								+ pc.getRemoteHost() + ":" + pc.getRemotePort()
								+ " for " + pc.getUsername(), e);
			} catch (Exception e) {
				logger.info("Failed to connect: " + pc.getRemoteUsername()
						+ "@" + pc.getRemoteHost() + ":" + pc.getRemotePort()
						+ " for " + pc.getUsername(), e);
				throw new IOException(
						"Failed to connect: " + pc.getRemoteUsername() + "@"
								+ pc.getRemoteHost() + ":" + pc.getRemotePort()
								+ " for " + pc.getUsername(), e);
			} finally {
				logger.debug("Leaving");
			}
			return;
		}
		logger.info("Denied username '{}' command '{}'", username, command);
		exitCallback.onExit(1, "You shall not pass");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#destroy()
	 */
	@Override
	public void destroy() {
		if (proxySession.getClientChannel() != null)
			proxySession.getClientChannel().close(true);
		if (proxySession.getClientSession() != null)
			proxySession.getClientSession().close(true);
	}

	public SshClient getSshClient() {
		return client;
	}

	public void setSshClient(SshClient client) {
		this.client = client;
	}

	public RemoteUserCredentialsService getRemoteUserCredentialsService() {
		return credentialsService;
	}

	public void setRemoteUserCredentialsService(
			RemoteUserCredentialsService credentialsService) {
		this.credentialsService = credentialsService;
	}

	public ProxySession getProxySession() {
		return proxySession;
	}

	public Set<ProxySession> getProxySessions() {
		return proxySessions;
	}

	public void setProxySessions(Set<ProxySession> proxySessions) {
		this.proxySessions = proxySessions;
	}
}
