/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.dao;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * A user of the sshproxyj application
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
@Entity
@Table(name = "User")
public class User {
	final Logger logger = LoggerFactory.getLogger(User.class);
	private Long id;
	private String comment;
	private YesNo enabled;
	private String publicKey;
	private String user;
	private Set<Host> hosts;
	private Set<HostGroup> hostGroups;
	final static public String userRegexp = "^[a-z][-a-z0-9]*$";
	final private Pattern userPattern = Pattern.compile(userRegexp);

	public User() {
	}

	/**
	 * @return the id
	 * @since 1.0
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nUserID", nullable = false)
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

	@Column(name = "cPubKey", nullable = false)
	@Lob
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * @return the username
	 * @since 1.0
	 */
	@Column(name = "cUser", nullable = false, unique = true)
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 * @throws SshProxyJException
	 * @since 1.0
	 */
	public void setUser(String user) throws SshProxyJException {
		Matcher m = userPattern.matcher(user);
		
		if (!m.find())
			throw new SshProxyJException("Username '" + user
					+ "' does not match pattern /" + userRegexp + "/");
		
		this.user = user;
	}

	/**
	 * @return the hosts
	 * @since 1.0
	 */
	@ManyToMany
	@JoinTable(name = "User_Host", joinColumns = @JoinColumn(name = "nUserID"), inverseJoinColumns = @JoinColumn(name = "nHostID"))
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

	/**
	 * @return the hostGroups
	 * @since 1.0
	 */
	@ManyToMany
	@JoinTable(name = "User_HostGroup", joinColumns = @JoinColumn(name = "nUserID"), inverseJoinColumns = @JoinColumn(name = "nHostGroupID"))
	public Set<HostGroup> getHostGroups() {
		return hostGroups;
	}

	/**
	 * @param hostGroups
	 *            the hostGroups to set
	 * @since 1.0
	 */
	public void setHostGroups(Set<HostGroup> hostGroups) {
		this.hostGroups = hostGroups;
	}
}
