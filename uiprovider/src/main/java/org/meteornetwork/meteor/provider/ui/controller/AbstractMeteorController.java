package org.meteornetwork.meteor.provider.ui.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.ui.MeteorSession;
import org.meteornetwork.meteor.provider.ui.token.TokenProvider;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public abstract class AbstractMeteorController extends ParameterizableViewController {

	public static final Log LOG = LogFactory.getLog(AbstractMeteorController.class);

	private AccessProviderService accessProviderService;
	private TokenProvider tokenProvider;
	private List<Role> validRoles;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		ModelAndView modelView = new ModelAndView(getViewName());

		// TODO create spring mvc handler interceptor with this SecurityToken
		// logic, redirect to access denied
		SecurityToken token;
		try {
			token = tokenProvider.getSecurityToken(httpRequest);
		} catch (SecurityTokenException e) {
			LOG.error("Could not get authenticated security token", e);
			return null;
		}

		String ssn = getSsn(httpRequest, token);

		MeteorSession session = getSession();

		boolean queryMeteor = false;
		if (!token.equals(session.getToken())) {
			queryMeteor = true;
			session.setToken(token);
		}

		if (session.getSsn() == null && ssn == null) {
			// TODO if SSN is missing, redirect to SSN query page
			return null;
		} else if (ssn != null && !ssn.equals(session.getSsn())) {
			queryMeteor = true;
			session.setSsn(ssn);
		}

		// TODO redirect to access denied if role is not valid for page
		if (!roleIsAllowed(token.getRole())) {
			return null;
		}
		
		if (queryMeteor) {
			session.setResponseXml(accessProviderService.findDataForBorrower(ssn, token.getMeteorAttributes()));
		}

		modelView.addObject("meteorData", createStreamSource(session.getResponseXml()));

		// Set global parameters
		modelView.addObject("ssn", session.getSsn());
		modelView.addObject("role", token.getRole() == null ? null : token.getRole().getName());
		modelView.addObject("docroot", httpRequest.getContextPath());

		handleMeteorRequest(modelView, httpRequest, httpResponse);

		return modelView;
	}

	private StreamSource createStreamSource(String xml) {
		return new StreamSource(new ByteArrayInputStream(xml.getBytes()));
	}

	private String getSsn(HttpServletRequest request, SecurityToken token) {
		if (Role.BORROWER.equals(token.getRole())) {
			return token.getSsn();
		}

		if (Role.FAA.equals(token.getRole()) || Role.APCSR.equals(token.getRole()) || Role.LENDER.equals(token.getRole())) {
			return request.getParameter("ssn");
		}

		return null;
	}

	/**
	 * Handles any logic for specific pages
	 * 
	 * @param modelAndView
	 * @param httpRequest
	 * @param httpResponse
	 * @throws Exception
	 */
	protected abstract void handleMeteorRequest(ModelAndView modelAndView, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception;

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
		if (validRoles == null) {
			return true;
		}
		for (Role validRole : validRoles) {
			if (validRole.equals(role)) {
				return true;
			}
		}

		return false;
	}

	public MeteorSession getSession() {
		// overridden by spring method-injection
		return null;
	}

	public AccessProviderService getAccessProviderService() {
		return accessProviderService;
	}

	@Autowired
	public void setAccessProviderService(AccessProviderService accessProviderService) {
		this.accessProviderService = accessProviderService;
	}

	public TokenProvider getTokenProvider() {
		return tokenProvider;
	}

	@Autowired
	public void setTokenProvider(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	public List<Role> getValidRoles() {
		return validRoles;
	}

	public void setValidRoles(List<Role> validRoles) {
		this.validRoles = validRoles;
	}

}
