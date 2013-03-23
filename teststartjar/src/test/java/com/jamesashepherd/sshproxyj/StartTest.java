/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import com.jamesashepherd.start.StartException;

/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class StartTest {
	
	@Rule
	public final StandardOutputStreamLog log = new StandardOutputStreamLog();
	
	@Test
	public void runs() throws StartException {
		Start s = new Start();
		Properties p = new Properties();
		p.setProperty("output.string", "123456789 Started qwertyuiop");
		s.setProperties(p);
		s.startup();
		assertEquals("123456789 Started qwertyuiop\n", log.getLog());
	}
}
