package org.meteornetwork.meteor.provider.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;
import org.springframework.web.servlet.ModelAndView;

public class MyDataDownloadController extends AbstractMeteorQueryController {

	@Override
	protected void handleMeteorRequest(ModelAndView modelAndView, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		modelAndView.addObject("meteorDataDownload", IOUtils.readStringFromStream(((StreamSource)modelAndView.getModel().get("meteorData")).getInputStream()));
	}

}
