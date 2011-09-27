package org.meteornetwork.meteor.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collections;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.saml.ext.bean.AttributeBean;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.ConditionsBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.apache.ws.security.saml.ext.bean.SubjectLocalityBean;
import org.apache.ws.security.saml.ext.builder.SAML2ComponentBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.meteornetwork.meteor.saml.util.DOMUtils;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * Represents a SAML 2.0 Assertion with Meteor attributes
 * 
 * @author jlazos
 * 
 */
public class SecurityTokenImpl implements SecurityToken {

	static {
		OpenSAMLUtil.initSamlEngine();
	}

	protected static final SAMLVersion SAML_VERSION = SAMLVersion.VERSION_20;

	private String assertionId;
	private String issuer;
	private String subjectName;
	private String subjectLocalityIpAddress;
	private String subjectLocalityDnsAddress;

	private static final Integer VALIDITY_PERIOD_DEFAULT = 14400;
	private DateTime conditionsNotBefore;
	private DateTime conditionsNotOnOrAfter;

	private ProviderType providerType;
	private String organizationId;
	private String organizationIdType;
	private String organizationType;
	private String authenticationProcessId;
	private Integer level;
	private String userHandle;
	private Role role;
	private String ssn;
	private String lender;

	/**
	 * Creates a SecurityTokenImpl given valid SAML 2.0 assertion xml.
	 * 
	 * @param xml
	 *            valid SAML 2.0 assertion with meteor attributes
	 * @return token representing the assertion xml
	 * @throws SecurityTokenException
	 *             wraps any exception that occurs while processing
	 */
	public static SecurityTokenImpl fromXML(String xml) throws SecurityTokenException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(Charset.forName("utf-8"))));
			return fromXML(doc.getDocumentElement());
		} catch (Exception e) {
			throw new SecurityTokenException(e);
		}
	}

	/**
	 * Creates a SecurityTokenImpl given valid SAML 2.0 assertion xml.
	 * 
	 * @param tokenElem
	 *            valid SAML 2.0 assertion with meteor attributes
	 * @return token representing the assertion xml
	 * @throws SecurityTokenException
	 *             wraps any exception that occurs while processing
	 */
	public static SecurityTokenImpl fromXML(Element tokenElem) throws SecurityTokenException {
		SecurityTokenImpl token = new SecurityTokenImpl();

		DatatypeFactory datatypeFactory;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e1) {
			throw new SecurityTokenException(e1);
		}

		try {
			Node node = XPathAPI.selectSingleNode(tokenElem, "Issuer");
			if (node != null) {
				token.setIssuer(node.getFirstChild().getNodeValue());
			}

			node = XPathAPI.selectSingleNode(tokenElem, "Subject/NameID");
			if (node != null) {
				token.setSubjectName(node.getFirstChild().getNodeValue());
			}

			node = XPathAPI.selectSingleNode(tokenElem, "AuthnStatement/SubjectLocality");
			if (node != null) {
				Attr attr = (Attr) node.getAttributes().getNamedItem("Address");
				if (attr != null) {
					token.setSubjectLocalityIpAddress(attr.getValue());
				}

				attr = (Attr) node.getAttributes().getNamedItem("DNSName");
				if (attr != null) {
					token.setSubjectLocalityDnsAddress(attr.getValue());
				}
			}

			node = XPathAPI.selectSingleNode(tokenElem, "Conditions");
			if (node != null) {
				Attr attr = (Attr) node.getAttributes().getNamedItem("NotBefore");
				if (attr != null) {
					token.setConditionsNotBefore(new DateTime(datatypeFactory.newXMLGregorianCalendar(attr.getValue()).toGregorianCalendar().getTime().getTime(), DateTimeZone.UTC));
				}

				attr = (Attr) node.getAttributes().getNamedItem("NotOnOrAfter");
				if (attr != null) {
					token.setConditionsNotOnOrAfter(new DateTime(datatypeFactory.newXMLGregorianCalendar(attr.getValue()).toGregorianCalendar().getTime().getTime(), DateTimeZone.UTC));
				}
			}

			NodeList attributeNodes = XPathAPI.selectNodeList(tokenElem, "AttributeStatement/Attribute/AttributeValue");
			for (int i = 0; i < attributeNodes.getLength(); ++i) {
				node = attributeNodes.item(i);
				Attr friendlyName = (Attr) node.getParentNode().getAttributes().getNamedItem("FriendlyName");

				if (friendlyName == null) {
					continue;
				}

				String friendlyNameStr = friendlyName.getValue();
				if (friendlyNameStr.equalsIgnoreCase("ProviderType")) {
					token.setProviderType(ProviderType.valueOfType(node.getFirstChild().getNodeValue()));
					continue;
				}

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

			Assertion assertion = SAML2ComponentBuilder.createAssertion();
			if (issuer != null) {
				assertion.setIssuer(SAML2ComponentBuilder.createIssuer(issuer));
			}

			assertion.setSubject(SAML2ComponentBuilder.createSaml2Subject(createSubjectBean()));
			assertion.setConditions(SAML2ComponentBuilder.createConditions(createConditionsBean(currentDateTime)));
			assertion.getAuthnStatements().addAll(SAML2ComponentBuilder.createAuthnStatement(Collections.singletonList(createAuthenticationStatementBean(currentDateTime))));
			assertion.getAttributeStatements().addAll(SAML2ComponentBuilder.createAttributeStatement(Collections.singletonList(createAttributeStatementBean())));

			Element domElement = OpenSAMLUtil.toDom(assertion, null);
			Document doc = createDocumentBuilder().newDocument();
			doc.appendChild(doc.adoptNode(domElement));

			return doc;
		} catch (Exception e) {
			throw new SecurityTokenException(e);
		}

	}

	protected DateTime getCurrentDateTime() {
		return new DateTime(DateTimeZone.UTC);
	}

	protected SubjectBean createSubjectBean() {
		SubjectBean subject = new SubjectBean();
		subject.setSubjectName(subjectName);
		return subject;
	}

	protected ConditionsBean createConditionsBean(DateTime currentDateTime) throws SecurityTokenException {
		ConditionsBean conditions = new ConditionsBean();
		DateTime notBefore = conditionsNotBefore == null ? getCurrentDateTime() : conditionsNotBefore;
		DateTime notOnOrAfter = conditionsNotOnOrAfter == null ? notBefore.plusSeconds(VALIDITY_PERIOD_DEFAULT) : conditionsNotOnOrAfter;

		if (notOnOrAfter.isBefore(notBefore.getMillis())) {
			throw new SecurityTokenException("Conditions NotOnOrAfter (" + notOnOrAfter.getMillis() + " since epoch) is before Conditions NotBefore (" + notBefore.getMillis() + " since epoch)");
		}
		conditions.setNotBefore(notBefore);
		conditions.setNotAfter(notOnOrAfter);
		return conditions;
	}

	protected AuthenticationStatementBean createAuthenticationStatementBean(DateTime currentDateTime) {
		AuthenticationStatementBean authenticationStatement = new AuthenticationStatementBean();
		authenticationStatement.setAuthenticationInstant(currentDateTime);

		try {
			SubjectLocalityBean subjectLocality = new SubjectLocalityBean();
			subjectLocality.setIpAddress(subjectLocalityIpAddress == null ? InetAddress.getLocalHost().getHostAddress() : subjectLocalityIpAddress);
			subjectLocality.setDnsAddress(subjectLocalityDnsAddress == null ? InetAddress.getLocalHost().getHostName() : subjectLocalityDnsAddress);
			authenticationStatement.setSubjectLocality(subjectLocality);
		} catch (UnknownHostException e) {
			// do not set subject locality if ip and dns addresses cannot be
			// resolved
		}

		return authenticationStatement;
	}

	protected AttributeStatementBean createAttributeStatementBean() {
		AttributeStatementBean attributeStatement = new AttributeStatementBean();

		if (providerType != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("ProviderType", providerType.getType()));
		}

		if (organizationId != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationID", organizationId));
		}

		if (organizationIdType != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationIDType", organizationIdType));
		}

		if (organizationType != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationType", organizationType));
		}

		if (ssn != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("SSN", ssn));
		}

		if (lender != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("LENDER", lender));
		}
		
		attributeStatement.getSamlAttributes().add(createAttributeBean("AuthenticationProcessID", authenticationProcessId));
		attributeStatement.getSamlAttributes().add(createAttributeBean("Level", Integer.toString(level)));
		attributeStatement.getSamlAttributes().add(createAttributeBean("UserHandle", userHandle));
		attributeStatement.getSamlAttributes().add(createAttributeBean("Role", role.getName()));

		return attributeStatement;
	}

	private AttributeBean createAttributeBean(String simpleName, String value) {
		AttributeBean attributeBean = new AttributeBean();
		attributeBean.setSimpleName(simpleName);
		attributeBean.setAttributeValues(Collections.singletonList(value));
		return attributeBean;
	}

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}

	public String getAssertionId() {
		return assertionId;
	}

	public void setAssertionId(String assertionId) {
		this.assertionId = assertionId;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectLocalityIpAddress() {
		return subjectLocalityIpAddress;
	}

	public void setSubjectLocalityIpAddress(String subjectLocalityIpAddress) {
		this.subjectLocalityIpAddress = subjectLocalityIpAddress;
	}

	public String getSubjectLocalityDnsAddress() {
		return subjectLocalityDnsAddress;
	}

	public void setSubjectLocalityDnsAddress(String subjectLocalityDnsAddress) {
		this.subjectLocalityDnsAddress = subjectLocalityDnsAddress;
	}

	public DateTime getConditionsNotBefore() {
		return conditionsNotBefore;
	}

	public void setConditionsNotBefore(DateTime dateTime) {
		this.conditionsNotBefore = dateTime;
	}

	public DateTime getConditionsNotOnOrAfter() {
		return conditionsNotOnOrAfter;
	}

	public void setConditionsNotOnOrAfter(DateTime dateTime) {
		this.conditionsNotOnOrAfter = dateTime;
	}

	public ProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(ProviderType providerType) {
		this.providerType = providerType;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationIdType() {
		return organizationIdType;
	}

	public void setOrganizationIdType(String organizationIdType) {
		this.organizationIdType = organizationIdType;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getAuthenticationProcessId() {
		return authenticationProcessId;
	}

	public void setAuthenticationProcessId(String authenticationProcessId) {
		this.authenticationProcessId = authenticationProcessId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getLender() {
		return lender;
	}

	public void setLender(String lender) {
		this.lender = lender;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assertionId == null) ? 0 : assertionId.hashCode());
		result = prime * result + ((authenticationProcessId == null) ? 0 : authenticationProcessId.hashCode());
		result = prime * result + ((lender == null) ? 0 : lender.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
		result = prime * result + ((organizationIdType == null) ? 0 : organizationIdType.hashCode());
		result = prime * result + ((organizationType == null) ? 0 : organizationType.hashCode());
		result = prime * result + ((providerType == null) ? 0 : providerType.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((ssn == null) ? 0 : ssn.hashCode());
		result = prime * result + ((userHandle == null) ? 0 : userHandle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecurityTokenImpl other = (SecurityTokenImpl) obj;
		if (assertionId == null) {
			if (other.assertionId != null)
				return false;
		} else if (!assertionId.equals(other.assertionId))
			return false;
		if (authenticationProcessId == null) {
			if (other.authenticationProcessId != null)
				return false;
		} else if (!authenticationProcessId.equals(other.authenticationProcessId))
			return false;
		if (lender == null) {
			if (other.lender != null)
				return false;
		} else if (!lender.equals(other.lender))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		if (organizationIdType == null) {
			if (other.organizationIdType != null)
				return false;
		} else if (!organizationIdType.equals(other.organizationIdType))
			return false;
		if (organizationType == null) {
			if (other.organizationType != null)
				return false;
		} else if (!organizationType.equals(other.organizationType))
			return false;
		if (providerType != other.providerType)
			return false;
		if (role != other.role)
			return false;
		if (ssn == null) {
			if (other.ssn != null)
				return false;
		} else if (!ssn.equals(other.ssn))
			return false;
		if (userHandle == null) {
			if (other.userHandle != null)
				return false;
		} else if (!userHandle.equals(other.userHandle))
			return false;
		return true;
	}

}
