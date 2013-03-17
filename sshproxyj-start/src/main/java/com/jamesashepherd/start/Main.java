/**
 * Copyright 2007 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * <p>
 * Java Daemonizing.
 * </p>
 * 
 * <p>
 * Allows us to do everything in Java that is needed to run a Java application
 * as a daemon or service. This class is the <code>Main-Class</code> for the
 * <code>.jar</code>, by default it sets up an {@link Listener} to listen for
 * admin commands over a socket, and continues to start up the application.
 * </p>
 * 
 * 
 * @author James A. Shepherd
 * @version $Id: Main.java 131 2007-06-08 14:15:24Z jas $
 * @since 0.5
 * 
 */
public class Main {

	/**
	 * System property holding port to listen on for admin commands.
	 */
	public static final String PORT_PROPERTY_KEY = "start.port";

	/**
	 * System property holding code used to authorize admin commands.
	 */
	public static final String CODE_PROPERTY_KEY = "start.code";

	/**
	 * System property holding the file to output the start code to if not null.
	 */
	public static final String CODEFILE_PROPERTY_KEY = "start.code.file";

	/**
	 * Default start.properties file resource name.
	 */
	public static final String START_PROPERTIES_RESOURCE = "start.properties";

	/**
	 * Possible commands, the first one is the default.
	 * 
	 * @author James A. Shepherd
	 * @version $Id: Main.java 131 2007-06-08 14:15:24Z jas $
	 * @since 0.5
	 * 
	 */
	public static enum Commands {
		runonly, help, startup, shutdown
	}

	/**
	 * command line option prefix
	 */
	public static final String PREFIX = "--";

