/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.core.ProxyCredentials;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class LogTest {
	final Logger logger = LoggerFactory.getLogger(LogTest.class);

	private SessionFactory sessionFactory;

	@Before
	public void setUp() throws Exception {
		// A SessionFactory is set up once for an application
		sessionFactory = new Configuration().configure() // configures settings
															// from
															// hibernate.cfg.xml
				.buildSessionFactory();
	}

	@Test
	public void testBasicUsage() throws UnsupportedEncodingException {
		// create a couple of events...
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		ProxyCredentials pc = new ProxyCredentials() {

			@Override
			public String getCommand() {
				return "admin@myhost";
			}

			@Override
			public String getUsername() {
				return "testuser";
			}

			@Override
			public String getRemoteUsername() {
				return "admin";
			}

			@Override
			public String getRemoteHost() {
				return "myhost";
			}

			@Override
			public int getRemotePort() {
				return 22;
			}

			@Override
			public KeyPair getKeyPair() {
				return null;
			}
		};
		
		session.save(new LogEntry(pc, LogInOut.I));
		session.save(new LogEntry(pc, "this is a command\n".getBytes("UTF-8"), false));
		session.save(new LogEntry(pc, "let's do something else\n".getBytes("UTF-8"), false));
		session.save(new LogEntry(pc, LogInOut.O));
		
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from LogEntry").list();
		for (LogEntry le : (List<LogEntry>) result) {
			System.err.println(le);
		}
		
		session.getTransaction().commit();
		session.close();
	}

	@After
	public void tearDown() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
}
