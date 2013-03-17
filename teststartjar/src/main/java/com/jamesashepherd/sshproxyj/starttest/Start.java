package com.jamesashepherd.sshproxyj.starttest;

import com.jamesashepherd.start.StartException;
import com.jamesashepherd.start.Startable;

/**
 * Hello world!
 *
 */
public class Start implements Startable
{
	public void startup() throws StartException {
		throw new StartTestException();
	}

	public void shutdown() throws StartException {
	}
}
