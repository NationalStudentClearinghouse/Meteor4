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
package org.meteornetwork.meteor.status.controller;

import java.util.ArrayList;
import java.util.List;

import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.util.message.Messages;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.status.controller.model.DataProviderStatus;
import org.meteornetwork.meteor.status.controller.model.StatusQueryModel;
import org.meteornetwork.meteor.status.manager.StatusProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("status")
public class StatusQueryController {

	private static final Logger LOG = LoggerFactory.getLogger(StatusQueryController.class);

	private StatusProviderManager manager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView input(ModelAndView modelView) {
		modelView.setViewName("statusQuery");

		StatusQueryModel model = new StatusQueryModel();

		try {
			List<DataProvider> dataProviders = manager.getDataProviders();
			populateModel(model, dataProviders);
		} catch (RegistryException e) {
			LOG.debug("Could not get list of data providers", e);
			model.setError(Messages.getMessage(MeteorMessage.REGISTRY_NO_CONNECTION.getPropertyRef(), (String) null));
		}

		modelView.addObject("command", model);

		return modelView;
	}

	private void populateModel(StatusQueryModel model, List<DataProvider> dataProviders) {
		model.setDataProviders(new ArrayList<DataProviderStatus>());
		for (DataProvider dataProvider : dataProviders) {
			model.getDataProviders().add(new DataProviderStatus(dataProvider));
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView submit(@ModelAttribute StatusQueryModel model, ModelAndView modelView) {
		modelView.setViewName("statusResult");

		ArrayList<DataProviderStatus> statusResult;
		try {
			statusResult = manager.getDataProviderStatuses(model.getDataProvidersToQuery());
		} catch (RegistryException e) {
			LOG.debug("Could not get list of data providers", e);
			model.setError(Messages.getMessage(MeteorMessage.REGISTRY_NO_CONNECTION.getPropertyRef(), (String) null));
			return modelView;
		}

		model.setDataProviders(statusResult);
		modelView.addObject("status", model);

		return modelView;
	}

	public StatusProviderManager getManager() {
		return manager;
	}

	@Autowired
	public void setManager(StatusProviderManager manager) {
		this.manager = manager;
	}
}
