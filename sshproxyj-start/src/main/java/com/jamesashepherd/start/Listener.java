/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * <p>
 * Server, that listens for admin commands.
 * </p>
 * 
 * <p>
 * Also, uses {@link Runtime.addShutdownHook} to stop the application.
 * </p>
 * 
 * @author James A. Shepherd
 * @since 1.0
 * 
 */
public class Listener extends Thread {

	/**
	 * Last message Listener sends client
	 */
	public static final String BYE = "<<BYE>>";

	/**
	 * Flag to say we have already started to shutdown.
	 */
	private boolean shuttingDown = false;

	/**
	 * Shutdown hook that calls {@link #startable}.shutdown()
	 */
	private Thread shutdownHook;

	/**
	 * Application we are listening on behalf of.
	 */
	private Startable startable;

	/**
	 * code to authenticate commands with
	 */
	private String code;

	/**
	 * code file to delete on exit
	 */
	private File codefile;

	/**
	 * Socket we listen for commands on.
	 */
	private ServerSocket socket;

	/**
	 * Should we listen on port.
	 */
	private boolean listenOnPort = false;

	/**
	 * 
	 * @param startable
	 *            application to start
	 * @param startport
	 *            port to listen to on 127.0.0.1, if 0 then a port will be
	 *            chosen and output.
	 * @param startcode
	 *            code to authorize commands with, if null, then a code will be
	 *            chosen and output
	 * @param startcodefile
	 *            file to hold the startcode used
	 * @throws IOException
	 *             if can't open socket
	 * @throws UnknownHostException
	 *             if can't open socket
	 * @since 1.0
	 */
	public Listener(final Startable startable, final int startport,
			String startcode, final File startcodefile)
			throws UnknownHostException, IOException {

		this(startable);
		listenOnPort = true;

		socket = new ServerSocket(startport, 1,
				InetAddress.getByName("127.0.0.1"));

		if (startport == 0) {
			System.out.println("Will listen on 127.0.0.1:"
					+ socket.getLocalPort());
		}

		if (startcode == null) {
			startcode = UUID.randomUUID().toString();
			System.out.println("Will listen for code: '" + startcode + "'");
		}

		code = startcode;
		codefile = startcodefile;

		if (codefile != null) {
			FileOutputStream out = null;
			PrintStream printer = null;
			try {
				out = new FileOutputStream(codefile);
				printer = new PrintStream(out);
				printer.println(code);
				printer.flush();
				printer.close();
				out.close();
				printer = null;
				out = null;
				System.out.println("Code output to file");
			} catch (final Exception e) {
				System.err.println("Unable to output code to file: '"
						+ codefile + "'");
				throw new IOException("Cannot open code file: " + codefile);
			} finally {
				if (printer != null) {
					printer.close();
				}
				if (out != null) {
					try {
						out.close();
						out = null;
					} catch (final IOException e) {
					}
				}
			}
		}
	}

	/**
	 * Only listens for JVM shutdown.
	 * 
	 * @param startable
	 *            application to start
	 * @since 1.0
	 */
	public Listener(final Startable startable) {
		super("start Listener");

		this.startable = startable;

		setDaemon(true);

		shutdownHook = new Thread("start ShutdownHook") {
			@Override
			public void run() {
				Listener.this.shutdown();
			}
		};
	}

	/**
	 * Starts up the listener and invokes the application.
	 * 
	 * @see java.lang.Thread#run()
	 * @since 1.0
	 */
	@Override
	public void run() {

		Runtime.getRuntime().addShutdownHook(shutdownHook);

		if (listenOnPort) {
			listen();
		}
	}

	/**
	 * Listen for commands.
	 * 
	 * @since 1.0
	 */
	private void listen() {
		System.out.println("Listening...");
		while (true) {

			Socket clientSocket = null;
			PrintWriter out = null;
			BufferedReader in = null;
			try {
				clientSocket = socket.accept();

				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));

				// read code
				String inputLine = in.readLine();

				// if code correct read command
				if (inputLine != null && inputLine.equals(code)) {
					inputLine = in.readLine();
					if (inputLine != null && inputLine.equals("shutdown")) {
						out.println("SHUTTING DOWN: ...");
						shutdown();
						out.println("SHUTDOWN COMPLETED.");
						out.println(Listener.BYE);
						socket.close();
						in.close();
						break;
					}
				}
				in.close();
			} catch (final IOException e) {
				e.printStackTrace();
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
			}
		}
	}

	/**
	 * Does the actual shutting down.
	 * 
	 * @since 1.0
	 */
	private synchronized void shutdown() {

		// make sure we only shutdown once
		if (!shuttingDown) {
			shuttingDown = true;

			// not necessary
			// Runtime.getRuntime().removeShutdownHook(this.shutdownHook);

			try {
				startable.shutdown();
			} catch (StartException e) {
				e.printStackTrace();
			}

			// delete codefile
			if (codefile != null) {
				if (codefile.delete()) {
					System.out.println("Code file deleted");
				} else {
					System.err.println("ERROR: Code file NOT deleted");
				}
			}
		}
	}
}
