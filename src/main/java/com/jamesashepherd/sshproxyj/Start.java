//
package com.jamesashepherd.sshproxyj;

import com.jamesashepherd.start.StartException;
import com.jamesashepherd.start.Startable;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @version $Id$
 * @since 1.9.5
 */
public class Start implements Startable {

	class MyThread extends Thread {
		
		public boolean stop = false;
		
		@Override
		public void run() {
			while(true) {
				System.out.print(".");
				try {
					this.sleep(1000);
					if(this.stop)
						break;
				} catch (InterruptedException e) {
					System.err.println("interrupted");
				}
			}
		}
	}
	
	MyThread t;
	
	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#startup()
	 */
	@Override
	public void startup() throws StartException {
		this.t = new MyThread();
		t.start();
	}

	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#shutdown()
	 */
	@Override
	public void shutdown() throws StartException {
		this.t.stop = true;
	}
}


