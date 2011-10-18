package org.meteornetwork.meteor.provider.ui.controller;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.provider.ui.exception.MeteorAccessException;
import org.meteornetwork.meteor.provider.ui.token.TokenProvider;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public abstract class AbstractMeteorController extends ParameterizableViewController {

	private static final Log LOG = LogFactory.getLog(AbstractMeteorController.class);

	private Properties uiProviderProperties;
	
	private TokenProvider tokenProvider;
	private List<Role> allowedRoles;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		/* *********************************************
		 * validate authentication
		 */
		SecurityToken token;
		try {
			token = tokenProvider.getSecurityToken(httpRequest);
		} catch (SecurityTokenException e) {
			LOG.error("Could not get authenticated security token", e);
			throw new MeteorAccessException();
		}

		if (!roleIsAllowed(token.getRole())) {
			throw new MeteorAccessException();
		}
		
		return handleMeteorRequest(httpRequest, httpResponse, token);
	}

	/**
	 * Validates user with the specified role is allowed to access the page. If
	 * no valid roles are specified on the controller bean, all roles are
	 * considered valid
	 * 
	 * @param role
	 *            the role to test
	 * @return true if role specified is allowed to access the page
	 */
	protected boolean roleIsAllowed(Role role) {
		if (allowedRoles == null) {
			return true;
		}
		for (Role allowedRole : allowedRoles) {
			if (allowedRole.equals(role)) {
				return true;
			}
		}

		return false;
	}

	protected abstract ModelAndView handleMeteorRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, SecurityToken token) throws Exception;

	public TokenProvider getTokenProvider() {
		return tokenProvider;
	}

	@Autowired
	public void setTokenProvider(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	public List<Role> getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(List<Role> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	public Properties getUiProviderProperties() {
		return uiProviderProperties;
	}

	@Autowired
	@Qualifier("UiProviderProperties")
	public void setUiProviderProperties(Properties uiProviderProperties) {
		this.uiProviderProperties = uiProviderProperties;
	}
}
