package com.jamesashepherd.sshproxyj.starttest;

import org.junit.Test;

import com.jamesashepherd.sshproxyj.starttest.Start;
import com.jamesashepherd.start.StartException;

/**
 * Unit test for simple Start.
 */
public class StartTest {
	
	@Test(expected=StartTestException.class)
	public void throwsException() throws StartException {
		Start s = new Start();
		s.startup();
	}
}
