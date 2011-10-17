package org.meteornetwork.meteor.provider.ui.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.util.message.Ssn;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.ui.MeteorSession;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public abstract class AbstractMeteorQueryController extends AbstractMeteorController {

	public static final Log LOG = LogFactory.getLog(AbstractMeteorQueryController.class);

	private AccessProviderService accessProviderService;

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleMeteorRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, SecurityToken token) throws Exception {

		ModelAndView modelView = new ModelAndView(getViewName());

		/* *********************************************
		 * Determine if meteor needs to be queried
		 */
		String ssn = getSsn(httpRequest, token);
		if (ssn != null && !Ssn.validate(ssn)) {
			// entered ssn is invalid -- redirect to query
			return new ModelAndView(new RedirectView("/meteor/query.do?invalid", true));
		}

		MeteorSession session = getSession();

		SecurityToken sessionToken = session.getToken();
		String sessionSsn = session.getSsn();

		boolean queryMeteor = false;
		if (!token.equals(sessionToken)) {
			queryMeteor = true;
			sessionToken = token;
		}

		if (session.getSsn() == null && ssn == null) {
			// if SSN is missing, redirect to query ssn page
			return new ModelAndView(new RedirectView("/meteor/query.do", true));
		} else if (ssn != null && !ssn.equals(sessionSsn)) {
			queryMeteor = true;
			sessionSsn = ssn;
		}

		/* *********************************************
		 * Query Meteor network
		 */
		if (queryMeteor) {
			if (Role.FAA.equals(token.getRole())) {
				Holder<String> responseBestSource = new Holder<String>();
				Holder<String> responseAll = new Holder<String>();
				Holder<byte[]> duplicateAwardIdsSerialized = new Holder<byte[]>();
				accessProviderService.findDataForBorrowerWithConsolidated(ssn, token.getMeteorAttributes(), responseBestSource, responseAll, duplicateAwardIdsSerialized);

				session.setResponseXml(responseBestSource.value);
				session.setResponseXmlUnfiltered(responseAll.value);
				if (duplicateAwardIdsSerialized.value != null) {
					session.setDuplicateAwardIds((HashMap<Integer, ArrayList<Integer>>) SerializationUtils.deserialize(duplicateAwardIdsSerialized.value));
				}
			} else {
				session.setResponseXml(accessProviderService.findDataForBorrower(ssn, token.getMeteorAttributes()));
			}
		}

		session.setToken(sessionToken);
		session.setSsn(sessionSsn);

		/* *********************************************
		 * Render page
		 */
		addMeteorDataToModelView(modelView, session.getResponseXml());

		// Set global parameters
		modelView.addObject("ssn", session.getSsn());
		modelView.addObject("role", token.getRole() == null ? null : token.getRole().getName());
		modelView.addObject("docroot", httpRequest.getContextPath());

		handleMeteorRequest(modelView, httpRequest, httpResponse);

		return modelView;
	}

	protected void addMeteorDataToModelView(ModelAndView modelView, String xmlData) {
		modelView.addObject("meteorData", createStreamSource(xmlData));
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
}
