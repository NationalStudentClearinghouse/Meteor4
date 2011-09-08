package org.meteornetwork.meteor.common.ws.security;

import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.ws.security.components.crypto.CertificateStore;

public class MeteorCertificateStore extends CertificateStore {

	/**
	 * WSS4J expects a constructor with these parameters.
	 * 
	 * @param properties
	 * @param classLoader
	 */
	public MeteorCertificateStore(Properties properties, ClassLoader classLoader) {
		super(null);
	}

	/**
	 * Adds an X509 certifiate to this certificate store
	 * 
	 * @param cert
	 *            the certificate to add
	 */
	public void addCertificate(X509Certificate cert) {
		if (trustedCerts == null) {
			trustedCerts = new X509Certificate[] { cert };
		} else {
			trustedCerts = (X509Certificate[]) ArrayUtils.add(trustedCerts, cert);
		}
	}

}
