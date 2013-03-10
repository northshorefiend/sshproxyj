/**
 * Copyright 2007 James A. Shepherd
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
 * @version $Id: StartException.java 137 2007-06-11 14:53:58Z jas $
 * @since 0.5
 * 
 */
public class StartException extends Exception {

	/**
	 * @since 0.5
	 */
	private static final long serialVersionUID = 9014223173414832304L;

	/**
	 * 
	 * @since 0.5
	 */
	public StartException() {
	}

	/**
	 * @param message
	 * @since 0.5
	 */
	public StartException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 * @since 0.5
	 */
	public StartException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @since 0.5
	 */
	public StartException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
