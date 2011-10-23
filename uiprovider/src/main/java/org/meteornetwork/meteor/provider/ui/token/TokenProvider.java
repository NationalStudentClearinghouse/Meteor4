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
	 *             thrown when security token cant be returned for some reason.
	 *             The cause code parameter in the constructor of
	 *             SecurityTokenException affects the message returned to the
	 *             user by the UI provider. For example, if the cause code is
	 *             ACCESS_DENIED (default), the user is presented with the
	 *             Access Denied page. If the cause code is SESSION_EXPIRED, on
	 *             the other hand, the user is presented with the more innocent
	 *             "Please login again" page. Consult the SecurityTokenException
	 *             javadoc for further details.
	 */
	SecurityToken getSecurityToken(HttpServletRequest request) throws SecurityTokenException;
}
