/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class CommandEcho implements Command {
	final Logger logger = LoggerFactory.getLogger(CommandEcho.class);
	private InputStream in;
	private OutputStream out;
	private OutputStream err;
	private ExitCallback exitCallback;
	private Thread thread;
	private boolean stop = false;

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

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Reader r = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(r);

				Writer w = new OutputStreamWriter(out);
				BufferedWriter bw = new BufferedWriter(w);

				logger.info("Starting");
				while (!getStop()) {
					try {
						logger.debug("Waiting...");
						String line = br.readLine();
						
						if (line == null) {
							logger.debug("Stream closed");
							break;
						}

						logger.debug("Echoing: {}", line);
						bw.write(line);
						bw.write('\n');
						bw.flush();
						w.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				logger.info("Exiting");
				exitCallback.onExit(0);
			}

		});
		
		thread.start();
	}

	synchronized private boolean getStop() {
		return stop;
	}

	synchronized private void setStop(boolean stop) {
		this.stop = stop;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#destroy()
	 */
	@Override
	public void destroy() {
		setStop(true);
		try {
			thread.join(); // could cause a deadlock?
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
