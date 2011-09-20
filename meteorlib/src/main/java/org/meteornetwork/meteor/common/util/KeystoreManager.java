package org.meteornetwork.meteor.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import org.springframework.stereotype.Service;

@Service
public class KeystoreManager {

	public PrivateKey getPrivateKey( PrivateKeyParams params ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException {
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

}
