/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class MemoryRemoteUserCredentialsService implements
		RemoteUserCredentialsService {
	final Logger logger = LoggerFactory
			.getLogger(MemoryRemoteUserCredentialsService.class);
	Map<String, ProxyCredentials> map = new HashMap<String, ProxyCredentials>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jamesashepherd.sshproxyj.core.RemoteUserCredentialsService#
	 * lookupUserCommand(java.lang.String, java.lang.String)
	 */
	@Override
	public ProxyCredentials lookupUserCommand(String username, String command) {
		return map.get(normalizeCommand(command, username));
	}

	public void addCredentials(String username, String command,
			ProxyCredentials pc) {
		map.put(normalizeCommand(command, username), pc);
	}

	private String normalizeCommand(String command, String username) {
		try {
			URL url = new URL("http://" + command + "/" + username);
			String norm = url.getUserInfo() + "@" + url.getHost() + ":"
			+ (url.getPort() == -1 ? 22 : url.getPort())
			+ url.getPath();
			logger.debug(norm);
			return norm;
		} catch (MalformedURLException e) {
			logger.info("Failed to normalize command", e);
			return null;
		}
	}
}
