package org.meteornetwork.meteor.provider.ui.controller;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.meteornetwork.meteor.saml.SecurityToken;
import org.springframework.web.servlet.ModelAndView;

public class QuerySSNController extends AbstractMeteorController {

	@Override
	protected ModelAndView handleMeteorRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, SecurityToken token) throws Exception {
		ModelAndView modelView = new ModelAndView(getViewName());

		// Set global parameters
		modelView.addObject("ssn", "---");
		modelView.addObject("role", token.getRole() == null ? null : token.getRole().getName());
		modelView.addObject("docroot", httpRequest.getContextPath());

		modelView.addObject("sourceXml", new StreamSource(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"utf-8\"?><PESCXML:MeteorRsMsg xmlns:PESCXML=\"http://schemas.pescxml.org\"></PESCXML:MeteorRsMsg>".getBytes())));

		if (httpRequest.getParameter("invalid") != null) {
			modelView.addObject("header-message", "Invalid SSN. Please try another.");
		}

		return modelView;
	}
}
