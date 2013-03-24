/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class ShellFactoryEcho implements Factory<Command> {
	final Logger logger = LoggerFactory.getLogger(ShellFactoryEcho.class);

	@Override
	public Command create() {
		logger.debug("Creating CommandEcho");
		return new CommandEcho();
	}
}


