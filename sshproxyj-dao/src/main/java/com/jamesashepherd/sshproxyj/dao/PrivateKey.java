/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An SSH privat key that will be used to log into some hosts.
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
@Entity
@Table(name = "PrivateKey")
public class PrivateKey {
	final Logger logger = LoggerFactory.getLogger(PrivateKey.class);
	private Long id;
	private String name;
	private String comment;
	private String privateKey;

	public PrivateKey() {
	}

	/**
	 * @return the id
	 * @since 1.0
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nPrivateKeyID", nullable = false)
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
	 * @return the name
	 * @since 1.0
	 */
	@Column(name = "cName", nullable = false, unique = true)
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 * @since 1.0
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the privateKey
	 * @since 1.0
	 */
	@Column(name = "cPrivKey", nullable = false)
	@Lob
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * @param privateKey
	 *            the privateKey to set
	 * @since 1.0
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
