package com.jamesashepherd.start;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

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

	@BeforeClass
	public static void loadProperties() throws IOException {
		InputStream is = MainTest.class
				.getResourceAsStream("start.properties");

		startProperties = new Properties();
		startProperties.load(is);
	}

	@Before
	public void setupTmp() throws IOException {
		toDelete = new ArrayList<File>();

		home = new File("/tmp/start-test-" + UUID.randomUUID());
		home.mkdir();
		toDelete.add(home);

		File lib = new File(home, "lib");
		lib.mkdir();
		toDelete.add(lib);

		File jar = new File(lib, "teststart.jar");
		FileOutputStream fos = new FileOutputStream(jar);
		InputStream is = getClass().getResourceAsStream(
				"teststartjar-1.0-SNAPSHOT.jar");
		IOUtils.copy(is, fos);
		toDelete.add(jar);

		File needle = new File(home, startProperties.getProperty("home.search.file"));
		needle.createNewFile();
		toDelete.add(needle);
	}

	@Test
	public void helpOutput() {
		Main.main(new String[] { "--help"} );
		assertEquals("usage: java [-Dstart.port=p] [-Dstart.code=c] [-Dstart.code.file=f] -jar X.jar [OPTIONS]\n" + 
				"\n" + 
				"OPTIONS:\n" + 
				"\n" + 
				"    < empty >                                 same as --runonly .\n" + 
				"\n" + 
				"    --help                                    display this help text.\n" + 
				"\n" + 
				"    --runonly                                 do not start port listener, just run application.\n" + 
				"\n" + 
				"    --startup [/path/to/start.properties]     startup service,\n" + 
				"                                              listening on 127.0.0.1 on port p,\n" + 
				"                                              with auth code c for shutdown message,\n" + 
				"                                              optionally overriding defaults with given properties file,\n" + 
				"                                              optionally outputting the generated code in file f.\n" + 
				"\n" + 
				"    --shutdown                                shutdown service\n" + 
				"                                              contacting on 127.0.0.1 on port p,\n" + 
				"                                              with auth code c.\n", log.getLog());
	}

	@Test
	public void testStartup() {
		System.setProperty(startProperties.getProperty("home.dir.property"), home.getAbsolutePath());
		exit.expectSystemExitWithStatus(1);
		Main.main(new String[0]);
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
