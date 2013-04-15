/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
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
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	@Test
	public void testBasicUsage() throws UnsupportedEncodingException, InterruptedException {
		Session session = sessionFactory.openSession();

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

		Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Thread.sleep(1);
		String[] testStrings = new String[] { "this is a command\n",
				"let's do something else\n" };

		session.beginTransaction();

		session.save(new LogEntry(pc, LogInOut.I));
		for (String s : testStrings) {
			session.save(new LogEntry(pc, s.getBytes("UTF-8"), false));
		}
		Thread.sleep(1);
		session.save(new LogEntry(pc, LogInOut.O));

		Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		session.getTransaction().commit();
		session.close();

		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from LogEntry LE ORDER BY LE.id")
				.list();
		int i = 0;
		for (LogEntry le : (List<LogEntry>) result) {
			System.out.println(le);
			assertTrue(le.getTimestamp().after(start));
			assertTrue(le.getTimestamp().before(end));
			assertEquals("admin", le.getRemoteUsername());
			assertEquals("testuser", le.getUsername());
			assertEquals(22, le.getRemotePort());
			assertEquals("myhost", le.getRemoteHost());
			assertEquals(YesNo.N, le.getContinues());

			if (i < 1) {
				assertEquals(LogInOut.I, le.getLogInOut());
				assertNull(le.getBytes());
			} else if (i <= testStrings.length) {
				assertEquals(LogInOut.N, le.getLogInOut());
				assertEquals(testStrings[i-1], new String(le.getBytes(), "UTF-8"));
			} else {
				assertEquals(LogInOut.O, le.getLogInOut());
				assertNull(le.getBytes());
			}

			i++;
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
