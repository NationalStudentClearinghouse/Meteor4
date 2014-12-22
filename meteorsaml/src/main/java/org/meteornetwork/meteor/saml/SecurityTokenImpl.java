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
package org.meteornetwork.meteor.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
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
import org.apache.xpath.XPathAPI;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.meteornetwork.meteor.saml.util.DOMUtils;
import org.opensaml.saml2.core.Assertion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a SAML 2.0 Assertion with Meteor attributes
 * 
 * @author jlazos
 * 
 */
public class SecurityTokenImpl implements SecurityToken, Serializable {

	private static final long serialVersionUID = 1L;

	static {
		OpenSAMLUtil.initSamlEngine();
	}

	private String assertionId;
	private String issuer;
	private String subjectName;
	private String subjectLocalityIpAddress;
	private String subjectLocalityDnsAddress;

	private static final Integer VALIDITY_PERIOD_DEFAULT = 3600; // seconds
	private DateTime conditionsNotBefore;
	private DateTime conditionsNotOnOrAfter;

	private TokenAttributes meteorAttributes = new TokenAttributes();

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
	 * Creates a SecurityTokenImpl given valid SAML 2.0 assertion xml.
	 * 
	 * @param tokenElem
	 *            valid SAML 2.0 assertion with meteor attributes. Parent
	 *            document must not be namespace aware.
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
			Node node = XPathAPI.selectSingleNode(tokenElem, "@ID");
			if (node != null) {
				token.setAssertionId(node.getFirstChild().getNodeValue());
			}

			node = XPathAPI.selectSingleNode(tokenElem, "Issuer");
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
			if (assertionId != null) {
				assertion.setID(assertionId);
			}

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

		DateTime curTime = currentDateTime == null ? getCurrentDateTime() : currentDateTime;
		DateTime notBefore = conditionsNotBefore == null ? curTime : conditionsNotBefore;
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

		if (meteorAttributes.getProviderType() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("ProviderType", meteorAttributes.getProviderType().getType()));
		}

		if (meteorAttributes.getOrganizationId() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationID", meteorAttributes.getOrganizationId()));
		}

		if (meteorAttributes.getOrganizationIdType() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationIDType", meteorAttributes.getOrganizationIdType()));
		}

		if (meteorAttributes.getOrganizationType() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationType", meteorAttributes.getOrganizationType()));
		}

		if (meteorAttributes.getSsn() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("SSN", meteorAttributes.getSsn()));
		}

		if (meteorAttributes.getLender() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("LENDER", meteorAttributes.getLender()));
		}

		attributeStatement.getSamlAttributes().add(createAttributeBean("AuthenticationProcessID", meteorAttributes.getAuthenticationProcessId()));
		attributeStatement.getSamlAttributes().add(createAttributeBean("Level", Integer.toString(meteorAttributes.getLevel())));
		attributeStatement.getSamlAttributes().add(createAttributeBean("UserHandle", meteorAttributes.getUserHandle()));
		attributeStatement.getSamlAttributes().add(createAttributeBean("Role", meteorAttributes.getRole().getName()));

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

	@Override
	public boolean validateConditions() {
		if (conditionsNotBefore == null) {
			return false;
		}

		if (conditionsNotOnOrAfter == null) {
			return true;
		}

		return (conditionsNotBefore.isEqualNow() || conditionsNotBefore.isBeforeNow()) && conditionsNotOnOrAfter.isAfterNow();
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
		return meteorAttributes.getProviderType();
	}

	public void setProviderType(ProviderType providerType) {
		meteorAttributes.setProviderType(providerType);
	}

	public String getOrganizationId() {
		return meteorAttributes.getOrganizationId();
	}

	public void setOrganizationId(String organizationId) {
		meteorAttributes.setOrganizationId(organizationId);
	}

	public String getOrganizationIdType() {
		return meteorAttributes.getOrganizationIdType();
	}

	public void setOrganizationIdType(String organizationIdType) {
		meteorAttributes.setOrganizationIdType(organizationIdType);
	}

	public String getOrganizationType() {
		return meteorAttributes.getOrganizationType();
	}

	public void setOrganizationType(String organizationType) {
		meteorAttributes.setOrganizationType(organizationType);
	}

	public String getAuthenticationProcessId() {
		return meteorAttributes.getAuthenticationProcessId();
	}

	public void setAuthenticationProcessId(String authenticationProcessId) {
		meteorAttributes.setAuthenticationProcessId(authenticationProcessId);
	}

	public Integer getLevel() {
		return meteorAttributes.getLevel();
	}

	public void setLevel(Integer level) {
		meteorAttributes.setLevel(level);
	}

	public String getUserHandle() {
		return meteorAttributes.getUserHandle();
	}

	public void setUserHandle(String userHandle) {
		meteorAttributes.setUserHandle(userHandle);
	}

	public Role getRole() {
		return meteorAttributes.getRole();
	}

	public void setRole(Role role) {
		meteorAttributes.setRole(role);
	}

	public String getSsn() {
		return meteorAttributes.getSsn();
	}

	public void setSsn(String ssn) {
		meteorAttributes.setSsn(ssn);
	}

	public String getLender() {
		return meteorAttributes.getLender();
	}

	public void setLender(String lender) {
		meteorAttributes.setLender(lender);
	}

	@Override
	public TokenAttributes getMeteorAttributes() {
		return meteorAttributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assertionId == null) ? 0 : assertionId.hashCode());
		result = prime * result + ((meteorAttributes == null) ? 0 : meteorAttributes.hashCode());
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
		if (meteorAttributes == null) {
			if (other.meteorAttributes != null)
				return false;
		} else if (!meteorAttributes.equals(other.meteorAttributes))
			return false;
		return true;
	}

	@Override
	public void setMeteorAttributes(TokenAttributes attributes) {
		this.meteorAttributes = attributes;
	}

}
