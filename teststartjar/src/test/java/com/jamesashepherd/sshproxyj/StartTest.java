package com.jamesashepherd.sshproxyj;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import com.jamesashepherd.start.StartException;

/**
 * Unit test for simple Start.
 */
public class StartTest {
	
	@Rule
	public final StandardOutputStreamLog log = new StandardOutputStreamLog();
	
	@Test
	public void runs() throws StartException {
		Start s = new Start();
		s.startup();
		assertEquals("123456789 Started qwertyuiop\n", log.getLog());
	}
}
