package org.meteornetwork.meteor.saml;

import org.joda.time.DateTime;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.w3c.dom.Document;

/**
 * Creates or parses a SAML 2.09 assertion. The getter/setter methods of this
 * class are used to access parts of the assertion.
 * 
 * When using an implementation of this interface to create a SAML 2.0
 * assertion, the programmer only needs to consider setting the following
 * properties:
 * 
 * 
 * 
 * @author jlazos
 * 
 */
public interface SecurityToken {

	/**
	 * Creates a new SAML 2.0 assertion using the parameters of this security
	 * token (except the assertion ID, which is generated anew)
	 * 
	 * @return DOM representation of the SAML 2.0 assertion
	 * @throws SecurityTokenException
	 *             wraps any exceptions that occur while processing
	 */
	Document toXML() throws SecurityTokenException;

	/**
	 * Creates a new SAML 2.0 assertion using the parameters of this security
	 * token (except the assertion ID, which is generated anew)
	 * 
	 * @return XML string representation of the SAML 2.0 assertion
	 * @throws SecurityTokenException
	 *             wraps any exceptions that occur while processing
	 */
	String toXMLString() throws SecurityTokenException;

	String getAssertionId();

	/**
	 * The ID attribute of the assertion element. This property is overridden
	 * with a generated value on a call to toXML() or toXMLString().
	 * 
	 * @param id
	 */
	void setAssertionId(String id);

	String getIssuer();

	/**
	 * Optional. The issuer of this assertion.
	 * 
	 * @param issuer
	 */
	void setIssuer(String issuer);

	String getSubjectName();

	/**
	 * The Subject element of the assertion.
	 * 
	 * @param subjectName
	 */
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

	String getOrganizationId();

	/**
	 * For FAA role only. The ID of the organization the FAA belongs to.
	 * 
	 * @param organizationId
	 */
	void setOrganizationId(String organizationId);

	String getOrganizationIdType();

	/**
	 * For FAA role only. The type of organization ID (e.g. OPEID)
	 * 
	 * @param organizationIdType
	 */
	void setOrganizationIdType(String organizationIdType);

	String getOrganizationType();

	/**
	 * For FAA role only. They type of organization (e.g. SCHOOL)
	 * 
	 * @param organizationType
	 */
	void setOrganizationType(String organizationType);

	String getAuthenticationProcessId();

	/**
	 * Required. The identifier of the process used to authenticate the user.
	 * 
	 * @param authenticationProcessId
	 */
	void setAuthenticationProcessId(String authenticationProcessId);

	Integer getLevel();

	/**
	 * Required. The authentication level of the user authentication process
	 * 
	 * @param level
	 */
	void setLevel(Integer level);

	/**
	 * Required.
	 * 
	 * @return
	 */
	String getUserHandle();

	void setUserHandle(String userHandle);

	Role getRole();

	/**
	 * Required. User role
	 * 
	 * @param role
	 */
	void setRole(Role role);

	String getSsn();

	/**
	 * Optional. Sets the SSN the user is authorized to view. Only applicable
	 * for the BORROWER role.
	 */
	void setSsn(String ssn);

	String getLender();

	/**
	 * Optional. Sets the lender OPEID. Only applicable for the LENDER role.
	 */
	void setLender(String lender);

}
