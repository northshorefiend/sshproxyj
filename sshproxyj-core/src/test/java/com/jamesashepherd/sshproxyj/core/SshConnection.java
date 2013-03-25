/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
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
public class SshConnection {
	final Logger logger = LoggerFactory.getLogger(SshConnection.class);
	private ClientSession session;
	private ClientChannel channel;

	public SshConnection(ClientSession session, ClientChannel channel)
			throws SshProxyJException {
		this.session = session;
		this.channel = channel;
	}

	/**
	 * Should be called after setting {@link #setIn(InputStream)}
	 * {@link #setOut(OutputStream)} {@link #setErr(OutputStream)}
	 * 
	 * @since 1.0
	 * @throws SshProxyJException
	 */
	public void open() throws SshProxyJException {
		try {
			this.channel.open();
			new Thread(new Runnable() {

				public void run() {
					channel.waitFor(ClientChannel.CLOSED, 0);
					close();
				}

			}).start();
		} catch (Exception e) {
			throw new SshProxyJException("Failed to open channel", e);
		}
	}

	public void setIn(InputStream in) {
		channel.setIn(in);
	}

	public void setOut(OutputStream out) {
		channel.setOut(out);
	}

	public void setErr(OutputStream err) {
		channel.setErr(err);
	}

	public void close() {
		this.channel.close(false);
		this.session.close(false);
	}

}
