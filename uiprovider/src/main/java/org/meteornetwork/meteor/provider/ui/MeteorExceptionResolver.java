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
