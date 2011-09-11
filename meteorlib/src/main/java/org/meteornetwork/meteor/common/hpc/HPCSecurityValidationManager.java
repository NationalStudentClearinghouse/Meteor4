package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.meteornetwork.meteor.common.registry.ProviderType;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.util.exception.MeteorSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * Performs legacy security validation on SAML 1.0 assertions and digital
 * signatures embedded in an HPC request
 * 
 * @author jlazos
 * 
 */
@Service
public class HPCSecurityValidationManager {

	//private static final String SAML_10_NS = "http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd";

	private RegistryManager registryManager;

	/**
	 * Validates the SAML assertion and signatures on an incoming HPC request
	 * 
	 * @param requestXml
	 * @throws MeteorSecurityException
	 */
	public void validateRequest(String requestXml) throws MeteorSecurityException {

		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document requestDom = docBuilder.parse(new ByteArrayInputStream(requestXml.getBytes(IOUtils.UTF8_CHARSET)));

			Element sigNSContext = XMLUtils.createDSctx(requestDom, "ds", Constants.SignatureSpecNS);

			verifyAssertionSignature(requestDom, sigNSContext);
			verifyBodySignature(requestDom, sigNSContext);
		} catch (Exception e) {
			throw new MeteorSecurityException("Cannot validate HPC request", e);
		}
	}

	private void verifyAssertionSignature(Document requestDom, Element sigNSContext) throws TransformerException, RegistryException, MeteorSecurityException, XMLSignatureException, XMLSecurityException {
		Element signature = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/Signature", sigNSContext);
		if (signature == null) {
			throw new MeteorSecurityException("SAML assertion missing digital signature");
		}

		Attr institutionAttr = (Attr) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/Assertion/AuthenticationStatement/Subject/NameIdentifier/@Name");
		if (institutionAttr == null) {
			throw new MeteorSecurityException("SAML assertion missing institution name from subject");
		}
		String institutionId = institutionAttr.getFirstChild().getNodeValue();

		// TODO: support status provider
		X509Certificate cert = registryManager.getCertificate(institutionId, ProviderType.ACCESS);
		verifySignature(signature, cert);
	}

	private void verifyBodySignature(Document requestDom, Element sigNSContext) throws TransformerException, MeteorSecurityException, RegistryException, XMLSignatureException, XMLSecurityException {
		Element signature = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/Signature", sigNSContext);
		if (signature == null) {
			throw new MeteorSecurityException("Request body missing digital signature");
		}

		Element institutionElem = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AccessProvider/MeteorInstitutionIdentifier");
		if (institutionElem == null) {
			throw new MeteorSecurityException("Request body missing meteor institution identifier");
		}
		String institutionId = institutionElem.getFirstChild().getNodeValue();

		X509Certificate cert = registryManager.getCertificate(institutionId, ProviderType.ACCESS);
		verifySignature(signature, cert);
	}

	private void verifySignature(Element signatureNode, X509Certificate cert) throws XMLSignatureException, XMLSecurityException, MeteorSecurityException {
		XMLSignature signature = new XMLSignature(signatureNode, "");

		if (!signature.checkSignatureValue(cert)) {
			throw new MeteorSecurityException("Invalid signature on incoming HPC request");
		}
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}
}
