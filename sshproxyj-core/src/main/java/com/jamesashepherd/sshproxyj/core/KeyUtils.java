/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;


/**
 * @author James A. Shepherd
 * @since 1.0
 */
public class KeyUtils {
	final Logger logger = LoggerFactory.getLogger(KeyUtils.class);
	
	public static boolean haveSameModulus(PublicKey pk1, PublicKey pk2) throws SshProxyJException {
		String type1 = pk1.getAlgorithm();
		String type2 = pk2.getAlgorithm();
		
		if(type1.equals(type2)) {
			if(type1.equals("DSA")) {
				return ((DSAPublicKey) pk1).getY().equals(
						((DSAPublicKey) pk2).getY());
			} else if(type2.equals("RSA")) {
				return ((RSAPublicKey) pk1).getModulus().equals(
						((RSAPublicKey) pk2).getModulus());
			} else {
				throw new SshProxyJException("Public Key is not DSA or RSA");
			}
		}
		
		return false;
	}
}


