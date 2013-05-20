/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A host that this application will be a proxy for.
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
@Entity
@Table(name = "Host", uniqueConstraints = @UniqueConstraint(columnNames = {
		"cFQDN", "nPort", "cUsername" }))
public class Host {
	final Logger logger = LoggerFactory.getLogger(Host.class);
	private Long id;
	private String fqdn;
	private Integer port;
	private String username;
	private String comment;
	private YesNo enabled;
	private PrivateKey privateKey;

	/**
	 * @return the id
	 * @since 1.0
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nHostID", nullable = false)
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 * @since 1.0
	 */
	private void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the fqdn
	 * @since 1.0
	 */
	@Column(name = "cFQDN", nullable = false)
	public String getFqdn() {
		return fqdn;
	}

	/**
	 * @param fqdn
	 *            the fqdn to set
	 * @since 1.0
	 */
	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	/**
	 * @return the port
	 * @since 1.0
	 */
	@Column(name = "nPort", nullable = false)
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 * @since 1.0
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the username
	 * @since 1.0
	 */
	@Column(name = "cUsername", nullable = false)
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 * @since 1.0
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the comment
	 * @since 1.0
	 */
	@Column(name = "cComment", nullable = false)
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 * @since 1.0
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the enabled
	 * @since 1.0
	 */
	@Column(name = "cEnabled", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 * @since 1.0
	 */
	public void setEnabled(YesNo enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the privateKey
	 * @since 1.0
	 */
	@ManyToOne
	@JoinColumn(name = "nPrivKeyID")
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * @param privateKey
	 *            the privateKey to set
	 * @since 1.0
	 */
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
}
