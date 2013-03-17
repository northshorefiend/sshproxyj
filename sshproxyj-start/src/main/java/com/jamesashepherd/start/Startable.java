/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;


/**
 * <p>
 * Applications wishing to be started by this package should implement this with
 * a public no arg constructor.
 * </p>
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 * 
 */
public interface Startable {

	/**
	 * Entry point for application.
	 * 
	 * @since 1.0
	 */
	void startup() throws StartException;

	/**
	 * <p>Should return when shutdown complete.</p>
	 * 
	 * <p>Once shutdown, implementor will not be restarted</p>
	 * 
	 * @since 1.0
	 */
	void shutdown() throws StartException;

}
