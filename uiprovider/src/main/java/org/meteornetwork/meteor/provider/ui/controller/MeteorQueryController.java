package org.meteornetwork.meteor.provider.ui.controller;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class MeteorQueryController extends ParameterizableViewController {

	private AccessProviderService accessProviderService;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		ModelAndView modelView = new ModelAndView(getViewName());

		// TODO cache response on session, use SSN from request
		String responseXml = accessProviderService.findDataForBorrower("110101010");
		modelView.addObject("meteorData", createStreamSource(responseXml));

		return modelView;
	}

	private StreamSource createStreamSource(String xml) {
		return new StreamSource(new ByteArrayInputStream(xml.getBytes()));
	}

	public AccessProviderService getAccessProviderService() {
		return accessProviderService;
	}

	@Autowired
	public void setAccessProviderService(AccessProviderService accessProviderService) {
		this.accessProviderService = accessProviderService;
	}

}
