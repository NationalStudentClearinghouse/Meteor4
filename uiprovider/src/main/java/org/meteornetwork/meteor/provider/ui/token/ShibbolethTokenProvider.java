package org.meteornetwork.meteor.provider.ui.token;

import javax.servlet.http.HttpServletRequest;

import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

public class ShibbolethTokenProvider implements TokenProvider {

	private static final String PREFIX = "AJP_";

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
		// TODO: get value from environment attribute
		return request.getHeader(PREFIX.concat(attributeName));
	}

	private String getRequiredAttribute(HttpServletRequest request, String attributeName) throws SecurityTokenException {
		String attrValue = getAttribute(request, attributeName);
		if (attrValue == null) {
			throw new SecurityTokenException("Could not create SecurityToken from Shibboleth attributes. " + PREFIX.concat(attributeName) + " is required.");
		}
		return attrValue;
	}

}
