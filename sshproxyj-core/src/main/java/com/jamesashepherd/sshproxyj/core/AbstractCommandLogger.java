/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public abstract class AbstractCommandLogger implements CommandLogger {
	final Logger logger = LoggerFactory.getLogger(AbstractCommandLogger.class);
	private final byte nl = (byte) '\n';
	private byte[] buffer;
	private int blen = 0;
	private boolean hasEnded = false;

	/**
	 * 
	 * @since 1.0
	 * @param bufferLength
	 *            buffer capacity, logBuffer is called when the buffer is full
	 *            or a newline is found
	 */
	AbstractCommandLogger(int bufferLength) {
		buffer = new byte[bufferLength];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.sshproxyj.core.CommandLogger#logStart()
	 */
	@Override
	abstract public void logStart();

	/**
	 * This flushes the buffer to {@link #logBuffer}. You may want to override
	 * to do your own finalisation code, but call this using super.logEnd().
	 */
	@Override
	synchronized public void logEnd() {
		if (!hasEnded) {
			if (blen > 0) {
				logBuffer(buffer, 0, blen, false);
			}
		}
		hasEnded = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.sshproxyj.core.CommandLogger#log(byte[], int,
	 * int)
	 */
	@Override
	public synchronized void log(byte[] bytes, int start, int len) {
		int end = 0;
		int off = start;
		for (int i = off; i < start + len; i++) {
			if (buffer.length == end + 2 - off) {
				System.arraycopy(bytes, off, buffer, blen, end + 2 - off);
				logBuffer(buffer, 0, buffer.length, bytes[i] != nl);
				off = i + 1;
				blen = 0;
				logger.debug("BUFFER FULL");
			} else {
				if (bytes[i] != nl) {
					end = i;
				} else {
					if (end - off > 0) {
						System.arraycopy(bytes, off, buffer, blen, end + 1
								- off);
						buffer[blen] = nl;
						logBuffer(buffer, 0, blen + 1, true);
						logger.debug("LINE ENDED");
						off = i + 1;
						blen = 0;
					} else {
						logBuffer(new byte[] { nl }, 0, 1, false);
					}
				}
			}
		}
		int left = end + 1 - off;
		if (left > 0) {
			logger.debug("ADDING TO BUFFER: {}", left);
			System.arraycopy(bytes, off, buffer, blen, left);
			blen += left;
		}
	}

	/**
	 * Called when there is a complete buffer of data to log, or a newline is
	 * found, or the end of the connection is reached and the buffer is flushed
	 * 
	 * @since 1.0
	 * @param b
	 * @param off
	 * @param len
	 * @param isContinued
	 *            if this log line is not complete and continues to the next one
	 */
	abstract protected void logBuffer(byte[] b, int off, int len, boolean isContinued);
}
