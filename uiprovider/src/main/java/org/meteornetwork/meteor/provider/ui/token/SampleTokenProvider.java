package org.meteornetwork.meteor.provider.ui.token;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.helpers.IOUtils;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException.CauseCode;

/**
 * Works with the Meteor example login provider
 * 
 * @author jlazos
 * 
 */
public class SampleTokenProvider implements TokenProvider {

	private static final Log LOG = LogFactory.getLog(SampleTokenProvider.class);

	@Override
	public SecurityToken getSecurityToken(HttpServletRequest request) throws SecurityTokenException {
		String tokenId = request.getParameter("tokenId");
		String tokenString = null;

		if (tokenId == null || "".equals(tokenId)) {
			tokenString = (String) request.getSession().getAttribute("SampleSecurityToken");
			if (tokenString == null || "".equals(tokenString)) {
				throw new SecurityTokenException(CauseCode.SESSION_EXPIRED);
			}
		} else {
			try {
				tokenString = getTokenFromURL(request.getParameter("url"), tokenId);
				request.getSession().setAttribute("SampleSecurityToken", tokenString);
			} catch (IOException e) {
				LOG.debug("Could not get security token from sample login provider", e);
				throw new SecurityTokenException();
			}
		}

		SecurityToken token;
		try {
			token = SecurityTokenImpl.fromXML(tokenString);
		} catch (Exception e) {
			throw new SecurityTokenException(e);
		}

		if (!token.validateConditions()) {
			LOG.debug("Token expired");
			throw new SecurityTokenException();
		}
		return token;
	}

	private String getTokenFromURL(String urlStr, String tokenId) throws IOException {
		String charset = "UTF-8";

		String query = String.format("artifactId=%s", URLEncoder.encode(tokenId, "UTF-8"));
		URLConnection connection = new URL(urlStr + "?" + query).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);

		return IOUtils.readStringFromStream(connection.getInputStream());
	}
}
