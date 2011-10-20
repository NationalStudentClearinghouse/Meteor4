package org.meteornetwork.meteor.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.saml.ext.bean.AttributeBean;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.ConditionsBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.apache.ws.security.saml.ext.bean.SubjectLocalityBean;
import org.apache.ws.security.saml.ext.builder.SAML1ComponentBuilder;
import org.apache.xpath.XPathAPI;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.meteornetwork.meteor.saml.util.DOMUtils;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml1.core.Assertion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents an old Meteor SAML assertion. The term "Meteor SAML assertion"
 * refers to Meteor's draft implementation of SAML 1. Meteor SAML is not valid
 * Saml 1 and it does not always use valid xml data types, so it requires
 * special parsing handled by this class
 * 
 * @author jlazos
 * 
 */
public class MeteorSamlSecurityToken extends SecurityTokenImpl implements SecurityToken {

	private static final Log LOG = LogFactory.getLog(MeteorSamlSecurityToken.class);

	private static Templates SAML1_METEORSAML_TEMPLATE;
	
	static {
		OpenSAMLUtil.initSamlEngine();

		InputStream samlTemplateInputStream = MeteorSamlSecurityToken.class.getResourceAsStream("saml1-meteorSaml.xsl");
		try {
			TransformerFactory transformFactory = TransformerFactory.newInstance();

			StreamSource streamSource = new StreamSource(samlTemplateInputStream);
			SAML1_METEORSAML_TEMPLATE = transformFactory.newTemplates(streamSource);
		} catch (Exception e) {
			LOG.fatal("Could not initialize saml1-meteorSaml.xsl template", e);
			SAML1_METEORSAML_TEMPLATE = null;
		} finally {
			try {
				samlTemplateInputStream.close();
			} catch (IOException e) {
				//empty catch block
			}
		}
	}

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static final String NCHELP_METEOR = "nchelp.org/meteor";
	private static final String NCHELP = "http://nchelp.org";

	private static final Integer DEFAULT_CONDITIONS_PERIOD_LENGTH_HOURS = 4;