	/**
	 * <p>
	 * Entry point.
	 * </p>
	 * 
	 * <p>
	 * Has the following commandline options:
	 * </p>
	 * 
	 * <p>
	 * System properties:
	 * </p>
	 * <ul>
	 * <li>{@link #PORT_PROPERTY_KEY} port to listen for signals on 127.0.0.1</li>
	 * <li>{@link #CODE_PROPERTY_KEY} code to use to authorize signals</li>
	 * <li>{@link #CODEFILE_PROPERTY_KEY} file to output the start code to</li>
	 * </ul>
	 * <p>
	 * When starting, if a port and a code are not given then the next free port
	 * is used, and a code is automatically generated. Pass port of 0 to use the
	 * next free port. The code and port used are echoed to the console if they
	 * are generated automatically, and if {@link #CODEFILE_PROPERTY_KEY} is set
	 * the code is output to it. This final point is so that the code doesn't
	 * have to appear on the command line, and can be read easily by the
	 * stopping process.
	 * </p>
	 * 
	 * <p>
	 * Command line arguments {@link #Commands}:
	 * </p>
	 * <ul>
	 * <li><code>&lt; empty &gt;</code> same as <code>--runonly</code>
	 * <li><code>--help</code> display this help text</li>
	 * <li><code>--runonly</code> do not port start listener, just run
	 * application
	 * <li><code>--startup</code> Startup service</li>
	 * <li><code>--shutdown</code> Shutdown service</li>
	 * </ul>
	 * <p>
	 * optionally followed by a <code>start.properties</code> filename. If no
	 * properties file is specified then we use the one in resources.
	 * </p>
	 * 
	 * <p>
	 * We also call {@link Runtime#addShutdownHook} and also use this signal to
	 * stop the application when <code>--startup</code> or
	 * <code>--runonly</code> is used.
	 * </p>
	 * 
	 * @param args
	 *            commandline arguments
	 * @since 0.5
	 */
	public static void main(final String[] args) {

		// command
		final String command = (args.length < 1) ? Main.PREFIX
				+ Commands.values()[0].toString() : args[0];

		// start.properties (note is second parameter)
		final String file = (args.length > 1) ? args[1] : null;

		// are we asked for help?
		if (command.equals(Main.PREFIX + Commands.valueOf("help"))) {
			Main.outputHelp();
		} else if (command.equals(Main.PREFIX + Commands.valueOf("runonly"))) {
			// run application
			try {
				Main.runonly(file);
			} catch (StartException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {

			// if not asked for help or runonly then we need port and code
			final int startport = Integer.getInteger(Main.PORT_PROPERTY_KEY, 0)
					.intValue();

			final String startcode = System.getProperty(Main.CODE_PROPERTY_KEY,
					null);

			// are we asked to shutdown?
			if (command.equals(Main.PREFIX + Commands.valueOf("shutdown"))) {

				// need valid port
				if (startport <= 0 || startport > 65535) {
					System.err.println();
					System.err.println("ERROR: Need a valid port to contact");
					System.err.println();
					Main.outputHelp();
					System.exit(1);
				}

				// need valid code
				if (startcode == null) {
					System.err.println();
					System.err
							.println("ERROR: Need a code to contact port with");
					System.err.println();
					Main.outputHelp();
					System.exit(1);
				}

				// stop application
				Main.shutdown(startport, startcode);

			} else if (command
					.equals(Main.PREFIX + Commands.valueOf("startup"))) {
				// we are asked to start the application

				// need a valid port
				if (startport < 0 || startport > 65535) {
					System.err.println();
					System.err.println("ERROR: Need a valid port to listen on");
					System.err.println();
					Main.outputHelp();
					System.exit(1);
				}

				// see if we need to output start code to a file
				final String scf = System
						.getProperty(Main.CODEFILE_PROPERTY_KEY);
				File startcodefile = null;
				if (scf != null) {
					startcodefile = new File(scf);
				}
				// start application
				Main.startup(startport, startcode, file, startcodefile);
			} else {
				Main.outputHelp();
			}
		}
	}

	/**
	 * Run application without listener on a port
	 * 
	 * @throws StartException
	 * 
	 * @since 0.5
	 */
	private static void runonly(final String file) throws StartException {

		// load properties
		final Properties prop = Main.loadProperties(file);

		// OK, now we are ready to start the listener and invoke the application
		final Starter s = new Starter(prop);
		final Listener l = new Listener(s.getStartable());
		l.start();
		s.startup();
	}

	/**
	 * <p>
	 * Startup application.
	 * </p>
	 * 
	 * <p>
	 * Note that <code>file</code> only needs to list the properties you want to
	 * override.
	 * </p>
	 * 
	 * @param startport
	 *            port to listen on
	 * @param startcode
	 *            code to listen for
	 * @param file
	 *            properties file to override defaults, may be null
	 * @param startcodefile
	 *            where to output the start code, if not null
	 * @since 0.5
	 */
	private static void startup(final int startport, final String startcode,
			final String file, final File startcodefile) {

		// load properties
		final Properties prop = Main.loadProperties(file);

		// OK, now we are ready to start the listener and invoke the application
		final Starter s = new Starter(prop);

		try {
			final Listener l = new Listener(s.getStartable(), startport,
					startcode, startcodefile);
			l.start();
		} catch (final UnknownHostException e) {
			System.err
					.println("Could not listen for shutdown on 127.0.0.1 on port: "
							+ startport);
			System.exit(1);
		} catch (final IOException e) {
			System.err
					.println("Could not listen for shutdown on 127.0.0.1 on port: "
							+ startport);
			System.exit(1);
		}

		try {
			s.startup();
		} catch (StartException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Does boilerplate loading of Properties.
	 * 
	 * @param propFile
	 * @return Properties
	 * @since 0.5
	 */
	private static Properties loadProperties(final String file) {

		File propFile = null;
		if (file != null) {
			propFile = new File(file);
			if (!propFile.isFile() || !propFile.canRead()) {
				System.err.println("ERROR: Can't find properties file: "
						+ propFile);
				System.exit(1);
			}
		}

		// first need to load in default start.properties
		final Properties propDef = new Properties();

		final InputStream dis = Main.class
				.getResourceAsStream(Main.START_PROPERTIES_RESOURCE);

		try {
			if (dis != null) {
				propDef.load(dis);
			} else {
				System.err.println("ERROR: Could not load default "
						+ Main.START_PROPERTIES_RESOURCE + " file");
				System.exit(1);
			}
		} catch (final IOException e) {
			System.err.println("ERROR: Could not load default "
					+ Main.START_PROPERTIES_RESOURCE + " file");
			System.exit(1);
		}

		// now load in commandline start.properties
		final Properties prop = new Properties(propDef);

		if (propFile != null) {
			InputStream is = null;
			try {
				is = new FileInputStream(propFile);
				prop.load(is);
			} catch (final FileNotFoundException e) {
				System.err.println("ERROR: Could not load properties file: "
						+ propFile);
				System.exit(1);
			} catch (final IOException e) {
				System.err.println("ERROR: Could not load properties file: "
						+ propFile);
				System.exit(1);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (final IOException e) {
					}
				}
			}
		}

		return prop;
	}

	/**
	 * Shutdown application.
	 * 
	 * @param startport
	 *            port to contact
	 * @param startcode
	 *            code to use
	 * @since 0.5
	 */
	private static void shutdown(final int startport, final String startcode) {
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			socket = new Socket("127.0.0.1", startport);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out.println(startcode);
			out.println("shutdown");
			String rcvd;
			while ((rcvd = in.readLine()) != null) {
				if (rcvd.equals(Listener.BYE)) {
					break;
				}
				System.out.println("SERVER MESSAGE: " + rcvd);
			}
			out.close();
			in.close();
			socket.close();
		} catch (final UnknownHostException e) {
			System.err
					.println("ERROR: Can't connect to 127.0.0.1:" + startport);
			System.exit(1);
		} catch (final IOException e) {
			System.err.println("ERROR: Can't talk to 127.0.0.1:" + startport);
			System.exit(1);
		} finally {
			if (out != null) {
				out.close();
			}

			if (in != null) {
				try {
					in.close();
				} catch (final IOException e) {
				}
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	/**
	 * Spits out help information
	 * 
	 * @since 0.5
	 */
	private static void outputHelp() {
		System.out.println("usage: java [-D" + Main.PORT_PROPERTY_KEY
				+ "=p] [-D" + Main.CODE_PROPERTY_KEY + "=c] [-D"
				+ Main.CODEFILE_PROPERTY_KEY + "=f] -jar X.jar [OPTIONS]");
		System.out.println();
		System.out.println("OPTIONS:");
		System.out.println();
		System.out
				.println("    < empty >                                 same as --runonly .");
		System.out.println();
		System.out
				.println("    --help                                    display this help text.");
		System.out.println();
		System.out
				.println("    --runonly                                 do not start port listener, just run application.");
		System.out.println();
		System.out
				.println("    --startup [/path/to/start.properties]     startup service,");
		System.out
				.println("                                              listening on 127.0.0.1 on port p,");
		System.out
				.println("                                              with auth code c for shutdown message,");
		System.out
				.println("                                              optionally overriding defaults with given properties file,");
		System.out
				.println("                                              optionally outputting the generated code in file f.");
		System.out.println();
		System.out
				.println("    --shutdown                                shutdown service");
		System.out
				.println("                                              contacting on 127.0.0.1 on port p,");
		System.out
				.println("                                              with auth code c.");
	}
}
