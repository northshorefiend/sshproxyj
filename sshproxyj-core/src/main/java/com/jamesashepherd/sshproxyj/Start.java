//
package com.jamesashepherd.sshproxyj;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

import org.apache.sshd.SshServer;
import org.apache.sshd.client.auth.UserAuthPublicKey;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
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
	public void startup() throws StartException {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(6667);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("/tmp/host.key"));
		sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/bash", "-i", "-l" }));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			
			public boolean authenticate(String username, String password, ServerSession session) {
				return username.equals("jas") && password.equals("mega");
			}
		});
		try {
			sshd.start();
		} catch (IOException e) {
			throw new StartException("Failed to start SshServer", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jamesashepherd.start.Startable#shutdown()
	 */
	public void shutdown() throws StartException {
		try {
			sshd.stop();
		} catch (InterruptedException e) {
			throw new StartException("Failed to stop SshServer", e);
		}
	}
}


