/**
 * Copyright 2013 James A. Shepherd
 * http://www.JamesAshepherd.com/
 *
 * LICENCE: http://www.gnu.org/licenses/lgpl.html
 */
package com.jamesashepherd.sshproxyj.core;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.codehaus.plexus.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesashepherd.sshproxyj.SshProxyJException;

/**
 * 
 * I found this code at {@link http
 * ://maven.apache.org/wagon/xref/org/apache/maven
 * /wagon/providers/ssh/TestPublickeyAuthenticator.html}
 * 
 * Original code has Apache 2.0 Licence {@link http
 * ://www.apache.org/licenses/LICENSE-2.0}
 * 
 * @author James A. Shepherd
 * @since 1.0
 */
public class PublicKeyRead {
	final Logger logger = LoggerFactory.getLogger(PublicKeyRead.class);

	/**
	 * 
	 * @since 1.0
	 * @param keyLine
	 * @return
	 * @throws Exception
	 */
	public static PublicKey decodePublicKey(String keyLine) throws SshProxyJException {
		byte[] bytes = null;
		int pos = 0;

		for (String part : keyLine.split(" ")) {
			if (part.startsWith("AAAA")) {
				bytes = Base64.decodeBase64(part.getBytes());
				break;
			}
		}
		
		if (bytes == null) {
			throw new SshProxyJException("no Base64 part to decode");
		}

		String type = decodeType(bytes, pos);
		try {
			if (type.equals("ssh-rsa")) {
				BigInteger e = decodeBigInt(bytes, pos);
				BigInteger m = decodeBigInt(bytes, pos);
				RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
				return KeyFactory.getInstance("RSA").generatePublic(spec);
			} else if (type.equals("ssh-dss")) {
				BigInteger p = decodeBigInt(bytes, pos);
				BigInteger q = decodeBigInt(bytes, pos);
				BigInteger g = decodeBigInt(bytes, pos);
				BigInteger y = decodeBigInt(bytes, pos);
				DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
				return KeyFactory.getInstance("DSA").generatePublic(spec);
			} else {
				throw new SshProxyJException("unknown type " + type);
			}
		} catch (InvalidKeySpecException e) {
			throw new SshProxyJException("Failed to load PublicKey", e);
		} catch (NoSuchAlgorithmException e) {
			throw new SshProxyJException("Failed to load PublicKey", e);
		}
	}

	private static String decodeType(byte[] bytes, int pos) {
		int len = decodeInt(bytes, pos);
		String type = new String(bytes, pos, len);
		pos += len;
		return type;
	}

	private static int decodeInt(byte[] bytes, int pos) {
		return ((bytes[pos++] & 0xFF) << 24) | ((bytes[pos++] & 0xFF) << 16)
				| ((bytes[pos++] & 0xFF) << 8) | (bytes[pos++] & 0xFF);
	}

	private static BigInteger decodeBigInt(byte[] bytes, int pos) {
		int len = decodeInt(bytes, pos);
		byte[] bigIntBytes = new byte[len];
		System.arraycopy(bytes, pos, bigIntBytes, 0, len);
		pos += len;
		return new BigInteger(bigIntBytes);
	}
}
