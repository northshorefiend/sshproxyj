/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

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

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class ConfigureTest {
	final Logger logger = LoggerFactory.getLogger(ConfigureTest.class);
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
	public void configureTest() throws SshProxyJException {
		Session session = sessionFactory.openSession();
		
		User u1 = new User();
		u1.setUser("user1");
		
	}
	
	@After
	public void tearDown() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
}
