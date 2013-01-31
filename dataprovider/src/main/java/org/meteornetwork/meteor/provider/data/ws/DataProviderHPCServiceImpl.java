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
package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.ws.DataProviderHPCService;
import org.meteornetwork.meteor.provider.data.adapter.HPCDataQueryAdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderHPCService", serviceName = "DataProviderHPCService")
public class DataProviderHPCServiceImpl implements DataProviderHPCService {

	private static final Logger LOG = LoggerFactory.getLogger(DataProviderHPCServiceImpl.class);

	private DataProviderManager dataManager;

	@Override
	public String submitHPC(String rawHPCMessage) {
		LOG.debug("DP received HPC request: " + rawHPCMessage);

		HPCDataQueryAdapterImpl adapter = getAdapter();
		adapter.setRawHPCMessage(rawHPCMessage.trim());
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseHPCMessage();
	}

	public HPCDataQueryAdapterImpl getAdapter() {
		// overridden by spring method-injection
		return null;
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}

}
