//
package com.jamesashepherd.sshproxyj;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Arrays;
import java.io.FileInputStream;

import org.apache.mina.util.Base64;
import org.apache.sshd.SshServer;
import org.apache.sshd.client.auth.UserAuthPublicKey;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.codehaus.plexus.util.IOUtil;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.start.Startable#startup()
	 */
	public void startup() throws StartException {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(6667);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
				"/tmp/host.key"));
		sshd.setShellFactory(new ProcessShellFactory(new String[] {
				"/bin/bash", "-i", "-l" }));
		// sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
		//
		// public boolean authenticate(String username, String password,
		// ServerSession session) {
		// return username.equals("jas") && password.equals("mega");
		// }
		// });

		sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {

			public boolean authenticate(String username, PublicKey key,
					ServerSession session) {
				PublicKey publicKey;
				try {
					InputStream is = new FileInputStream(new File("/tmp/id_rsa.pub"));
					publicKey = decodePublicKey(IOUtil.toString(is));
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				return ((RSAPublicKey) publicKey).getModulus().equals(
						((RSAPublicKey) key).getModulus());
			}
		});

		try {
			sshd.start();
		} catch (IOException e) {
			throw new StartException("Failed to start SshServer", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.start.Startable#shutdown()
	 */
	public void shutdown() throws StartException {
		try {
			sshd.stop();
		} catch (InterruptedException e) {
			throw new StartException("Failed to stop SshServer", e);
		}
	}

	private byte[] bytes;

	private int pos;

	public PublicKey decodePublicKey(String keyLine) throws Exception {
		bytes = null;
		pos = 0;

		for (String part : keyLine.split(" ")) {
			if (part.startsWith("AAAA")) {
				bytes = Base64.decodeBase64(part.getBytes());
				break;
			}
		}
		if (bytes == null) {
			throw new IllegalArgumentException("no Base64 part to decode");
		}

		String type = decodeType();
		if (type.equals("ssh-rsa")) {
			BigInteger e = decodeBigInt();
			BigInteger m = decodeBigInt();
			RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		} else if (type.equals("ssh-dss")) {
			BigInteger p = decodeBigInt();
			BigInteger q = decodeBigInt();
			BigInteger g = decodeBigInt();
			BigInteger y = decodeBigInt();
			DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
			return KeyFactory.getInstance("DSA").generatePublic(spec);
		} else {
			throw new IllegalArgumentException("unknown type " + type);
		}
	}

	private String decodeType() {
		int len = decodeInt();
		String type = new String(bytes, pos, len);
		pos += len;
		return type;
	}

	private int decodeInt() {
		return ((bytes[pos++] & 0xFF) << 24) | ((bytes[pos++] & 0xFF) << 16)
				| ((bytes[pos++] & 0xFF) << 8) | (bytes[pos++] & 0xFF);
	}

	private BigInteger decodeBigInt() {
		int len = decodeInt();
		byte[] bigIntBytes = new byte[len];
		System.arraycopy(bytes, pos, bigIntBytes, 0, len);
		pos += len;
		return new BigInteger(bigIntBytes);
	}
}
