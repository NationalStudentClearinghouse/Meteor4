package org.meteornetwork.meteor.provider.ui.controller;

import java.io.ByteArrayInputStream;

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

public class MeteorQueryController extends ParameterizableViewController {

	public static final Log LOG = LogFactory.getLog(MeteorQueryController.class);

	private AccessProviderService accessProviderService;
	private TokenProvider tokenProvider;

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
		
		if (queryMeteor) {
			// TODO pass token attributes to access provider
			session.setResponseXml(accessProviderService.findDataForBorrower(ssn));
		}
		
		modelView.addObject("meteorData", createStreamSource(session.getResponseXml()));

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

}
