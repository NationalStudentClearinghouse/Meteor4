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
package org.meteornetwork.meteor.provider.ui.token;

import javax.servlet.http.HttpServletRequest;

import org.meteornetwork.meteor.common.abstraction.token.TokenProvider;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

public class ShibbolethTokenProvider implements TokenProvider {

	@Override
	public SecurityToken getSecurityToken(HttpServletRequest request) throws SecurityTokenException {

		SecurityToken token = new SecurityTokenImpl();
		token.setAssertionId(getAttribute(request, "Shib-Session-ID"));
		token.setIssuer(getAttribute(request, "Shib-Identity-Provider"));

		token.setUserHandle(getRequiredAttribute(request, "UserHandle"));
		token.setOrganizationId(getAttribute(request, "OrganizationId"));
		token.setOrganizationIdType(getAttribute(request, "OrganizationIdType"));
		token.setOrganizationType(getAttribute(request, "OrganizationType"));
		token.setAuthenticationProcessId(getRequiredAttribute(request, "AuthenticationProcessId"));
		token.setLevel(Integer.valueOf(getRequiredAttribute(request, "Level")));
		token.setRole(Role.valueOfName(getRequiredAttribute(request, "Role")));
		if (token.getRole() == null) {
			throw new SecurityTokenException("Could not create SecurityToken from Shibboleth attributes. Role " + getRequiredAttribute(request, "Role") + " is not a valid Meteor role.");
		}
		token.setSsn(getAttribute(request, "SSN"));
		token.setLender(getAttribute(request, "Lender"));

		return token;
	}

	private String getAttribute(HttpServletRequest request, String attributeName) {
		return (String) request.getAttribute(attributeName);
	}

	private String getRequiredAttribute(HttpServletRequest request, String attributeName) throws SecurityTokenException {
		String attrValue = getAttribute(request, attributeName);
		if (attrValue == null) {
			throw new SecurityTokenException("Could not create SecurityToken from Shibboleth attributes. " + attributeName + " is required.");
		}
		return attrValue;
	}

}
