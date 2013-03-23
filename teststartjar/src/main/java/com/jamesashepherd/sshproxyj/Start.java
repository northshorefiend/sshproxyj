/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj;

import java.io.File;
import java.util.Properties;

import com.jamesashepherd.start.ConfigurableStartable;
import com.jamesashepherd.start.StartException;

/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class Start implements ConfigurableStartable {
	private boolean shutdown = false;
	private File home;
	private Properties props;

	@Override
	public void startup() throws StartException {
		System.out.println(props.getProperty("output.string"));

		if ("1".equals(System.getProperty("teststart.run"))) {
			while (!getShutdown()) {
				try {
					System.err.print(".");
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.err.println();
		}

	}

	synchronized private boolean getShutdown() {
		return shutdown;
	}

	@Override
	synchronized public void shutdown() throws StartException {
		shutdown = true;
	}

	@Override
	public void setHome(File home) {
		this.home = home;
	}

	@Override
	public void setProperties(Properties prop) {
		this.props = prop;
	}
}
