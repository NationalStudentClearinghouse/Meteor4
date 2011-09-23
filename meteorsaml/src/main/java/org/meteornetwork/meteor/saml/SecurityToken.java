package org.meteornetwork.meteor.saml;

import org.joda.time.DateTime;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.w3c.dom.Document;

public interface SecurityToken {

	Document toXML() throws SecurityTokenException;

	String toXMLString() throws SecurityTokenException;

	// TODO create javadoc to document these properties

	String getIssuer();

	/**
	 * Optional. The unique identifier of the issuer of this assertion.
	 * 
	 * @param issuer
	 */
	void setIssuer(String issuer);

	String getSubjectName();

	void setSubjectName(String subjectName);

	String getSubjectLocalityIpAddress();

	/**
	 * Optional. Default value is the result of call to
	 * InetAddress.getLocalHost().getHostAddress(), or null if
	 * UnknownHostException is thrown.
	 * 
	 * @param subjectLocalityIpAddress
	 */
	void setSubjectLocalityIpAddress(String subjectLocalityIpAddress);

	String getSubjectLocalityDnsAddress();

	/**
	 * Optional. Default value is the result of call to
	 * InetAddress.getLocalHost().getHostName(), or null if UnknownHostException
	 * is thrown.
	 * 
	 * @param subjectLocalityDnsAddress
	 */
	void setSubjectLocalityDnsAddress(String subjectLocalityDnsAddress);

	DateTime getConditionsNotBefore();

	/**
	 * Optional. Earliest time the assertion is valid. Defaults to current time
	 * when toXML() or toXMLString() is called
	 * 
	 * @param dateTime
	 */
	void setConditionsNotBefore(DateTime dateTime);

	DateTime getConditionsNotOnOrAfter();

	/**
	 * Optional. The time when the assertion is no longer valid. Defaults to the
	 * value set using setConditionsNotBefore() plus 4 hours.
	 * 
	 * @param dateTime
	 */
	void setConditionsNotOnOrAfter(DateTime dateTime);

	ProviderType getProviderType();

	void setProviderType(ProviderType providerType);

	String getOrganizationId();

	void setOrganizationId(String organizationId);

	String getOrganizationIdType();

	void setOrganizationIdType(String organizationIdType);

	String getOrganizationType();

	void setOrganizationType(String organizationType);

	String getAuthenticationProcessId();

	void setAuthenticationProcessId(String authenticationProcessId);

	Integer getLevel();

	void setLevel(Integer level);

	String getUserHandle();

	void setUserHandle(String userHandle);

	Role getRole();

	void setRole(Role role);

}
