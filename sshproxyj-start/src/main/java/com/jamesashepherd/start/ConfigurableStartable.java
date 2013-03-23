/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;

import java.io.File;
import java.util.Properties;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public interface ConfigurableStartable extends Startable {

	/**
	 * 
	 * @since 1.0
	 * @param home home directory
	 */
	void setHome(File home);
	
	/**
	 * 
	 * @since 1.0
	 * @param prop properties
	 */
	void setProperties(Properties prop);
}


