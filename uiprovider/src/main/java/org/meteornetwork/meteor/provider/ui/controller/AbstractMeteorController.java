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
package org.meteornetwork.meteor.provider.ui.controller;

import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.abstraction.token.TokenProvider;
import org.meteornetwork.meteor.provider.ui.exception.MeteorAccessException;
import org.meteornetwork.meteor.provider.ui.exception.MeteorSessionExpiredException;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException.CauseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public abstract class AbstractMeteorController extends ParameterizableViewController {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractMeteorController.class);

	private Properties uiProviderProperties;

	private TokenProvider tokenProvider;
	private List<Role> allowedRoles;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		/* *********************************************
		 * validate authentication
		 */
		LOG.debug("Authenticating user");
		SecurityToken token;
		try {
			token = tokenProvider.getSecurityToken(httpRequest);
		} catch (SecurityTokenException e) {
			LOG.debug("Could not get authenticated security token", e);
			if (CauseCode.SESSION_EXPIRED.equals(e.getCauseCode())) {
				throw new MeteorSessionExpiredException();
			}
			throw new MeteorAccessException();
		}

		if (!roleIsAllowed(token.getRole())) {
			throw new MeteorAccessException();
		}

		LOG.debug("User is authenticated with role " + token.getRole().getName() + ", handling request");
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

	@Resource(name="uiProviderProperties")
	public void setUiProviderProperties(Properties uiProviderProperties) {
		this.uiProviderProperties = uiProviderProperties;
	}
}
