//
package com.jamesashepherd.sshproxyj;

import java.io.IOException;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;

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
	
	private SshServer sshd;
	
	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#startup()
	 */
	@Override
	public void startup() throws StartException {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(6667);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("/tmp/host.key"));
		sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/bash", "-i", "-l" }));
		try {
			sshd.start();
		} catch (IOException e) {
			throw new StartException("Failed to start SshServer", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#shutdown()
	 */
	@Override
	public void shutdown() throws StartException {
		try {
			sshd.stop();
		} catch (InterruptedException e) {
			throw new StartException("Failed to stop SshServer", e);
		}
	}
}


