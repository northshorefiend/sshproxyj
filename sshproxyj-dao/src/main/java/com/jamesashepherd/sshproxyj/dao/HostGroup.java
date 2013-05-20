/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A name for a group of hosts.
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
@Entity
@Table(name = "HostGroup")
public class HostGroup {
	final Logger logger = LoggerFactory.getLogger(HostGroup.class);
	private Long id;
	private String name;
	private String comment;
	private Set<Host> hosts;

	/**
	 * @return the id
	 * @since 1.0
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nHostGroupID", nullable = false)
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
	 * @return the hosts
	 * @since 1.0
	 */
	@ManyToMany
	@JoinTable(name = "HostGroup_Host", joinColumns = @JoinColumn(name = "nHostGroupID"), inverseJoinColumns = @JoinColumn(name = "nHostID"))
	public Set<Host> getHosts() {
		return hosts;
	}

	/**
	 * @param hosts
	 *            the hosts to set
	 * @since 1.0
	 */
	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}
}
