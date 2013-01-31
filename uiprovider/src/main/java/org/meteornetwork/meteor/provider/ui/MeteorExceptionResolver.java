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
package org.meteornetwork.meteor.provider.ui;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class MeteorExceptionResolver extends SimpleMappingExceptionResolver {

	private static final Logger LOG = LoggerFactory.getLogger(MeteorExceptionResolver.class);
	
	private Properties uiproviderProperties;

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		LOG.debug("UI provider threw the following exception", ex);
		
		ModelAndView modelView = super.doResolveException(request, response, handler, ex);

		modelView.addObject("docroot", request.getContextPath());
		modelView.addObject("contact-url", uiproviderProperties.getProperty("uiprovider.errorcontacturl"));
		modelView.addObject("sourceXml", new StreamSource(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"utf-8\"?><none></none>".getBytes())));

		return modelView;
	}

	public Properties getUiProviderProperties() {
		return uiproviderProperties;
	}

	public void setUiProviderProperties(Properties uiproviderProperties) {
		this.uiproviderProperties = uiproviderProperties;
	}
}
