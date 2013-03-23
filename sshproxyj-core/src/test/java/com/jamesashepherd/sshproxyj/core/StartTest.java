/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import static org.junit.Assert.assertTrue;

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
import org.junit.Test;

import com.jamesashepherd.start.StartException;

/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class StartTest {
	
	private List<File> toDelete;
	private File home;

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
		InputStream is = getClass().getResourceAsStream(
				"spring.xml");
		IOUtils.copy(is, fos);
		toDelete.add(conf);
		
		conf = new File(confDir, "spring-echo.xml");
		fos = new FileOutputStream(conf);
		is = getClass().getResourceAsStream(
				"spring.xml");
		IOUtils.copy(is, fos);
		toDelete.add(conf);
	}
	
	@Test
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

	@Test
	public void singleUser() throws StartException, IOException {
		Properties p = new Properties();
		p.load(getClass().getResourceAsStream("echo.properties"));
		Start s = new Start();
		s.setHome(home);
		s.setProperties(p);
		s.startup();
		
		// TODO create client to connect to 6667 and see if it echos
		
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


