package org.meteornetwork.meteor.provider.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * Adds APSUniqueAwardID to rendering parameters
 * 
 * @author jlazos
 * 
 */
public class AwardDetailController extends AbstractMeteorController {

	@Override
	protected void handleMeteorRequest(ModelAndView modelAndView, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		modelAndView.addObject("apsuniqueawardid", getAPSUniqueAwardId(httpRequest));
	}

	protected String getAPSUniqueAwardId(HttpServletRequest httpRequest) {
		return httpRequest.getParameter("apsUniqAwardId");
	}
}
