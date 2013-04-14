/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

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
@Entity
@Table(name = "Log")
public class LogEntry {
	final Logger logger = LoggerFactory.getLogger(LogEntry.class);
	private Long id;
	private Date timestamp;
	private Long version;
	private String username;
	private String host;
	private int port;
	private String remoteUsername;
	private byte[] bytes = null;
	private YesNo continues;
	private LogInOut logInOut;

	public LogEntry() {
	}

	public LogEntry(ProxyCredentials pc, byte[] b, boolean continues) {
		setTimestamp(new Date());
		setUsername(pc.getUsername());
		setRemoteHost(pc.getRemoteHost());
		setRemotePort(pc.getRemotePort());
		setRemoteUsername(pc.getRemoteUsername());
		setBytes(b);
		setContinues(continues ? YesNo.Y : YesNo.N);
		setLogInOut(LogInOut.N);
	}

	public LogEntry(ProxyCredentials pc, LogInOut loginout) {
		this(pc, null, false);
		setLogInOut(loginout);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nLogID", nullable = false)
	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "nVersion", nullable = false)
	public Long getVersion() {
		return version;
	}

	private void setVersion(long version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtTimestamp", nullable = false)
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 
	 * @since 1.0
	 * @return The sshproxyj username
	 */
	@Column(name = "cUsername", nullable = false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @since 1.0
	 * @return host that is being proxied
	 */
	@Column(name = "cHost", nullable = false)
	public String getRemoteHost() {
		return host;
	}

	public void setRemoteHost(String host) {
		this.host = host;
	}

	@Column(name = "nPort", nullable = false)
	public int getRemotePort() {
		return port;
	}

	public void setRemotePort(int port) {
		this.port = port;
	}

	/**
	 * 
	 * @since 1.0
	 * @return the username on the remote host
	 */
	@Column(name = "cRemoteUsername", nullable = false)
	public String getRemoteUsername() {
		return remoteUsername;
	}

	public void setRemoteUsername(String remoteUsername) {
		this.remoteUsername = remoteUsername;
	}

	@Lob
	@Column(name = "bBytes", nullable = true)
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Column(name = "cContinues", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getContinues() {
		return continues;
	}

	public void setContinues(YesNo continues) {
		this.continues = continues;
	}

	@Column(name = "cLogInOut", nullable = false)
	@Enumerated(EnumType.STRING)
	public LogInOut getLogInOut() {
		return logInOut;
	}

	public void setLogInOut(LogInOut logInOut) {
		this.logInOut = logInOut;
	}

	@Override
	public String toString() {
		String out = getId() + ":" + getTimestamp() + " " + getRemoteUsername()
				+ "@" + getRemoteHost() + ":" + getRemotePort() + "/"
				+ getUsername() + " Continues:" + getContinues() + " LogInOut:"
				+ getLogInOut();

		if (getBytes() != null) {
			try {
				out += " {" + new String(getBytes(), "UTF-8") + "}";
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to create String", e);
			}
		}
		return out;
	}
}
