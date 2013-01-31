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
