/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
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
			String s = new String(new byte[] { (byte) arg0 });
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

	private List<File> toDelete;
	private File home;

	@BeforeClass
	static public void setLogging() {
		System.setProperty("org.slf4j.simpleLogger.log.com.jamesashepherd",
				"debug");
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

		File conf = new File(confDir, "spring.xml");
		FileOutputStream fos = new FileOutputStream(conf);
		InputStream is = getClass().getResourceAsStream("spring.xml");
		IOUtils.copy(is, fos);
		toDelete.add(conf);

		conf = new File(confDir, "spring-echo.xml");
		fos = new FileOutputStream(conf);
		is = getClass().getResourceAsStream("spring-echo.xml");
		IOUtils.copy(is, fos);
		toDelete.add(conf);
	}

	@Test(timeout=30000)
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

	@Test(timeout=30000)
	public void testEcho() throws StartException, IOException, BeansException,
			NumberFormatException, SshProxyJException, InterruptedException {
		Properties p = new Properties();
		p.load(getClass().getResourceAsStream("echo.properties"));
		Start s = new Start();
		s.setHome(home);
		s.setProperties(p);
		s.startup();

		KeyPair keyPair = KeyUtils.makeKeyPair(UtilsTest.testPublicKey(),
				UtilsTest.testPrivateKey());

		SshShell shell = s
				.getApplicationContext()
				.getBean("sshProxyJServer", SshProxyJServer.class)
				.getSshShell("localhost",
						Integer.parseInt(p.getProperty("server.sshd.port")),
						"testuser", keyPair);

		String command = "alrkuhliuhaerg\n";
		shell.setIn(new StringInputStream(command));

		StringBuilder sb = new StringBuilder();
		OutputStream out = new StringBuilderOutputStream(sb);
		shell.setOut(out);
		shell.setErr(out);
		shell.open();

		Thread.sleep(1000);

		shell.close();

		assertEquals(command, sb.toString());

		s.shutdown();
	}

	@Test(timeout=30000,expected=SshProxyJException.class)
	public void testEchoFail() throws IOException, SshProxyJException, StartException {
		Properties p = new Properties();
		p.load(getClass().getResourceAsStream("echo.properties"));
		Start s = new Start();
		s.setHome(home);
		s.setProperties(p);
		s.startup();

		KeyPair keyPair2 = KeyUtils.makeKeyPair(UtilsTest.test2PublicKey(),
				UtilsTest.test2PrivateKey());

		SshShell shell = s
				.getApplicationContext()
				.getBean("sshProxyJServer", SshProxyJServer.class)
				.getSshShell("localhost",
						Integer.parseInt(p.getProperty("server.sshd.port")),
						"testuser", keyPair2);
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
