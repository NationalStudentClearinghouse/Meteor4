package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xpath.XPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HPCSecurityValidationManagerTest {

	private static final String SAML_10_NS = "http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd";
	
	@Test
	public void testValidateRequest() throws SAXException, IOException, ParserConfigurationException, TransformerException, XMLSignatureException, XMLSecurityException {
		Init.init();
		
		String xml = "<MeteorDataRequest><AssertionSpecifier><saml:Assertion xmlns:saml=\"http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd\" AssertionID=\"1312475061493\" IssueInstant=\"2011-08-04T16:24:21+0000\" Issuer=\"nchelp.org/meteor\" MajorVersion=\"1\" MinorVersion=\"0\"><saml:AuthenticationStatement AuthenticationInstant=\"2011-08-04T16:24:21+0000\" AuthenticationMethod=\"http://nchelp.org\"><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:AuthenticationLocality DNSAddress=\"lticslt-20\" IPAddress=\"192.168.100.161\"></saml:AuthenticationLocality></saml:AuthenticationStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"OrganizationIDType\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>SCHOOL</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"AuthenticationProcessID\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>1</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"OrganizationType\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>SCHOOL</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"OrganizationID\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>OPEID</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"UserHandle\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>faa</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"Level\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>3</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:AttributeStatement><saml:Subject><saml:NameIdentifier Name=\"ap\" SecurityDomain=\"nchelp.org/meteor\"></saml:NameIdentifier></saml:Subject><saml:Attribute AttributeName=\"Role\" AttributeNamespace=\"nchelp.org/meteor\"><saml:AttributeValue>FAA</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><saml:Condition NotBefore=\"2011-08-04T16:24:21+0000\" NotOnOrAfter=\"2011-08-04T20:24:21+0000\"></saml:Condition></saml:Assertion><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></ds:CanonicalizationMethod><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#dsa-sha1\"></ds:SignatureMethod><ds:Reference URI=\"\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform><ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod><ds:DigestValue>MAX0pQJjOA7KLYAXhkHQjlwe54E=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>RFgNwCsrpkH4Wztp/Mr+5AHylgqKr0PozpYiJAI9R1N5gB9iD1gU7g==</ds:SignatureValue></ds:Signature></AssertionSpecifier><AccessProvider><MeteorInstitutionIdentifier>ap</MeteorInstitutionIdentifier><ID>ap</ID><IssueInstant>2011-08-04T16:24:22+0000</IssueInstant><UserHandle>faa</UserHandle></AccessProvider><SSN>101011001</SSN><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></ds:CanonicalizationMethod><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#dsa-sha1\"></ds:SignatureMethod><ds:Reference URI=\"\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform><ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod><ds:DigestValue>Dlut0m65fo1iqUmpTL1fqolOyR4=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>D6LSVim0i1Qh2PuK3kCvKwQjl3onfwftuUxtjh4dDsAucjzWiPoJNw==</ds:SignatureValue></ds:Signature></MeteorDataRequest>";
		
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document requestDom = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(IOUtils.UTF8_CHARSET)));
		
		NodeList signatureNodes = XPathAPI.selectNodeList(requestDom, "MeteorDataRequest/AssertionSpecifier/Signature");
		Node sigNode = signatureNodes.item(0);
		System.out.println(sigNode.getLocalName());
		System.out.println(sigNode.getNamespaceURI());
		XMLSignature signature = new XMLSignature((Element) sigNode, "");
	}

}
