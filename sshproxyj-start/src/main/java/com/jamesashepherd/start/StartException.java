/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;

/**
 * Exception thrown during server startup. If one of these is
 * thrown, then the server cannot start.
 *
 *
 * @author James A. Shepherd
 * @since 1.0
 * 
 */
public class StartException extends Exception {

	/**
	 * @since 1.0
	 */
	private static final long serialVersionUID = 9014223173414832304L;

	/**
	 * 
	 * @since 1.0
	 */
	public StartException() {
	}

	/**
	 * @param message
	 * @since 1.0
	 */
	public StartException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 * @since 1.0
	 */
	public StartException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @since 1.0
	 */
	public StartException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
