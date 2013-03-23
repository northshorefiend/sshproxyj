/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.start.StartException;
import com.jamesashepherd.start.Startable;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class Start implements Startable {
	final Logger logger = LoggerFactory.getLogger(Start.class);

	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#startup()
	 */
	@Override
	public void startup() throws StartException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#shutdown()
	 */
	@Override
	public void shutdown() throws StartException {
		// TODO Auto-generated method stub

	}
}


