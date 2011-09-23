package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.saml.ext.bean.AttributeBean;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.ConditionsBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.apache.ws.security.saml.ext.bean.SubjectLocalityBean;
import org.apache.ws.security.saml.ext.builder.SAML1ComponentBuilder;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.exception.MeteorSecurityException;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml1.core.Assertion;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	private static final String NCHELP_METEOR = "nchelp.org/meteor";
	private static final String NCHELP = "http://nchelp.org";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	private RegistryManager registryManager;

	/**
	 * Creates a SAML 1 assertion and inserts it into the request XML (for HPC)
	 * 
	 * @param meteorInstitutionIdentifier
	 *            the institution issuing the assertion
	 * @param requestInfo
	 *            request-specific info to insert into SAML attributes
	 * @param requestXml
	 *            the request xml to insert the assertion into
	 * @return the Request XML with an AssertionSpecifier element containing the
	 *         SAML assertion and signature
	 * 
	 * @throws MeteorSecurityException
	 * @throws WSSecurityException
	 * @throws SecurityException
	 * @throws UnknownHostException
	 */
	public String createSaml(String meteorInstitutionIdentifier, String authenticationProcessId, RequestInfo requestInfo) throws MeteorSecurityException {
		try {
			XMLObject assertion = createSamlXMLObject(meteorInstitutionIdentifier, authenticationProcessId, requestInfo);

			return domToString(OpenSAMLUtil.toDom(assertion, null));
		} catch (Exception e) {
			throw new MeteorSecurityException("Could not create SAML assertion", e);
		}
	}

	private XMLObject createSamlXMLObject(String meteorInstitutionIdentifier, String authenticationProcessId, RequestInfo requestInfo) throws UnknownHostException, SecurityException, WSSecurityException {
		Assertion assertion = SAML1ComponentBuilder.createSamlv1Assertion(NCHELP_METEOR);
		assertion.setVersion(SAMLVersion.VERSION_10);

		SubjectBean subjectBean = new SubjectBean();
		subjectBean.setSubjectName(meteorInstitutionIdentifier);
		subjectBean.setSubjectNameQualifier(NCHELP_METEOR);

		DateTime currentDateTime = new DateTime(DateTimeZone.UTC);

		/*
		 * Authentication statement
		 */
		AuthenticationStatementBean authStatementBean = new AuthenticationStatementBean();
		authStatementBean.setAuthenticationInstant(currentDateTime);
		authStatementBean.setAuthenticationMethod(NCHELP);
		authStatementBean.setSubject(subjectBean);
		SubjectLocalityBean subjectLocalityBean = new SubjectLocalityBean();
		subjectLocalityBean.setIpAddress(InetAddress.getLocalHost().getHostAddress());
		subjectLocalityBean.setDnsAddress(InetAddress.getLocalHost().getHostName());
		authStatementBean.setSubjectLocality(subjectLocalityBean);

		assertion.getAuthenticationStatements().addAll(SAML1ComponentBuilder.createSamlv1AuthenticationStatement(Collections.singletonList(authStatementBean)));

		/*
		 * Attributes
		 */
		List<AttributeStatementBean> attributeStatementBeans = new ArrayList<AttributeStatementBean>();

		SecurityToken token = requestInfo.getSecurityToken();
		if (token.getOrganizationId() != null) {
			attributeStatementBeans.add(createAttributeStatement("OrganizationID", token.getOrganizationId(), subjectBean));
		}

		if (token.getOrganizationIdType() != null) {
			attributeStatementBeans.add(createAttributeStatement("OrganizationIDType", token.getOrganizationIdType(), subjectBean));
		}

		if (token.getOrganizationType() != null) {
			attributeStatementBeans.add(createAttributeStatement("OrganizationType", token.getOrganizationType(), subjectBean));
		}

		attributeStatementBeans.add(createAttributeStatement("AuthenticationProcessID", authenticationProcessId, subjectBean));
		attributeStatementBeans.add(createAttributeStatement("Level", token.getLevel().toString(), subjectBean));
		attributeStatementBeans.add(createAttributeStatement("UserHandle", token.getUserHandle(), subjectBean));
		attributeStatementBeans.add(createAttributeStatement("Role", token.getRole().getName(), subjectBean));

		assertion.getAttributeStatements().addAll(SAML1ComponentBuilder.createSamlv1AttributeStatement(attributeStatementBeans));

		/*
		 * Conditions
		 */
		ConditionsBean conditionsBean = new ConditionsBean();
		conditionsBean.setNotBefore(currentDateTime);
		conditionsBean.setNotAfter(currentDateTime.plusHours(4));

		assertion.setConditions(SAML1ComponentBuilder.createSamlv1Conditions(conditionsBean));

		return assertion;
	}

	private AttributeStatementBean createAttributeStatement(String simpleName, String value, SubjectBean subjectBean) {
		AttributeStatementBean attributeStatementBean = new AttributeStatementBean();
		AttributeBean attributeBean = new AttributeBean();
		attributeBean.setSimpleName(simpleName);
		attributeBean.setAttributeValues(Collections.singletonList(value));

		attributeStatementBean.getSamlAttributes().add(attributeBean);
		attributeStatementBean.setSubject(subjectBean);
		return attributeStatementBean;
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

		Document assertion = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(IOUtils.UTF8_CHARSET)));
		assertionSpecifier.appendChild(doc.importNode(assertion.getDocumentElement(), true));

		return sign(doc, privateKey, cert);
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
		Document doc = docBuilder.parse(new ByteArrayInputStream(xmlBody.getBytes(IOUtils.UTF8_CHARSET)));

		Document assertionSpecifier = docBuilder.parse(new ByteArrayInputStream(xmlAssertionSpecifier.getBytes(IOUtils.UTF8_CHARSET)));
		doc.getDocumentElement().appendChild(doc.importNode(assertionSpecifier.getDocumentElement(), true));

		return sign(doc, privateKey, cert);
	}

	private String sign(Document doc, PrivateKey privateKey, X509Certificate cert) throws MeteorSecurityException, IOException {
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

		return domToString(doc);
	}

	/**
	 * Validates the SAML assertion and signatures on an incoming HPC request
	 * 
	 * @param requestXml
	 * @throws MeteorSecurityException
	 */
	public void validateRequest(String requestXml) throws MeteorSecurityException {

		try {
			DocumentBuilder docBuilder = createDocumentBuilder();
			Document requestDom = docBuilder.parse(new ByteArrayInputStream(requestXml.getBytes(IOUtils.UTF8_CHARSET)));

			Element sigNSContext = XMLUtils.createDSctx(requestDom, "ds", Constants.SignatureSpecNS);
			Element samlNSContext = XMLUtils.createDSctx(requestDom, "saml", SAML_10_NS);

			verifyAssertionSignature(requestDom, sigNSContext, samlNSContext);
			verifyBodySignature(requestDom, sigNSContext);
			verifyAssertionExpiry(requestDom, samlNSContext);
		} catch (Exception e) {
			throw new MeteorSecurityException("Cannot validate HPC request", e);
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

		// TODO: support status provider
		X509Certificate cert = registryManager.getCertificate(institutionId, ProviderType.ACCESS);
		verifySignature(signature, cert);
	}

	private void verifyBodySignature(Document requestDom, Element sigNSContext) throws TransformerException, MeteorSecurityException, RegistryException, XMLSignatureException, XMLSecurityException {
		Element signature = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/ds:Signature", sigNSContext);
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

	private void verifyAssertionExpiry(Document requestDom, Element samlNSContext) throws TransformerException, ParseException, MeteorSecurityException {
		Element condition = (Element) XPathAPI.selectSingleNode(requestDom, "MeteorDataRequest/AssertionSpecifier/saml:Assertion/saml:Condition", samlNSContext);

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

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}

	private String domToString(Node dom) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		XMLUtils.outputDOM(dom, outputStream);
		String xml = outputStream.toString();
		outputStream.close();
		return xml;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
