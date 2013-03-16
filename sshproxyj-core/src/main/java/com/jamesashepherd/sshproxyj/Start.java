//
package com.jamesashepherd.sshproxyj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.LinkedList;
import java.util.List;

import org.apache.mina.util.Base64;
import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.SshServer;
import org.apache.sshd.client.future.OpenFuture;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.future.SshFutureListener;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.codehaus.plexus.util.IOUtil;

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

	private SshClient client;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.start.Startable#startup()
	 */
	public void startup() throws StartException {

		clientSessions = new LinkedList<ClientSession>();
		
		// set up client
		client = SshClient.setUpDefaultClient();
		PublicKey publicKey = null;
		KeyPair kp = null;
		try {
			InputStream is = new FileInputStream(new File("/tmp/id_rsa.pub"));
			publicKey = decodePublicKey(IOUtil.toString(is));

			BufferedReader br = new BufferedReader(
					new FileReader("/tmp/id_rsa"));
			Security.addProvider(new BouncyCastleProvider());
			PEMReader pr = new PEMReader(br);
			kp = (KeyPair) pr.readObject();
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final KeyPair keyPair = new KeyPair(publicKey, kp.getPrivate());

		client.start();

		// set up server
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(6667);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
				"/tmp/host.key"));
		// sshd.setShellFactory(new ProcessShellFactory(new String[] {
		// "/bin/bash", "-i", "-l" }));
		sshd.setShellFactory(new Factory<Command>() {

			ExitCallback exitCallBack;

			public Command create() {
				try {
					final ClientSession session = client
							.connect("localhost", 22).await().getSession();
					clientSessions.add(session);
					System.out.println("Started Client Session");
					session.authPublicKey("jas", keyPair);

					int ret = session.waitFor(ClientSession.CLOSED
							| ClientSession.AUTHED, 10 * 1000); // milliseconds
					System.out.println("Waited for auth: " + ret);
					if ((ret & ClientSession.CLOSED) != 0) {
						System.err.println("error session closed");
						System.exit(-1);
					}

					System.out.println("Still open");

					final ClientChannel channel = session
							.createChannel("shell");
					System.out.println("Returning Command");

					return new Command() {

						public void setInputStream(InputStream in) {
							channel.setIn(in);
						}

						public void setOutputStream(OutputStream out) {
							channel.setOut(out);
						}

						public void setErrorStream(OutputStream err) {
							channel.setErr(err);
						}

						public void setExitCallback(ExitCallback callback) {
							exitCallBack = callback;
						}

						public void start(Environment env) throws IOException {
							try {
								channel.open();
								new Thread(new Runnable() {

									public void run() {
										channel.waitFor(ClientChannel.CLOSED, 0);
										exitCallBack.onExit(channel.getExitStatus() == null ? 1 : channel.getExitStatus());
										session.close(false);
									}
									
								}).start();
							} catch (Exception e) {
								throw new IOException(e);
							}
						}

						public void destroy() {
							try {
								session.close(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					};
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});

		sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {

			public boolean authenticate(String username, PublicKey key,
					ServerSession session) {
				PublicKey publicKey;
				try {
					InputStream is = new FileInputStream(new File(
							"/tmp/id_rsa.pub"));
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
			for(ClientSession session : clientSessions) {
				session.close(false);
			}
			client.stop();
			sshd.stop();
		} catch (InterruptedException e) {
			throw new StartException("Failed to stop SshServer", e);
		}
	}

	private List<ClientSession> clientSessions;
	
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
