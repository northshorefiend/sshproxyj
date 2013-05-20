/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
@RunWith(Parameterized.class)
public class UserTest {
	final Logger logger = LoggerFactory.getLogger(UserTest.class);
	private String username;

	public UserTest(String username) {
		this.username = username;
	}

	@Test(expected = SshProxyJException.class)
	public void usernameTest1() throws SshProxyJException {
		User u = new User();
		u.setUser(this.username);
	}

	@Parameterized.Parameters
	public static Collection failingUsernames() {
		return Arrays.asList(new Object[][] { { "1user" }, { "user2@" },
				{ "User" } });
	}
}
