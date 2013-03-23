/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/**
 * <p>
 * Sets up classpath and class loader and starts the application.
 * </p>
 * 
 * @author James A. Shepherd
 * @since 1.0
 * 
 */
public class Starter {

	/**
	 * start.properties: class implementing {@link Startable}
	 */
	private static final String START_CLASS = "start.class";

	/**
	 * start.properties: class subclassing {@link Policy}
	 */
	private static final String SECURITY_POLICY = "security.policy";

	/**
	 * start.properties: dir where application jars are found
	 */
	private static final String JAR_DIR = "jar.dir";

	/**
	 * start.properties: file we search for to check we have found home dir
	 */
	private static final String HOME_SEARCH_FILE = "home.search.file";

	/**
	 * start.properties: java system property to set/read with home dir
	 */
	private static final String HOME_DIR_PROPERTY = "home.dir.property";

	/**
	 * start.properties: environment variable that might contain home dir
	 */
	private static final String HOME_DIR_ENV = "home.dir.env";

	/**
	 * start.properties: comma separated list of directories to try to find home
	 * dir
	 */
	private static final String HOME_DIR_SEARCH = "home.dir.search";

	/**
	 * start.properties: path from home to config file
	 */
	private static final String CONFIG_FILE_PROPERTY = "config.file";

	/**
	 * Application ClassLoader
	 */
	private ClassLoader cl = null;

	/**
	 * start.properties file
	 */
	private Properties prop;

	/**
	 * Home dir of application
	 */
	private File home;

	/**
	 * instance of {@link Startable} class of the applications
	 */
	private Startable startable;

	/**
	 * Name of home dir environment variable
	 */
	private String homeEnv;

	/**
	 * Sets up classloader, classpath, finds the {@link Startable} class, using
	 * <code>prop</code>.
	 * 
	 * @param prop
	 *            start.properties
	 * @throws StartException
	 * @since 1.0
	 */
	public Starter(final Properties prop) throws StartException {

		this.prop = prop;
		setHome();
		cl = new URLClassLoader(getJars());
		Thread.currentThread().setContextClassLoader(cl);
		Thread.currentThread().setName("Main");

		findStartable();

		loadPolicy();
	}

	/**
	 * @since 1.0
	 * @param prop
	 * @throws StartException
	 */
	private void findStartable() throws StartException {
		String clazz = null;
		try {
			clazz = prop.getProperty(Starter.START_CLASS);
			final Class c = Class.forName(clazz, true, cl);
			if (!Startable.class.isAssignableFrom(c)) {
				System.err
						.println("ERROR: " + c.getName()
								+ " is not an instance of "
								+ Startable.class.getName());
				System.exit(1);
			}
			if (ConfigurableStartable.class.isAssignableFrom(c)) {
				ConfigurableStartable cs = (ConfigurableStartable) c
						.newInstance();
				configure(cs);
			} else {
				startable = (Startable) c.newInstance();
			}
		} catch (final ClassNotFoundException e) {
			throw new StartException("ERROR: Class " + clazz + " not found", e);
		} catch (final InstantiationException e) {
			throw new StartException("ERROR: Could not instantiate class "
					+ clazz, e);
		} catch (final IllegalAccessException e) {
			throw new StartException("ERROR: Could not access class " + clazz,
					e);
		}
	}

	/**
	 * Add homedir and properties
	 * 
	 * @since 1.0
	 * @param cs
	 * @throws StartException
	 */
	private void configure(ConfigurableStartable cs) throws StartException {
		cs.setHome(home);
		String path = System.getProperty(CONFIG_FILE_PROPERTY);

		if (path == null) {
			System.err.println("ERROR: Failed to find confing property");
			System.exit(1);
		}

		Properties p = new Properties();
		File f = new File(home, path);
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
			p.load(fis);
		} catch (FileNotFoundException e) {
			throw new StartException("Failed to open config file "
					+ f.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new StartException("Failed to read config file "
					+ f.getAbsolutePath(), e);
		}
		cs.setProperties(p);
	}

