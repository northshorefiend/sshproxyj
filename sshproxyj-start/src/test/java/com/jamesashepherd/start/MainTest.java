package com.jamesashepherd.start;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class MainTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Rule
	public final StandardOutputStreamLog log = new StandardOutputStreamLog();

	List<File> toDelete;
	File home;
	static Properties startProperties;
	Pattern startoutput = Pattern.compile("^123456789 Started qwertyuiop$",
			Pattern.MULTILINE);

	@BeforeClass
	public static void loadProperties() throws IOException {
		InputStream is = MainTest.class.getResourceAsStream("start.properties");

		startProperties = new Properties();
		startProperties.load(is);
	}

	@Before
	public void setupTmp() throws IOException {
		toDelete = new ArrayList<File>();

		home = new File("/tmp/start-test-" + UUID.randomUUID());
		home.mkdir();
		toDelete.add(home);

		System.setProperty(startProperties.getProperty("home.dir.property"),
				home.getAbsolutePath());

		File lib = new File(home, "lib");
		lib.mkdir();
		toDelete.add(lib);

		File jar = new File(lib, "teststart.jar");
		FileOutputStream fos = new FileOutputStream(jar);
		InputStream is = getClass().getResourceAsStream(
				"teststartjar-1.0-SNAPSHOT.jar");
		IOUtils.copy(is, fos);
		toDelete.add(jar);

		File needle = new File(home,
				startProperties.getProperty("home.search.file"));
		needle.createNewFile();
		toDelete.add(needle);
	}

	@Test
	public void helpOutput() {
		exit.expectSystemExitWithStatus(0);
		Main.main(new String[] { "--help" });
		assertEquals(
				"usage: java [-Dstart.port=p] [-Dstart.code=c] [-Dstart.code.file=f] -jar X.jar [OPTIONS]\n"
						+ "\n"
						+ "OPTIONS:\n"
						+ "\n"
						+ "    < empty >                                 same as --runonly .\n"
						+ "\n"
						+ "    --help                                    display this help text.\n"
						+ "\n"
						+ "    --runonly                                 do not start port listener, just run application.\n"
						+ "\n"
						+ "    --startup [/path/to/start.properties]     startup service,\n"
						+ "                                              listening on 127.0.0.1 on port p,\n"
						+ "                                              with auth code c for shutdown message,\n"
						+ "                                              optionally overriding defaults with given properties file,\n"
						+ "                                              optionally outputting the generated code in file f.\n"
						+ "\n"
						+ "    --shutdown                                shutdown service\n"
						+ "                                              contacting on 127.0.0.1 on port p,\n"
						+ "                                              with auth code c.\n",
				log.getLog());
	}

	@Test
	public void runonly() {
		System.setProperty("teststart.run", "0");
		Main.main(new String[] { "--runonly" });
		assertTrue(findOutput(log.getLog()));
	}

	public boolean findOutput(String output) {
		return startoutput.matcher(output).find();
	}

	@Test
	public void runonlyNoOption() {
		System.setProperty("teststart.run", "0");
		Main.main(new String[0]);
		assertTrue(findOutput(log.getLog()));
	}

	@Test
	public void isValidPort() {
		assertFalse(Main.isValidPort(0));
		assertTrue(Main.isValidPort(1));
		assertTrue(Main.isValidPort(100));
		assertTrue(Main.isValidPort(65535));
		assertFalse(Main.isValidPort(65536));
	}

	@Test
	public void startupWithNoPort() {
		exit.expectSystemExitWithStatus(1);
		System.setProperty("teststart.run", "0");
		System.setProperty("start.port", "0");
		Main.main(new String[] { "--startup" });
	}

	@Test
	public void startup() throws InterruptedException {
		System.setProperty("teststart.run", "1");
		System.setProperty("start.port", "6668");
		System.setProperty("start.code", "ohyeah");
		Thread server = new Thread(new Runnable() {

			public void run() {
				Main.main(new String[] { "--startup" });
			}
		});
		server.start();

		Thread.sleep(2000);
		assertTrue(findOutput(log.getLog()));

		Main.main(new String[] { "--shutdown" });

		Thread.sleep(2000);

		assertFalse(server.isAlive());
	}

	@Test
	public void startupWithoutCode() throws InterruptedException,
			FileNotFoundException, IOException {
		System.setProperty("teststart.run", "1");
		System.setProperty("start.port", "6668");
		System.setProperty("start.code", "");
		File codeFile = new File(home, "start.code");
		System.setProperty("start.code.file", codeFile.getAbsolutePath());

		Thread server = new Thread(new Runnable() {

			public void run() {
				Main.main(new String[] { "--startup" });
			}
		});
		server.start();

		Thread.sleep(2000);

		String stdout = log.getLog();
		assertTrue(findOutput(stdout));

		Pattern p = Pattern.compile("Will listen for code: '([^']*)'");
		Matcher m = p.matcher(stdout);
		assertTrue(m.find());
		String code = m.group(1);

		String writtenCode = IOUtils.toString(new FileInputStream(codeFile));

		System.setProperty("start.code", code + ".");

		Main.main(new String[] { "--shutdown" });

		Thread.sleep(2000);
		
		assertTrue(server.isAlive());
		
		System.setProperty("start.code", code);

		Main.main(new String[] { "--shutdown" });

		Thread.sleep(2000);
		
		assertFalse(server.isAlive());
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
