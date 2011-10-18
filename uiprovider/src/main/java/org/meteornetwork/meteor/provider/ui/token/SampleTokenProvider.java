package org.meteornetwork.meteor.provider.ui.token;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.helpers.IOUtils;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

/**
 * Works with the Meteor example login provider
 * 
 * @author jlazos
 * 
 */
public class SampleTokenProvider implements TokenProvider {

	@Override
	public SecurityToken getSecurityToken(HttpServletRequest request) throws SecurityTokenException {
		String tokenString = request.getParameter("SecurityToken");
		if (tokenString == null) {
			tokenString = (String) request.getSession().getAttribute("SampleSecurityToken");
		} else {
			request.getSession().setAttribute("SampleSecurityToken", tokenString);
		}

		// TODO convert sample login to use SAML token profile. Do not pass
		// complete saml assertion on request

		SecurityToken token;
		try {
			token = SecurityTokenImpl.fromXML(new String(Base64Utility.decode(tokenString), IOUtils.UTF8_CHARSET));
		} catch (Exception e) {
			throw new SecurityTokenException(e);
		}

		return token;
	}

}