	/**
	 * Creates a MeteorSamlSecurityToken given old Meteor SAML assertion xml.
	 * 
	 * @param xml
	 *            meteor SAML assertion XML
	 * @return token representing the assertion xml
	 * @throws SecurityTokenException
	 *             wraps any exception that occurs while processing
	 */
	public static MeteorSamlSecurityToken fromXML(String xml) throws SecurityTokenException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes(Charset.forName("utf-8")));
		try {
			Document doc = factory.newDocumentBuilder().parse(inputStream);
			return fromXML(doc.getDocumentElement());
		} catch (Exception e) {
			throw new SecurityTokenException(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new SecurityTokenException(e);
			}
		}
	}

	/**
	 * Creates a MeteorSamlSecurityToken given old Meteor SAML assertion xml.
	 * 
	 * @param tokenElem
	 *            meteor SAML assertion XML
	 * @return token representing the assertion xml
	 * @throws SecurityTokenException
	 *             wraps any exception that occurs while processing
	 */
	public static MeteorSamlSecurityToken fromXML(Element tokenElem) throws SecurityTokenException {
		MeteorSamlSecurityToken token = new MeteorSamlSecurityToken();

		try {
			Node node = XPathAPI.selectSingleNode(tokenElem, "@AssertionID");
			if (node != null) {
				token.setAssertionId(node.getFirstChild().getNodeValue());
			}

			node = XPathAPI.selectSingleNode(tokenElem, "@Issuer");
			if (node != null) {
				token.setIssuer(node.getFirstChild().getNodeValue());
			}

			node = XPathAPI.selectSingleNode(tokenElem, "AuthenticationStatement/Subject/NameIdentifier/@Name");
			if (node != null) {
				token.setSubjectName(node.getFirstChild().getNodeValue());
			}

			node = XPathAPI.selectSingleNode(tokenElem, "AuthenticationStatement/SubjectLocality");
			if (node != null) {
				Attr attr = (Attr) node.getAttributes().getNamedItem("IPAddress");
				if (attr != null) {
					token.setSubjectLocalityIpAddress(attr.getValue());
				}

				attr = (Attr) node.getAttributes().getNamedItem("DNSAddress");
				if (attr != null) {
					token.setSubjectLocalityDnsAddress(attr.getValue());
				}
			}

			node = XPathAPI.selectSingleNode(tokenElem, "Condition");
			if (node != null) {
				Attr attr = (Attr) node.getAttributes().getNamedItem("NotBefore");
				if (attr != null) {
					token.setConditionsNotBefore(getDateFromMeteorSaml(attr.getValue()));
				}

				attr = (Attr) node.getAttributes().getNamedItem("NotOnOrAfter");
				if (attr != null) {
					token.setConditionsNotOnOrAfter(getDateFromMeteorSaml(attr.getValue()));
				}
			}

			NodeList attributeNodes = XPathAPI.selectNodeList(tokenElem, "AttributeStatement/Attribute/AttributeValue");
			for (int i = 0; i < attributeNodes.getLength(); ++i) {
				node = attributeNodes.item(i);
				Attr friendlyName = (Attr) node.getParentNode().getAttributes().getNamedItem("AttributeName");

				if (friendlyName == null) {
					continue;
				}

				String friendlyNameStr = friendlyName.getValue();
				if (friendlyNameStr.equalsIgnoreCase("OrganizationID")) {
					token.setOrganizationId(node.getFirstChild().getNodeValue());
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("OrganizationIDType")) {
					token.setOrganizationIdType(node.getFirstChild().getNodeValue());
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("OrganizationType")) {
					token.setOrganizationType(node.getFirstChild().getNodeValue());
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("AuthenticationProcessID")) {
					token.setAuthenticationProcessId(node.getFirstChild().getNodeValue());
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("Level")) {
					token.setLevel(Integer.valueOf(node.getFirstChild().getNodeValue()));
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("UserHandle")) {
					token.setUserHandle(node.getFirstChild().getNodeValue());
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("Role")) {
					token.setRole(Role.valueOfName(node.getFirstChild().getNodeValue()));
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("SSN")) {
					token.setSsn(node.getFirstChild().getNodeValue());
					continue;
				}

				if (friendlyNameStr.equalsIgnoreCase("LENDER")) {
					token.setLender(node.getFirstChild().getNodeValue());
					continue;
				}
			}

		} catch (Exception e) {
			throw new SecurityTokenException(e);
		}

		return token;
	}

	private static DateTime getDateFromMeteorSaml(String dateValue) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

		return new DateTime(formatter.parse(dateValue).getTime(), DateTimeZone.UTC);
	}

	@Override
	public String toXMLString() throws SecurityTokenException {
		try {
			return DOMUtils.domToString(toXML());
		} catch (IOException e) {
			throw new SecurityTokenException(e);
		}
	}

	@Override
	public Document toXML() throws SecurityTokenException {

		try {
			DateTime currentDateTime = getCurrentDateTime();

			Assertion assertion = SAML1ComponentBuilder.createSamlv1Assertion(getIssuer());
			assertion.setID(getAssertionId());
			assertion.setIssuer(getIssuer());
			assertion.setVersion(SAMLVersion.VERSION_10);

			SubjectBean subjectBean = new SubjectBean();
			subjectBean.setSubjectName(getSubjectName());
			subjectBean.setSubjectNameQualifier(NCHELP_METEOR);

			/*
			 * Authentication statement
			 */
			AuthenticationStatementBean authStatementBean = new AuthenticationStatementBean();
			authStatementBean.setAuthenticationInstant(currentDateTime);
			authStatementBean.setAuthenticationMethod(NCHELP);
			authStatementBean.setSubject(subjectBean);

			SubjectLocalityBean subjectLocalityBean = new SubjectLocalityBean();
			subjectLocalityBean.setIpAddress(getSubjectLocalityIpAddress() == null ? InetAddress.getLocalHost().getHostAddress() : getSubjectLocalityIpAddress());
			subjectLocalityBean.setDnsAddress(getSubjectLocalityDnsAddress() == null ? InetAddress.getLocalHost().getHostName() : getSubjectLocalityDnsAddress());
			authStatementBean.setSubjectLocality(subjectLocalityBean);

			assertion.getAuthenticationStatements().addAll(SAML1ComponentBuilder.createSamlv1AuthenticationStatement(Collections.singletonList(authStatementBean)));

			/*
			 * Attributes
			 */
			List<AttributeStatementBean> attributeStatementBeans = new ArrayList<AttributeStatementBean>();

			if (getOrganizationId() != null) {
				attributeStatementBeans.add(createAttributeStatement("OrganizationID", getOrganizationId(), subjectBean));
			}

			if (getOrganizationIdType() != null) {
				attributeStatementBeans.add(createAttributeStatement("OrganizationIDType", getOrganizationIdType(), subjectBean));
			}

			if (getOrganizationType() != null) {
				attributeStatementBeans.add(createAttributeStatement("OrganizationType", getOrganizationType(), subjectBean));
			}

			if (getSsn() != null) {
				attributeStatementBeans.add(createAttributeStatement("SSN", getSsn(), subjectBean));
			}

			if (getLender() != null) {
				attributeStatementBeans.add(createAttributeStatement("LENDER", getLender(), subjectBean));
			}

			attributeStatementBeans.add(createAttributeStatement("AuthenticationProcessID", getAuthenticationProcessId(), subjectBean));
			attributeStatementBeans.add(createAttributeStatement("Level", getLevel().toString(), subjectBean));
			attributeStatementBeans.add(createAttributeStatement("UserHandle", getUserHandle(), subjectBean));
			attributeStatementBeans.add(createAttributeStatement("Role", getRole().getName(), subjectBean));

			assertion.getAttributeStatements().addAll(SAML1ComponentBuilder.createSamlv1AttributeStatement(attributeStatementBeans));

			/*
			 * Conditions
			 */
			ConditionsBean conditionsBean = new ConditionsBean();
			conditionsBean.setNotBefore(currentDateTime);
			conditionsBean.setNotAfter(currentDateTime.plusHours(DEFAULT_CONDITIONS_PERIOD_LENGTH_HOURS));

			assertion.setConditions(SAML1ComponentBuilder.createSamlv1Conditions(conditionsBean));

			Element domElement = OpenSAMLUtil.toDom(assertion, null);
			Document doc = createDocumentBuilder().newDocument();
			doc.appendChild(doc.adoptNode(domElement));

			doc = convertToMeteorSaml(doc);
			return doc;
		} catch (Exception e) {
			throw new SecurityTokenException(e);
		}

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

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}

	private Document convertToMeteorSaml(Document assertionDoc) throws ParserConfigurationException, TransformerException {
		Transformer transformer = SAML1_METEORSAML_TEMPLATE.newTransformer();

		DOMSource domSource = new DOMSource(assertionDoc);
		DOMResult domResult = new DOMResult(createDocumentBuilder().newDocument());

		transformer.transform(domSource, domResult);
		return (Document) domResult.getNode();
	}
}
