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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Holder;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.util.message.Ssn;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.ui.MeteorSession;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public abstract class AbstractMeteorQueryController extends AbstractMeteorController {

	public static final Logger LOG = LoggerFactory.getLogger(AbstractMeteorQueryController.class);

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleMeteorRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, SecurityToken token) throws Exception {
		LOG.debug("Determining if query needs to be made to meteor network...");

		ModelAndView modelView = new ModelAndView(getViewName());

		/* *********************************************
		 * Determine if meteor needs to be queried
		 */
		LOG.debug("Validating SSN");
		String ssn = getSsn(httpRequest, token);
		if (ssn != null && !Ssn.validate(ssn)) {
			// entered ssn is invalid -- redirect to query
			LOG.debug("SSN " + ssn + " is invalid");
			return new ModelAndView(new RedirectView("/meteor/query.do?invalid", true));
		}

		MeteorSession session = getSession();

		SecurityToken sessionToken = session.getToken();

		LOG.debug("Checking authentication token - if new, requery meteor");
		boolean queryMeteor = false;
		if (!token.equals(sessionToken)) {
			LOG.debug("Authentication token is new - requerying meteor");
			session.clear();
			queryMeteor = true;
			sessionToken = token;
		}

		String sessionSsn = session.getSsn();

		if (session.getSsn() == null && ssn == null) {
			// if SSN is missing, redirect to query ssn page
			LOG.debug("SSN is missing, redirect to query page");
			return new ModelAndView(new RedirectView("/meteor/query.do", true));
		} else if (ssn != null && !ssn.equals(sessionSsn)) {
			queryMeteor = true;
			sessionSsn = ssn;
		}

		session.setToken(sessionToken);
		session.setSsn(sessionSsn);

		LOG.debug("Query SSN: " + sessionSsn);

		// if APCSR or LENDER, check inquiry role
		if ((Role.APCSR.equals(sessionToken.getRole()) || Role.LENDER.equals(sessionToken.getRole())) && httpRequest.getParameter("inquiryRole") != null) {
			session.setInquiryRole(httpRequest.getParameter("inquiryRole"));
		}

		/* *********************************************
		 * Query Meteor network
		 */
		if (queryMeteor || httpRequest.getParameter("splash") != null) {
			LOG.debug("Querying meteor network");

			/*
			 * redirect to loading screen first.
			 */
			if (httpRequest.getParameter("splash") == null && "Yes".equals(getUiProviderProperties().getProperty("uiprovider.splashenabled"))) {
				LOG.debug("Displaying splash screen");
				ModelAndView queryStatusModelView = new ModelAndView("queryStatus");
				queryStatusModelView.addObject("docroot", httpRequest.getContextPath());
				queryStatusModelView.addObject("redirect-url", httpRequest.getRequestURL().toString());
				queryStatusModelView.addObject("sourceXml", createStreamSource("<?xml version=\"1.0\" encoding=\"utf-8\"?><node></node>"));
				return queryStatusModelView;
			}

			JaxWsProxyFactoryBean accessClientProxy = getAccessClientProxy();
			AccessProviderService accessProviderService = (AccessProviderService) accessClientProxy.create();

			if (Role.FAA.equals(sessionToken.getRole()) || Role.APCSR.equals(sessionToken.getRole()) || Role.LENDER.equals(sessionToken.getRole())) {
				LOG.debug("User role is " + sessionToken.getRole().getName() + ", querying meteor network for duplicate data as well as best source");

				Holder<String> responseBestSource = new Holder<String>();
				Holder<String> responseAll = new Holder<String>();
				Holder<byte[]> duplicateAwardIdsSerialized = new Holder<byte[]>();

				accessProviderService.findDataForBorrowerWithConsolidated(sessionSsn, sessionToken.getMeteorAttributes(), responseBestSource, responseAll, duplicateAwardIdsSerialized);

				session.setResponseXml(responseBestSource.value);
				session.setResponseXmlUnfiltered(responseAll.value);
				if (duplicateAwardIdsSerialized.value != null) {
					session.setDuplicateAwardIds((TreeMap<Integer, ArrayList<Integer>>) SerializationUtils.deserialize(duplicateAwardIdsSerialized.value));
				}
			} else {
				LOG.debug("Querying meteor network for best source data");
				session.setResponseXml(accessProviderService.findDataForBorrower(sessionSsn, sessionToken.getMeteorAttributes()));
			}
		}

		/* *********************************************
		 * Render page
		 */
		addMeteorDataToModelView(modelView, session.getResponseXml());

		// Set global parameters
		modelView.addObject("ssn", sessionSsn);
		modelView.addObject("role", sessionToken.getRole() == null ? "" : sessionToken.getRole().getName());
		modelView.addObject("docroot", httpRequest.getContextPath());
		if (Role.APCSR.equals(sessionToken.getRole()) || Role.LENDER.equals(sessionToken.getRole())) {
			modelView.addObject("inquiryRole", session.getInquiryRole() == null ? "" : session.getInquiryRole());
		}

		handleMeteorRequest(modelView, httpRequest, httpResponse);

		return modelView;
	}

	protected void addMeteorDataToModelView(ModelAndView modelView, String xmlData) {
		modelView.addObject("meteorData", createStreamSource(xmlData));
	}

	private StreamSource createStreamSource(String xml) {
		if (xml == null) {
			LOG.debug("XML from access provider is null - displaying default xml");
			return new StreamSource(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"utf-8\"?><node></node>".getBytes()));
		}
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

	public JaxWsProxyFactoryBean getAccessClientProxy() {
		// overridden by spring method-injection
		return null;
	}
}
