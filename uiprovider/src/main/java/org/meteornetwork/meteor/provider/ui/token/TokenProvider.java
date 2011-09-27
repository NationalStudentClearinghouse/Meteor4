package org.meteornetwork.meteor.provider.ui.token;

import javax.servlet.http.HttpServletRequest;

import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

/**
 * Provides a Meteor Security Token for the authenticated user. Implementations
 * create security tokens based on whatever login system is employed (Sample
 * logon, Shibboleth, OpenAM etc)
 * 
 * @author jlazos
 * 
 */
public interface TokenProvider {

	/**
	 * Produces a Meteor Security Token for the authenticated user.
	 * 
	 * @return the token representing the authenticated user
	 * @throws SecurityTokenException
	 */
	SecurityToken getSecurityToken(HttpServletRequest request) throws SecurityTokenException;
}
