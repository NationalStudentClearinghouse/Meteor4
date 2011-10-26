package org.meteornetwork.meteor.common.ws.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.SignatureProcessor;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.saml.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Extends WSS4J SignatureProcessor to retrieve public certificates from the
 * Meteor registry. Assumes crypto provider is MeteorCertificateStore
 * 
 * @author jlazos
 * 
 */
public class MeteorSignatureProcessor extends SignatureProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MeteorSignatureProcessor.class);

	private RegistryManager registryManager;

	private static final ProviderType DEFAULT_PROVIDER_TYPE = ProviderType.ACCESS;

	@Override
	public List<WSSecurityEngineResult> handleToken(Element elem, RequestData data, WSDocInfo wsDocInfo) throws WSSecurityException {
		assert data.getSigCrypto() instanceof MeteorCertificateStore : "Signature crypto implementation is not MeteorCertificateStore";

		addCertificateToCrypto(elem, (MeteorCertificateStore) data.getSigCrypto());
		try {
			return super.handleToken(elem, data, wsDocInfo);
		} catch (WSSecurityException e) {
			LOG.debug("Could not handle token", e);
			throw new WSSecurityException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef(), e);
		}
	}

	/**
	 * Gets Meteor Insititution Identifier out of the SAML assertion and fetches
	 * the public key from the registry
	 * 
	 * @param token
	 * @param crypto
	 * @throws WSSecurityException
	 */
	private void addCertificateToCrypto(Element elem, MeteorCertificateStore crypto) throws WSSecurityException {
		NodeList securityHeaderNodes = elem.getParentNode().getChildNodes();
		Element token = null;
		for (int i = 0; i < securityHeaderNodes.getLength(); ++i) {
			if ("Assertion".equalsIgnoreCase(securityHeaderNodes.item(i).getLocalName())) {
				token = (Element) securityHeaderNodes.item(i);
				break;
			}
		}

		if (token == null) {
			throw new WSSecurityException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef());
		}

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
		NodeList elements = token.getElementsByTagNameNS(WSConstants.SAML2_NS, "Attribute");
		if (elements == null) {
			return DEFAULT_PROVIDER_TYPE;
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

		return DEFAULT_PROVIDER_TYPE;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
