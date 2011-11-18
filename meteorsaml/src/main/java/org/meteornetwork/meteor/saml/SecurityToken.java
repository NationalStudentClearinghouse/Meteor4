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

import org.joda.time.DateTime;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.w3c.dom.Document;

/**
 * Creates or parses a SAML 2.0 assertion. The getter/setter methods of this
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

	/**
	 * Validates assertion has not yet expired
	 * 
	 * @return true if assertion has not expired, false otherwise
	 */
	boolean validateConditions();

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
	 * value set using setConditionsNotBefore() plus 1 hour.
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

	/**
	 * Get all meteor-specific attributes in one bean
	 * 
	 * @return
	 */
	TokenAttributes getMeteorAttributes();

	/**
	 * Set all meteor-specific attributes with one bean
	 * 
	 * @param attributes
	 */
	void setMeteorAttributes(TokenAttributes attributes);
}
