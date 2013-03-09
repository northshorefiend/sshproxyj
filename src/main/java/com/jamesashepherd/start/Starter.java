/**
 * Copyright 2007 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;

import java.io.File;
import java.io.FileFilter;
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
 * @version $Id: Starter.java 382 2008-06-23 14:27:54Z jas $
 * @since 0.5
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
	 * @since 0.5
	 */
	public Starter(final Properties prop) {

		this.prop = prop;
		setHome();
		cl = new URLClassLoader(getJars());
		Thread.currentThread().setContextClassLoader(cl);
		Thread.currentThread().setName("Main");

		// Listener.printClassloaders(this);

		String clazz = null;
		try {

			// load Startable
			clazz = prop.getProperty(Starter.START_CLASS);
			final Class c = Class.forName(clazz, true, cl);
			if (!Startable.class.isAssignableFrom(c)) {
				System.err
						.println("ERROR: " + c.getName()
								+ " is not an instance of "
								+ Startable.class.getName());
				System.exit(1);
			}
			startable = (Startable) c.newInstance();

		} catch (final ClassNotFoundException e) {
			System.err.println("ERROR: Class " + clazz + " not found");
			System.exit(1);
		} catch (final InstantiationException e) {
			System.err.println("ERROR: Could not instantiate class " + clazz);
			System.exit(1);
		} catch (final IllegalAccessException e) {
			System.err.println("ERROR: Could not access class " + clazz);
			System.exit(1);
		}

		// load Policy
		try {
			clazz = this.prop.getProperty(Starter.SECURITY_POLICY);
			if (clazz != null) {
				final Class c = Class.forName(clazz, true, cl);
				if (!Policy.class.isAssignableFrom(c)) {
					System.err.println("ERROR: " + c.getName()
							+ " is not an instance of "
							+ Policy.class.getName());
					System.exit(1);
				}

				if (!c.isAssignableFrom(Policy.getPolicy().getClass())) {
					Policy.setPolicy((Policy) c.newInstance());
				}

				if (c.isAssignableFrom(Policy.getPolicy().getClass())) {
					System.out.println("Policy: " + clazz);
				} else {
					System.err.println("Can't set Policy " + clazz);
					System.exit(1);
				}
			}
		} catch (final ClassNotFoundException e) {
			System.err.println("ERROR: Class " + clazz + " not found");
			System.exit(1);
		} catch (final InstantiationException e) {
			System.err.println("ERROR: Could not instantiate class " + clazz);
			System.exit(1);
		} catch (final IllegalAccessException e) {
			System.err.println("ERROR: Could not access class " + clazz);
			System.exit(1);
		}
	}

	/**
	 * Read in classpath jars
	 * 
	 * @return URLs of jars to put on the classpath
	 * @since 0.5
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
						System.out.println("ADDED: " + pathname);
					} catch (final MalformedURLException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
				return b;
			}
		});

		return l.toArray(new URL[0]);
	}

	/**
	 * Works out where home dir for this application is. First from system
	 * property, then environment variable, then searching.
	 * 
	 * @since 0.5
	 */
	private void setHome() {

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
			// System.out.println(s);
			if (s != null) {
				s = s.replace('/', File.separatorChar).trim();
				final File home = new File(s);
				System.out.println("Searching: " + home.getAbsolutePath());
				final File f = new File(home, needle);
				if (f.isFile()) {
					this.home = home;
					System.setProperty(property, this.home.getAbsolutePath());
					System.out.println(property + "=" + System.getProperty(property));
					return;
				}
			} else {
				i.remove();
			}
		}

		System.err.println("ERROR: application home dir not found in: ");
		System.err.println("       java system property " + property);
		System.err.println("       environment variable " + homeEnv);
		System.err.println("       in the paths " + l);
		System.err.println("Searching for file " + needle);
		System.exit(1);
	}

	/**
	 * Invokes the application, by calling {@link #startable}.startup().
	 * 
	 * @since 0.5
	 */
	public void startup() {

		System.out.println(homeEnv + "=" + home);

		try {
			startable.startup();
		} catch (StartException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Prints ClassLoader heirarchy of <code>o</code>.
	 * 
	 * @param o
	 * @since 0.5
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
	 * @return {@link Startable} object we are concerned with.
	 * @since 0.5
	 */
	public Startable getStartable() {
		return startable;
	}
}
