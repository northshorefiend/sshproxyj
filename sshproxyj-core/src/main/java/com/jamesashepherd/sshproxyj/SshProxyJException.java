/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj;

/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class SshProxyJException extends Exception {
	/**
	 * 
	 * @since 1.0
	 */
	public SshProxyJException() {
	}

	/**
	 * @param message
	 * @since 1.0
	 */
	public SshProxyJException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 * @since 1.0
	 */
	public SshProxyJException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @since 1.0
	 */
	public SshProxyJException(final String message, final Throwable cause) {
		super(message, cause);
	}
}


