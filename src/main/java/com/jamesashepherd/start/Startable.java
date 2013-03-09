/**
 * Copyright 2007 James A. Shepherd
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
 * In theory, {@link #shutdown} could be called before {@link #startup}.
 * 
 * @author James A. Shepherd
 * @version $Id: Startable.java 279 2008-03-04 13:40:18Z jas $
 * @since 0.5
 * 
 */
public interface Startable {

	/**
	 * Entry point for application.
	 * 
	 * @since 0.5
	 */
	void startup() throws StartException;

	/**
	 * <p>Should return when shutdown complete.</p>
	 * 
	 * <p>Once shutdown, implementor will not be restarted</p>
	 * 
	 * @since 0.5
	 * @see Reloadable
	 */
	void shutdown() throws StartException;

}
