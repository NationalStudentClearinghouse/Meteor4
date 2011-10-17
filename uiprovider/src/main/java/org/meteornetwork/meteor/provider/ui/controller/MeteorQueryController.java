package org.meteornetwork.meteor.provider.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public class MeteorQueryController extends AbstractMeteorQueryController {

	@Override
	protected void handleMeteorRequest(ModelAndView modelAndView, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		// do nothing. AbstractMeteorController's request handling is
		// sufficient.
	}

}
