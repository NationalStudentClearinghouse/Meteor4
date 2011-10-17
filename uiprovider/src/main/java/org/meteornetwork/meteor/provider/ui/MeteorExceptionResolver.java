package org.meteornetwork.meteor.provider.ui;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class MeteorExceptionResolver extends SimpleMappingExceptionResolver {

	private Properties uiproviderProperties;

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
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
