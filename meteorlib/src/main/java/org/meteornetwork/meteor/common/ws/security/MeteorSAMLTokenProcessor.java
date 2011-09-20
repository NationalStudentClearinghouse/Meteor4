package org.meteornetwork.meteor.common.ws.security;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.SAMLTokenProcessor;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.meteornetwork.meteor.common.registry.ProviderType;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
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
public class MeteorSAMLTokenProcessor extends SAMLTokenProcessor {

	private RegistryManager registryManager;

	private static final ProviderType defaultProviderType = ProviderType.ACCESS;

	@Override
	public Credential handleSAMLToken(Element token, RequestData data, Validator validator, WSDocInfo docInfo) throws WSSecurityException {
		if (!(data.getSigCrypto() instanceof MeteorCertificateStore)) {
			throw new WSSecurityException("Signature crypto implementation is not MeteorCertificateStore");
		}

		MeteorCertificateStore crypto = (MeteorCertificateStore) data.getSigCrypto();
		addCertificateToCrypto(token, crypto);

		return super.handleSAMLToken(token, data, validator, docInfo);
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
			crypto.addCertificate(registryManager.getCertificate(institutionID, providerType));
		} catch (RegistryException e) {
			throw new WSSecurityException("Could not get X509 certificate from Meteor registry for institution " + institutionID, e);
		}
	}

	private String getMeteorInstitutionIdentifier(Element token) throws WSSecurityException {
		NodeList elements = token.getElementsByTagNameNS(WSConstants.SAML2_NS, "NameID");
		if (elements == null) {
			throw new WSSecurityException("Cannot validate SAML assertion - subject name id is missing");
		}

		for (int i = 0; i < elements.getLength(); ++i) {
			Node node = elements.item(i);
			if (node.getParentNode() != null && node.getParentNode().getNamespaceURI().equals(WSConstants.SAML2_NS) && node.getParentNode().getLocalName().equals("Subject")) {
				return node.getFirstChild().getNodeValue();
			}
		}

		throw new WSSecurityException("Cannot validate SAML assertion - subject name id is missing");
	}

	private ProviderType getProviderType(Element token) {
		NodeList elements = token.getElementsByTagNameNS(WSConstants.SAML2_NS, "Attribute");
		if (elements == null) {
			return defaultProviderType;
		}

		for (int i = 0; i < elements.getLength(); ++i) {
			Node node = elements.item(i);
			if (node.getParentNode() != null && node.getParentNode().getNamespaceURI().equals(WSConstants.SAML2_NS) && node.getParentNode().getLocalName().equals("AttributeStatement")) {
				NamedNodeMap attributes = node.getAttributes();
				Node friendlyNameAttr = attributes.getNamedItem("FriendlyName");
				if (friendlyNameAttr == null || !friendlyNameAttr.getNodeValue().equals("ProviderType")) {
					continue;
				}

				return ProviderType.valueOfType(node.getFirstChild().getFirstChild().getNodeValue());
			}
		}

		return defaultProviderType;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}
}
