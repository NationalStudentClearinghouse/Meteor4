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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.SAMLTokenProcessor;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.saml.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Extends WSS4J SAMLTokenProcessor to retrieve public certificates from the
 * Meteor registry. Assumes crypto provider is MeteorCertificateStore
 * 
 * @author jlazos
 * 
 */
public class RegistrySAMLTokenProcessor extends SAMLTokenProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrySAMLTokenProcessor.class);

	private RegistryManager registryManager;

	private static final ProviderType DEFAULT_PROVIDER_TYPE = ProviderType.ACCESS;

	@Override
	public Credential handleSAMLToken(Element token, RequestData data, Validator validator, WSDocInfo docInfo) throws WSSecurityException {
		assert data.getSigCrypto() instanceof MeteorCertificateStore : "Signature crypto implementation is not MeteorCertificateStore";

		MeteorCertificateStore crypto = (MeteorCertificateStore) data.getSigCrypto();
		addCertificateToCrypto(token, crypto);

		Credential credential;
		try {
			credential = super.handleSAMLToken(token, data, validator, docInfo);
		} catch (WSSecurityException e) {
			LOG.debug("Could not handle SAML token", e);
			throw new WSSecurityException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef(), e);
		}

		return credential;
	}

	/**
	 * Gets Meteor Insititution Identifier out of the SAML assertion and fetches
	 * the public key from the registry
	 * 
	 * @param token
	 * @param crypto
	 * @throws WSSecurityException
	 */
	private void addCertificateToCrypto(Element token, MeteorCertificateStore crypto) throws WSSecurityException {
		String institutionID = getMeteorInstitutionIdentifier(token);
		ProviderType providerType = getProviderType(token);

		try {
			LOG.debug("Getting certificate for " + institutionID + ", provider type " + providerType.getType());
			crypto.addCertificate(registryManager.getCertificate(institutionID, providerType));
		} catch (RegistryException e) {
			LOG.debug("Could not get X509 certificate from Meteor registry for institution " + institutionID);
			throw new WSSecurityException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef(), e);
		}
	}

	private String getMeteorInstitutionIdentifier(Element token) throws WSSecurityException {
		LOG.debug("Getting identifier from assertion");
		NodeList elements = token.getElementsByTagNameNS(WSConstants.SAML2_NS, "NameID");
		if (elements == null) {
			LOG.debug("Cannot validate SAML assertion - subject name id is missing");
			throw new WSSecurityException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef());
		}

		for (int i = 0; i < elements.getLength(); ++i) {
			Node node = elements.item(i);
			if (node.getParentNode() != null && node.getParentNode().getNamespaceURI().equals(WSConstants.SAML2_NS) && node.getParentNode().getLocalName().equals("Subject")) {
				return node.getFirstChild().getNodeValue();
			}
		}

		LOG.debug("Cannot validate SAML assertion - subject name id is missing");
		throw new WSSecurityException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef());
	}

	private ProviderType getProviderType(Element token) {
		NodeList elements = token.getElementsByTagNameNS(WSConstants.SAML2_NS, "AttributeValue");
		if (elements == null) {
			return DEFAULT_PROVIDER_TYPE;
		}

		for (int i = 0; i < elements.getLength(); ++i) {
			Node node = elements.item(i);
			NamedNodeMap attributes = node.getParentNode().getAttributes();
			Node friendlyNameAttr = attributes.getNamedItem("FriendlyName");
			if (friendlyNameAttr == null || !friendlyNameAttr.getNodeValue().equals("ProviderType")) {
				continue;
			}

			return ProviderType.valueOfType(node.getFirstChild().getNodeValue());
		}

		return DEFAULT_PROVIDER_TYPE;
	}

	protected RequestInfo getRequestInfo() {
		// method injection implemented by Spring
		return null;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}
}
