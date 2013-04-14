/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

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
	private String user;
	private String fqdn;
	private int port;
	private String username;
	private byte[] bytes;
	private YesNo continues;
	private LogInOut logInOut;

	public LogEntry() {
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
	 * @return The sshproxyj user
	 */
	@Column(name = "cUser", nullable = false)
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * 
	 * @since 1.0
	 * @return host that is being proxied
	 */
	@Column(name = "cFQDN", nullable = false)
	public String getFQDN() {
		return fqdn;
	}

	public void setFQDN(String fqdn) {
		this.fqdn = fqdn;
	}

	@Column(name = "nPort", nullable = false)
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 
	 * @since 1.0
	 * @return the username on the remote host
	 */
	@Column(name = "cUsername", nullable = false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Lob
	@Column(name = "bBytes", nullable = false)
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Column(name = "cContinues", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo isContinues() {
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
}
