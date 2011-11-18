/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.meteornetwork.meteor.common.util.exception.MeteorSecurityException;
import org.meteornetwork.meteor.saml.util.DOMUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class DigitalSignatureManager {

	public PrivateKey getPrivateKey(PrivateKeyParams params) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException {
		KeyStore keyStore = KeyStore.getInstance(params.getKeystoreType());

		char[] passwdChars = params.getKeystorePass() == null ? null : params.getKeystorePass().toCharArray();

		FileInputStream keyStoreFile = new FileInputStream(params.getKeystoreFile());
		try {
			keyStore.load(keyStoreFile, passwdChars);
		} finally {
			if (keyStoreFile != null) {
				keyStoreFile.close();
			}
		}

		passwdChars = params.getPrivateKeyPass() == null ? null : params.getPrivateKeyPass().toCharArray();
		KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(passwdChars);

		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(params.getPrivateKeyAlias(), passwordProtection);
		PrivateKey myPrivateKey = pkEntry.getPrivateKey();
		return myPrivateKey;
	}

	/**
	 * Sign an xml DOM object
	 * 
	 * @param doc
	 *            the document object to sign
	 * @param privateKey
	 *            the private key to sign with
	 * @param cert
	 *            if not null, attached to assertion
	 * @return
	 * @throws MeteorSecurityException
	 * @throws IOException
	 */
	public String sign(Document doc, PrivateKey privateKey, X509Certificate cert) throws MeteorSecurityException, IOException {
		String baseURI = "";
		XMLSignature dsig = null;

		String algorithm = null;
		if (privateKey instanceof RSAPrivateKey) {
			algorithm = XMLSignature.ALGO_ID_SIGNATURE_RSA;
		} else if (privateKey instanceof DSAPrivateKey) {
			algorithm = XMLSignature.ALGO_ID_SIGNATURE_DSA;
		} else {
			throw new MeteorSecurityException("Private Key implements an unknown algorithm. The only supported algorithms are RSA and DSA.");
		}

		Transforms transforms = new Transforms(doc);
		try {
			dsig = new XMLSignature(doc, baseURI, algorithm);

			Element elem = doc.getDocumentElement();
			elem.appendChild(dsig.getElement());

			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
			dsig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

			if (cert != null) {
				dsig.addKeyInfo(cert);
				dsig.addKeyInfo(cert.getPublicKey());
			}

			dsig.sign(privateKey);
		} catch (Exception e) {
			throw new MeteorSecurityException("Could not sign xml", e);
		}

		return DOMUtils.domToString(doc);
	}
}
