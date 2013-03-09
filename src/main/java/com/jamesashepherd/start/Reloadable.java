/**
 * Copyright 2007 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;


/**
 * 
 * Adds config relading to {@link Startable}
 * 
 * @author James A. Shepherd
 * @version $Id: Reloadable.java 137 2007-06-11 14:53:58Z jas $
 * @since 0.5
 * 
 */
public interface Reloadable extends Startable {

	/**
	 * Reload config and do anything necessary to reflect changes in
	 * configuration.
	 * 
	 * @throws StartException
	 * 
	 * @since 0.5
	 */
	public void reload() throws StartException;
}