	/**
	 * @throws StartException
	 * @since 1.0
	 */
	private void loadPolicy() throws StartException {
		String clazz = null;
		try {
			clazz = this.prop.getProperty(Starter.SECURITY_POLICY);
			if (clazz != null) {
				final Class c = Class.forName(clazz, true, cl);
				if (!Policy.class.isAssignableFrom(c)) {
					throw new StartException("ERROR: " + c.getName()
							+ " is not an instance of "
							+ Policy.class.getName());
				}

				if (!c.isAssignableFrom(Policy.getPolicy().getClass())) {
					Policy.setPolicy((Policy) c.newInstance());
				}

				if (c.isAssignableFrom(Policy.getPolicy().getClass())) {
					System.out.println("Policy: " + clazz);
				} else {
					throw new StartException("Can't set Policy " + clazz);
				}
			}
		} catch (final ClassNotFoundException e) {
			throw new StartException("ERROR: Class " + clazz + " not found", e);
		} catch (final InstantiationException e) {
			throw new StartException("ERROR: Could not instantiate class "
					+ clazz, e);
		} catch (final IllegalAccessException e) {
			throw new StartException("ERROR: Could not access class " + clazz,
					e);
		}
	}

	/**
	 * Read in classpath jars
	 * 
	 * @return URLs of jars to put on the classpath
	 * @since 1.0
	 */
	private URL[] getJars() {

		final LinkedList<URL> l = new LinkedList<URL>();

		final File f = new File(home, prop.getProperty(Starter.JAR_DIR));

		System.out.println(Starter.JAR_DIR + "=" + f);

		f.listFiles(new FileFilter() {
			public boolean accept(final File pathname) {
				final boolean b = pathname.isFile()
						&& pathname.getName().endsWith(".jar");
				if (b) {
					try {
						l.add(pathname.toURI().toURL());
					} catch (final MalformedURLException e) {
						System.err.println("Failed to add "
								+ pathname.getAbsolutePath() + " to classpath");
						e.printStackTrace();
					}
				}
				return b;
			}
		});

		for (URL url : l) {
			System.out.println("ADDED: " + url);
		}

		return l.toArray(new URL[0]);
	}

	/**
	 * Works out where home dir for this application is. First from system
	 * property, then environment variable, then searching.
	 * @throws StartException 
	 * 
	 * @since 1.0
	 */
	private void setHome() throws StartException {

		final LinkedList<String> l = new LinkedList<String>();
		final String needle = prop.getProperty(Starter.HOME_SEARCH_FILE);

		final String property = prop.getProperty(Starter.HOME_DIR_PROPERTY);
		l.add(System.getProperty(property));

		homeEnv = prop.getProperty(Starter.HOME_DIR_ENV);
		l.add(System.getenv(homeEnv));

		l.addAll(Arrays.asList(prop.getProperty(Starter.HOME_DIR_SEARCH).split(
				",")));
		System.out.println("Using '" + needle + "' to confirm home dir");
		for (final Iterator<String> i = l.iterator(); i.hasNext();) {
			String s = i.next();
			if (s != null) {
				s = s.replace('/', File.separatorChar).trim();
				final File home = new File(s);
				System.out.println("Searching: " + home.getAbsolutePath());
				final File f = new File(home, needle);
				if (f.isFile()) {
					try {
						this.home = home.getCanonicalFile();
					} catch (IOException e) {
						System.out.println("Failed to canonicalize " + home);
					}
					System.setProperty(property, this.home.getAbsolutePath());
					System.out.println(property + "="
							+ System.getProperty(property));
					return;
				}
			} else {
				i.remove();
			}
		}

		throw new StartException(
				"ERROR: application home dir not found in: java system property "
						+ property + "; environment variable " + homeEnv
						+ "; in the paths " + l + ". Searching for file "
						+ needle);
	}

	/**
	 * Invokes the application, by calling {@link #startable}.startup().
	 * 
	 * @throws StartException
	 * 
	 * @since 1.0
	 */
	public void startup() throws StartException {
		startable.startup();
	}

	/**
	 * Prints ClassLoader heirarchy of <code>o</code>.
	 * 
	 * @param o
	 * @since 1.0
	 */
	public static void printClassloaders(final Object o) {
		System.out.println("Current ClassLoader for Class: "
				+ o.getClass().getName());
		ClassLoader current = o.getClass().getClassLoader();

		while (current != null) {
			System.out.println(current.getClass());
			current = current.getParent();
		}

		System.out.println("Context ClassLoader of this Thread:");

		current = Thread.currentThread().getContextClassLoader();

		while (current != null) {
			System.out.println(current.getClass());
			current = current.getParent();
		}
	}
	
	/**
	 * 
	 * @since 1.0
	 * @return the {@link Startable} we are concerned with
	 */
	public Startable getStartable() {
		return startable;
	}
}
