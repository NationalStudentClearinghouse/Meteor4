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
package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.ws.security.WSSecurityException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.DigitalSignatureManager;
import org.meteornetwork.meteor.common.util.exception.MeteorSecurityException;
import org.meteornetwork.meteor.saml.MeteorSamlSecurityToken;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.opensaml.xml.security.SecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Performs legacy security validation on SAML 1.0 assertions and digital
 * signatures embedded in an HPC request
 * 
 * @author jlazos
 * 
 */
@Service
public class HPCSecurityManager {

	private static final String SAML_10_NS = "http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static final String METEOR_32_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
	private static final Integer SAML_ISSUE_INSTANT_VALIDITY_PERIOD_SECONDS = 14400;

	private RegistryManager registryManager;
	private DigitalSignatureManager digitalSignatureManager;

	public String getMeteorInstitutionIdentifier(String requestXml) throws MeteorSecurityException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(requestXml.getBytes(IOUtils.UTF8_CHARSET));
		Document requestDom;
		try {
			DocumentBuilder docBuilder = createDocumentBuilder();
			requestDom = docBuilder.parse(inputStream);
		} catch (Exception e) {
			throw new MeteorSecurityException("Could not get meteor institution identifier", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new MeteorSecurityException("Could not get meteor institution identifier", e);
			}
		}
		return getMeteorInstitutionIdentifier(requestDom);
	}

	public String getMeteorInstitutionIdentifier(Document requestDom) throws MeteorSecurityException {
		Element institutionElem;
		try {
			institutionElem = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AccessProvider/MeteorInstitutionIdentifier");
			if (institutionElem == null) {
				// compatibility with Meteor 3.2
				institutionElem = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AccessProvider/ID");
			}
		} catch (Exception e) {
			throw new MeteorSecurityException("Could not get meteor institution identifier", e);
		}

		if (institutionElem == null) {
			throw new MeteorSecurityException("Request body missing meteor institution identifier");
		}

		try {
			return institutionElem.getFirstChild().getNodeValue();
		} catch (Exception e) {
			throw new MeteorSecurityException("Could not get meteor institution identifier", e);
		}
	}

	/**
	 * Creates a SAML 1 assertion and inserts it into the request XML (for HPC)
	 * 
	 * @param meteorInstitutionIdentifier
	 *            the institution issuing the assertion
	 * @param requestInfo
	 *            request-specific info to insert into SAML attributes
	 * @return the Request XML with an AssertionSpecifier element containing the
	 *         SAML assertion and signature
	 * 
	 * @throws MeteorSecurityException
	 * @throws WSSecurityException
	 * @throws SecurityException
	 * @throws UnknownHostException
	 */
	public String createSaml(String meteorInstitutionIdentifier, RequestInfo requestInfo) throws MeteorSecurityException {
		MeteorSamlSecurityToken token = new MeteorSamlSecurityToken();
		token.setIssuer(meteorInstitutionIdentifier);
		token.setSubjectName(meteorInstitutionIdentifier);
		token.setMeteorAttributes(requestInfo.getSecurityToken().getMeteorAttributes());

		try {
			return token.toXMLString();
		} catch (SecurityTokenException e) {
			throw new MeteorSecurityException("Could not create SAML security token", e);
		}
	}

	/**
	 * Sign a saml assertion and wrap in an AssertionSpecifier element
	 * 
	 * @param xml
	 *            the assertion to sign
	 * @param privateKey
	 *            the key to sign with
	 * @param cert
	 *            the certificate if it and the public key should be added to
	 *            the signature
	 * @return the signed assertion contained in a root AssertionSpecifier
	 *         element
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws MeteorSecurityException
	 */
	public String signAssertion(String xml, PrivateKey privateKey, X509Certificate cert) throws ParserConfigurationException, SAXException, IOException, MeteorSecurityException {
		DocumentBuilder docBuilder = createDocumentBuilder();
		Document doc = docBuilder.newDocument();

		Element assertionSpecifier = doc.createElement("AssertionSpecifier");
		doc.appendChild(assertionSpecifier);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes(IOUtils.UTF8_CHARSET));
		try {
			Document assertion = docBuilder.parse(inputStream);
			assertionSpecifier.appendChild(doc.importNode(assertion.getDocumentElement(), true));

			return digitalSignatureManager.sign(doc, privateKey, cert);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Combine AssertionSpecifier and body into single xml and sign it
	 * 
	 * @param xmlBody
	 *            the xml body to sign
	 * @param xmlAssertionSpecifier
	 *            the AssertionSpecifier element to insert into the xml body
	 *            before signing
	 * @param privateKey
	 *            the key to sign with
	 * @param cert
	 *            the certificate if it and the public key should be added to
	 *            the signature
	 * @return the signed xml body
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws MeteorSecurityException
	 */
	public String signBody(String xmlBody, String xmlAssertionSpecifier, PrivateKey privateKey, X509Certificate cert) throws SAXException, IOException, ParserConfigurationException, MeteorSecurityException {
		DocumentBuilder docBuilder = createDocumentBuilder();
		ByteArrayInputStream bodyInStream = new ByteArrayInputStream(xmlBody.getBytes(IOUtils.UTF8_CHARSET));
		ByteArrayInputStream assertionInStream = new ByteArrayInputStream(xmlAssertionSpecifier.getBytes(IOUtils.UTF8_CHARSET));

		try {
			Document doc = docBuilder.parse(bodyInStream);

			Document assertionSpecifier = docBuilder.parse(assertionInStream);
			doc.getDocumentElement().appendChild(doc.importNode(assertionSpecifier.getDocumentElement(), true));

			return digitalSignatureManager.sign(doc, privateKey, cert);
		} finally {
			bodyInStream.close();
			assertionInStream.close();
		}
	}

	/**
	 * Validates the signatures on an incoming HPC request
	 * 
	 * @param requestXml
	 * @throws MeteorSecurityException
	 */
	public void validateRequest(String requestXml) throws MeteorSecurityException {

		ByteArrayInputStream inputStream = new ByteArrayInputStream(requestXml.getBytes(IOUtils.UTF8_CHARSET));
		try {
			DocumentBuilder docBuilder = createDocumentBuilder();
			Document requestDom = docBuilder.parse(inputStream);

			Element sigNSContext = XMLUtils.createDSctx(requestDom, "ds", Constants.SignatureSpecNS);
			Element samlNSContext = XMLUtils.createDSctx(requestDom, "saml", SAML_10_NS);

			verifyAssertionSignature(requestDom, sigNSContext, samlNSContext);
			verifyBodySignature(requestDom, sigNSContext, samlNSContext);
		} catch (Exception e) {
			throw new MeteorSecurityException("Cannot validate HPC request", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new MeteorSecurityException("Cannot validate HPC request", e);
			}
		}
	}

	private void verifyAssertionSignature(Document requestDom, Element sigNSContext, Element samlNSContext) throws TransformerException, RegistryException, MeteorSecurityException, XMLSignatureException, XMLSecurityException, ParserConfigurationException {
		DocumentBuilder docBuilder = createDocumentBuilder();
		Document assertionDoc = docBuilder.newDocument();
		assertionDoc.appendChild(assertionDoc.importNode(XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier"), true));

		Element signature = (Element) XPathAPI.selectSingleNode(assertionDoc, "AssertionSpecifier/ds:Signature", sigNSContext);
		if (signature == null) {
			throw new MeteorSecurityException("SAML assertion missing digital signature");
		}

		Attr institutionAttr = (Attr) XPathAPI.selectSingleNode(assertionDoc, "AssertionSpecifier/saml:Assertion/saml:AuthenticationStatement/saml:Subject/saml:NameIdentifier/@Name", samlNSContext);
		if (institutionAttr == null) {
			throw new MeteorSecurityException("SAML assertion missing institution name from subject");
		}
		String institutionId = institutionAttr.getFirstChild().getNodeValue();

		X509Certificate cert = registryManager.getCertificate(institutionId, ProviderType.ACCESS);
		verifySignature(signature, cert);
	}

	private void verifyBodySignature(Document requestDom, Element sigNSContext, Element samlNSContext) throws TransformerException, MeteorSecurityException, RegistryException, XMLSignatureException, XMLSecurityException {
		Element signature = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/ds:Signature", sigNSContext);
		if (signature == null) {
			// If Meteor 3.2, body is not signed.
			// Meteor 3.2 assertion does not contain condition element.
			Element condition = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/saml:Assertion/saml:Condition", samlNSContext);
			if (condition == null) {
				return;
			}

			throw new MeteorSecurityException("Request body missing digital signature");
		}

		String institutionId = getMeteorInstitutionIdentifier(requestDom);

		X509Certificate cert = registryManager.getCertificate(institutionId, ProviderType.ACCESS);
		verifySignature(signature, cert);
	}

	private void verifySignature(Element signatureNode, X509Certificate cert) throws XMLSignatureException, XMLSecurityException, MeteorSecurityException {
		XMLSignature signature = new XMLSignature(signatureNode, "");

		if (!signature.checkSignatureValue(cert)) {
			throw new MeteorSecurityException("Invalid signature on incoming HPC request");
		}
	}

	public void verifyAssertionExpiry(String requestXml) throws MeteorSecurityException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(requestXml.getBytes(IOUtils.UTF8_CHARSET));
		try {
			DocumentBuilder docBuilder = createDocumentBuilder();
			Document requestDom = docBuilder.parse(inputStream);

			Element samlNSContext = XMLUtils.createDSctx(requestDom, "saml", SAML_10_NS);

			verifyAssertionExpiry(requestDom, samlNSContext);
		} catch (Exception e) {
			throw new MeteorSecurityException("Cannot validate assertion expiry", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new MeteorSecurityException("Cannot validate assertion expiry", e);
			}
		}
	}

	private void verifyAssertionExpiry(Document requestDom, Element samlNSContext) throws TransformerException, ParseException, MeteorSecurityException {
		Element condition = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/saml:Assertion/saml:Condition", samlNSContext);

		if (condition == null) {
			// Meteor 3.2 compatibility
			verifyAssertionExpiryIssueInstant(requestDom, samlNSContext);
			return;
		}

		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		String notBeforeStr = condition.getAttribute("NotBefore");
		String notAfterStr = condition.getAttribute("NotOnOrAfter");
		Date notBefore = dateFormatter.parse(notBeforeStr);
		Date notOnOrAfter = dateFormatter.parse(notAfterStr);

		Date currentTime = Calendar.getInstance().getTime();
		if (currentTime.before(notBefore)) {
			throw new MeteorSecurityException("Current time " + dateFormatter.format(currentTime) + " is before Assertion Condition@NotBefore " + notBeforeStr);
		}
		if (!currentTime.before(notOnOrAfter)) {
			throw new MeteorSecurityException("Current time " + dateFormatter.format(currentTime) + " is equal to or after Assertion Condition@NotOnOrAfter " + notAfterStr);
		}
	}

	private void verifyAssertionExpiryIssueInstant(Document requestDom, Element samlNSContext) throws TransformerException, ParseException, MeteorSecurityException {
		Element assertion = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/saml:Assertion", samlNSContext);

		SimpleDateFormat dateFormatter = new SimpleDateFormat(METEOR_32_DATE_FORMAT, Locale.US);
		String issueInstantStr = assertion.getAttribute("IssueInstant");
		Date issueInstant = dateFormatter.parse(issueInstantStr);

		Calendar currentTimeCal = Calendar.getInstance();

		Calendar issueInstantCal = Calendar.getInstance();
		issueInstantCal.setTime(issueInstant);

		// Meteor 3.2 issue instant does not distinguish between AM or PM. This
		// means in the afternoon, a Meteor 3.2. assertion will never be valid.
		// So to allow validation to be successful in the afternoon, arbitrarily
		// add 12 hours to issue instant. This creates a minor flaw in assertion
		// expiration checking such that in a given day, an assertion will be
		// valid for two periods -- one before noon and one after noon. But, it
		// allows assertions to be valid during the afternoon.

		if (currentTimeCal.get(Calendar.HOUR_OF_DAY) > 12) {
			issueInstantCal.add(Calendar.HOUR_OF_DAY, 12);
			issueInstant = issueInstantCal.getTime();
		}
		issueInstantCal.add(Calendar.SECOND, SAML_ISSUE_INSTANT_VALIDITY_PERIOD_SECONDS);
		Date expiryTime = issueInstantCal.getTime();

		if (currentTimeCal.getTime().before(issueInstant)) {
			throw new MeteorSecurityException("Current time " + dateFormatter.format(currentTimeCal.getTime()) + " is before Assertion@IssueInstant " + dateFormatter.format(issueInstant));
		}
		if (!currentTimeCal.getTime().before(expiryTime)) {
			throw new MeteorSecurityException("Current time " + dateFormatter.format(currentTimeCal.getTime()) + " is not within the assertion validity period, which ended " + dateFormatter.format(expiryTime));
		}
	}

	public MeteorSamlSecurityToken getSecurityToken(String requestXml) throws MeteorSecurityException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(requestXml.getBytes(IOUtils.UTF8_CHARSET));
		try {
			DocumentBuilder docBuilder = createDocumentBuilder(false);
			Document requestDom = docBuilder.parse(inputStream);

			Element assertionElement = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/Assertion");

			return MeteorSamlSecurityToken.fromXML(assertionElement);
		} catch (Exception e) {
			throw new MeteorSecurityException("Cannot get security token", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new MeteorSecurityException("Cannot get security token", e);
			}
		}
	}

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		return createDocumentBuilder(true);
	}

	private DocumentBuilder createDocumentBuilder(boolean namespaceAware) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(namespaceAware);
		return factory.newDocumentBuilder();
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	public DigitalSignatureManager getDigitalSignatureManager() {
		return digitalSignatureManager;
	}

	@Autowired
	public void setDigitalSignatureManager(DigitalSignatureManager digitalSignatureManager) {
		this.digitalSignatureManager = digitalSignatureManager;
	}

}
