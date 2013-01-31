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
