package com.jamesashepherd.start;

import java.io.File;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class StarterTest {
	
	@Before
	public void setupTmp() {
		File home = new File("/tmp/start-test-" + UUID.randomUUID());
		home.mkdir();
		File lib = new File(home, "lib");
		lib.mkdir();
		
	}
	
	@Test
	public void testStartup() {
		
	}
}


