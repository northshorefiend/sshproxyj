package com.jamesashepherd.sshproxyj;

import com.jamesashepherd.start.StartException;
import com.jamesashepherd.start.Startable;

public class Start implements Startable
{
	private boolean shutdown = false;
	
	public void startup() throws StartException {
		System.out.println("123456789 Started qwertyuiop");
		
		if("1".equals(System.getProperty("teststart.run"))) {
			while(!getShutdown()) {
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
	
	synchronized public void shutdown() throws StartException {
		shutdown = true;
	}
}
