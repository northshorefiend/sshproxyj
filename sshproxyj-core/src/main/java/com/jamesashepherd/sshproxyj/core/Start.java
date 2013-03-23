/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.io.File;
import java.net.MalformedURLException;
import java.security.Security;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.jamesashepherd.start.ConfigurableStartable;
import com.jamesashepherd.start.StartException;

/**
 * 
 * Class that gets booted by sshproxyj-start
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class Start implements ConfigurableStartable {
	public static final String SPRING_XML_PROPERTY = "spring.xml";
	final Logger logger = LoggerFactory.getLogger(Start.class);
	private static File home;
	private static Properties props;
	private AbstractApplicationContext context;

	@Override
	public void startup() throws StartException {
		logger.info("sshproxyj starting");
		Security.addProvider(new BouncyCastleProvider());
		context = new FileSystemXmlApplicationContext(getSpringConfigURL());
	}

	private String getSpringConfigURL() throws StartException {
		File f = new File(home, props.getProperty(SPRING_XML_PROPERTY));
		try {
			return f.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			throw new StartException("Failed to get Spring config URL", e);
		}
	}

	@Override
	public void shutdown() throws StartException {
		logger.info("sshproxyj shutting down");
		context.close();
	}

	@Override
	public void setHome(File home) {
		this.home = home;
	}

	public static File getHome() {
		return home;
	}

	@Override
	public void setProperties(Properties prop) {
		this.props = prop;
	}

	public static Properties getProperties() {
		return props;
	}

	public ApplicationContext getApplicationContext() {
		return context;
	}
}
