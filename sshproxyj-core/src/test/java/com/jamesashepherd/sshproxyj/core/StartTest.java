/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.BeansException;

import com.jamesashepherd.sshproxyj.SshProxyJException;
import com.jamesashepherd.sshproxyj.utils.KeyUtils;
import com.jamesashepherd.start.StartException;

/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class StartTest {

	private final class StringBuilderOutputStream extends OutputStream {
		private final StringBuilder sb;

		private StringBuilderOutputStream(StringBuilder sb) {
			this.sb = sb;
		}

		@Override
		public void write(int arg0) throws IOException {
			String s = new String(new byte[] { (byte) arg0 }, "UTF-8");
			sb.append(s);
		}
	}

	private final class StringInputStream extends InputStream {
		private final String command;
		byte[] commandBytes;
		int at = 0;

		private StringInputStream(String command) {
			this.command = command;
			commandBytes = command.getBytes();
		}

		@Override
		public int read() throws IOException {
			return at < commandBytes.length ? commandBytes[at++] : -1;
		}
	}

	private static KeyPair keyPair;

	private static KeyPair keyPair2;

	private List<File> toDelete;
	private File home;

	@BeforeClass
	static public void setLogging() throws SshProxyJException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		System.setProperty("org.slf4j.simpleLogger.log.com.jamesashepherd",
				"debug");
		keyPair = KeyUtils.makeKeyPair(UtilsTest.testPublicKey(),
				UtilsTest.testPrivateKey());
		keyPair2 = KeyUtils.makeKeyPair(UtilsTest.test2PublicKey(),
				UtilsTest.test2PrivateKey());
	}

	@Before
	public void setupTmp() throws IOException {
		toDelete = new ArrayList<File>();

		home = new File("/tmp/start-test-" + UUID.randomUUID());
		home.mkdir();
		toDelete.add(home);

		File confDir = new File(home, "conf");
		confDir.mkdir();
		toDelete.add(confDir);

		for (String file : new String[] { "spring.xml", "spring-echo.xml",
				"spring-passthru.xml" }) {
			File conf = new File(confDir, file);
			FileOutputStream fos = new FileOutputStream(conf);
			InputStream is = getClass().getResourceAsStream(file);
			IOUtils.copy(is, fos);
			toDelete.add(conf);
		}
	}

	@Test(timeout = 30000)
	public void springStartup() throws StartException {
		Properties p = new Properties();
		p.setProperty(Start.SPRING_XML_PROPERTY, "conf/spring.xml");
		Start s = new Start();
		s.setHome(home);
		s.setProperties(p);
		s.startup();
		assertTrue(s.getApplicationContext().getBean("props", Properties.class) == p);
		assertTrue(s.getApplicationContext().getBean("home", File.class) == home);
		s.shutdown();
	}

	@Test(timeout = 30000)
	public void testEcho() throws StartException, IOException, BeansException,
			NumberFormatException, SshProxyJException, InterruptedException {
		Properties p = new Properties();
		Start s = setupEchoServer(p);

		SshConnectionFactory scf = new SshConnectionFactory();
		scf.startup();
		SshConnection shell = scf.getSshConnection("localhost",
				Integer.parseInt(p.getProperty("server.sshd.port")),
				"testuser", keyPair, "shell");

		String command = "alrkuhliuhaerg\n";
		shell.setIn(new StringInputStream(command));

		StringBuilder sb = new StringBuilder();
		OutputStream out = new StringBuilderOutputStream(sb);
		shell.setOut(out);
		shell.setErr(out);
		shell.open();

		Thread.sleep(1000);

		shell.close();
		scf.shutdown();

		assertEquals(command, sb.toString());

		s.shutdown();
	}

	/**
	 * @since 1.0
	 * @param p
	 * @return
	 * @throws IOException
	 * @throws StartException
	 */
	private Start setupEchoServer(Properties p) throws IOException,
			StartException {
		p.load(getClass().getResourceAsStream("echo.properties"));
		Start s = new Start();
		s.setHome(home);
		s.setProperties(p);
		s.startup();
		return s;
	}

	@Test(timeout = 30000, expected = SshProxyJException.class)
	public void testEchoFail() throws IOException, SshProxyJException,
			StartException {
		Start s = null;
		SshConnection shell = null;
		SshConnectionFactory scf = null;
		try {
			Properties p = new Properties();
			p.load(getClass().getResourceAsStream("echo.properties"));
			s = new Start();
			s.setHome(home);
			s.setProperties(p);
			s.startup();

			scf = new SshConnectionFactory();
			scf.startup();
			shell = scf.getSshConnection("localhost",
					Integer.parseInt(p.getProperty("server.sshd.port")),
					"testuser", keyPair2, "shell");
		} finally {
			if (shell != null)
				shell.close();
			if (s != null)
				s.shutdown();
			if (scf != null)
				scf.shutdown();
		}
	}

	@Test
	// (timeout = 30000)
	public void passThru() throws IOException, StartException, BeansException,
			SshProxyJException, InterruptedException {
		System.err.println("PassThru Started");
		Properties p = new Properties();
		p.load(getClass().getResourceAsStream("passthru.properties"));
		Start s = new Start();
		s.setHome(home);
		s.setProperties(p);
		s.startup();

		MemoryCommandLoggerFactory mclf = s.getApplicationContext().getBean(
				"commandLoggerFactory", MemoryCommandLoggerFactory.class);

		MemoryUserPublicKeyService userService = s.getApplicationContext()
				.getBean("userPublicKeyService",
						MemoryUserPublicKeyService.class);

		userService.addUser("testuser", keyPair2.getPublic());

		MemoryRemoteUserCredentialsService credentialsService = s
				.getApplicationContext().getBean(
						"remoteUserCredentialsService",
						MemoryRemoteUserCredentialsService.class);
		credentialsService.addCredentials("testuser",
				"testuser@localhost:6667", new ProxyCredentials() {

					@Override
					public String getUsername() {
						return "testuser";
					}

					@Override
					public String getRemoteUsername() {
						return "testuser";
					}

					@Override
					public int getRemotePort() {
						return 6667;
					}

					@Override
					public String getRemoteHost() {
						return "localhost";
					}

					@Override
					public KeyPair getKeyPair() {
						return keyPair;
					}

					@Override
					public String getCommand() {
						return "testuser@localhost:6667";
					}
				});

		System.err.println("Starting Echo Server");
		Properties p2 = new Properties();
		Start echo = setupEchoServer(p2);
		System.err.println("Echo Server Started");

		SshConnectionFactory scf = new SshConnectionFactory();
		scf.startup();
		SshConnection shell = scf.getSshConnection("localhost",
				Integer.parseInt(p.getProperty("server.sshd.port")),
				"testuser", keyPair2, "testuser@localhost:6667");

		String command = "alrkuhliuhaerg\n";
		shell.setIn(new StringInputStream(command));

		StringBuilder sb = new StringBuilder();
		OutputStream out = new StringBuilderOutputStream(sb);
		shell.setOut(out);
		shell.setErr(out);
		shell.open();

		Thread.sleep(1000);

		shell.close();
		scf.shutdown();

		assertEquals(command, sb.toString());

		assertArrayEquals(new String[] {"--STARTING--", "alrkuhliuhaerg", "--ENDING--"}, mclf.getLastLog().toArray(new String[0]));

		echo.shutdown();
		s.shutdown();
	}

	@After
	public void rmTmp() {
		while (!toDelete.isEmpty()) {
			int i = toDelete.size() - 1;
			File f = toDelete.get(i);
			f.delete();
			toDelete.remove(i);
		}
	}
}
