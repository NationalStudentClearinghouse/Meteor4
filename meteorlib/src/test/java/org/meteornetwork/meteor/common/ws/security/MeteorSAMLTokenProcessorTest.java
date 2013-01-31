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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.apache.ws.security.WSSecurityException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.saml.ProviderType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MeteorSAMLTokenProcessorTest {

	private MeteorSAMLTokenProcessor meteorSAMLTokenProcessor;
	private RegistryManager registryManagerMock;

	public MeteorSAMLTokenProcessorTest() {
		meteorSAMLTokenProcessor = new MeteorSAMLTokenProcessor();

		registryManagerMock = EasyMock.createStrictMock(RegistryManager.class);
		meteorSAMLTokenProcessor.setRegistryManager(registryManagerMock);
	}

	@Before
	public void setUp() {
		EasyMock.reset(registryManagerMock);
	}

	@Test
	public void testAddCertificateToCrypto() throws SecurityException, NoSuchMethodException, CertificateException, IOException, RegistryException, ParserConfigurationException, SAXException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, WSSecurityException {
		Method addCertificateToCryptoMethod = MeteorSAMLTokenProcessor.class.getDeclaredMethod("addCertificateToCrypto", Element.class, MeteorCertificateStore.class);
		addCertificateToCryptoMethod.setAccessible(true);

		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		FileInputStream fileInputStream = new FileInputStream(new File(this.getClass().getResource("test.cer").getFile()));
		try {
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(fileInputStream);
			EasyMock.expect(registryManagerMock.getCertificate("LTI_AP40", ProviderType.ACCESS)).andReturn(cert);
			EasyMock.replay(registryManagerMock);
			fileInputStream.close();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			fileInputStream = new FileInputStream(new File(this.getClass().getResource("assertion.xml").getFile()));
			Document assertion = docBuilder.parse(fileInputStream);
			Element token = assertion.getDocumentElement();

			MeteorCertificateStore crypto = new MeteorCertificateStore(null, null);

			addCertificateToCryptoMethod.invoke(meteorSAMLTokenProcessor, token, crypto);
			Assert.assertTrue(crypto.verifyTrust(cert.getPublicKey()));
		} finally {
			fileInputStream.close();
		}
	}

}
