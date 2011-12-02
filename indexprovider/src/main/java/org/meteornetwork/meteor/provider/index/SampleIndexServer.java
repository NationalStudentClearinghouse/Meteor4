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
package org.meteornetwork.meteor.provider.index;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.abstraction.index.IndexServerAbstraction;
import org.meteornetwork.meteor.common.abstraction.index.MeteorContext;
import org.meteornetwork.meteor.common.abstraction.index.MeteorIndexResponseWrapper;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Retrieves all data providers available in the registry
 * 
 * @author jlazos
 * 
 */
public class SampleIndexServer implements IndexServerAbstraction {

	private static final Logger LOG = LoggerFactory.getLogger(SampleIndexServer.class);

	private RegistryManager registryManager;

	@Override
	public MeteorIndexResponseWrapper getDataProviders(MeteorContext context, String ssn) {
		MeteorIndexResponseWrapper response = new MeteorIndexResponseWrapper();

		List<DataProvider> dataProviders;
		try {
			dataProviders = registryManager.getAllDataProviders();
		} catch (RegistryException e) {
			LOG.error("Could not connect to registry", e);
			response.addMessage(MeteorMessage.REGISTRY_NO_CONNECTION.getPropertyRef(), RsMsgLevelEnum.E);
			return response;
		}

		for (DataProvider dataProvider : dataProviders) {
			org.meteornetwork.meteor.common.xml.indexresponse.DataProvider indexDp = new org.meteornetwork.meteor.common.xml.indexresponse.DataProvider();
			indexDp.setEntityID(dataProvider.getInstitutionIdentifier());
			indexDp.setEntityName(dataProvider.getDescription());
			indexDp.setEntityURL(dataProvider.getUrl());
			response.addDataProviders(indexDp);
		}

		return response;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
