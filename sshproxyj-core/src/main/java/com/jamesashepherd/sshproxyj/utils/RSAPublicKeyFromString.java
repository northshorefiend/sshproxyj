/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.utils;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * 
 * 
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class RSAPublicKeyFromString implements RSAPublicKey {
	final Logger logger = LoggerFactory.getLogger(RSAPublicKeyFromString.class);
	
	RSAPublicKey delegate;

	public RSAPublicKeyFromString(String key) throws SshProxyJException {
		PublicKeyRead pkr = new PublicKeyRead();
		delegate = (RSAPublicKey) pkr.decodePublicKey(key);
	}
	
	/**
	 * @return
	 * @see java.security.Key#getAlgorithm()
	 */
	public String getAlgorithm() {
		return delegate.getAlgorithm();
	}

	/**
	 * @return
	 * @see java.security.Key#getEncoded()
	 */
	public byte[] getEncoded() {
		return delegate.getEncoded();
	}

	/**
	 * @return
	 * @see java.security.Key#getFormat()
	 */
	public String getFormat() {
		return delegate.getFormat();
	}

	/**
	 * @return
	 * @see java.security.interfaces.RSAKey#getModulus()
	 */
	public BigInteger getModulus() {
		return delegate.getModulus();
	}

	/**
	 * @return
	 * @see java.security.interfaces.RSAPublicKey#getPublicExponent()
	 */
	public BigInteger getPublicExponent() {
		return delegate.getPublicExponent();
	}
}


