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
package org.meteornetwork.meteor.status.manager;

import java.io.StringReader;
import java.util.concurrent.Callable;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StatusQueryCallable implements Callable<MeteorRsMsg> {

	private static final Logger LOG = LoggerFactory.getLogger(StatusQueryCallable.class);
	
	private DataProvider dataProvider;
	private JaxWsProxyFactoryBean dataClientProxyFactory;
	
	@Override
	public MeteorRsMsg call() throws Exception {
		LOG.debug("Calling data provider " + dataProvider.getInstitutionIdentifier() + "...");
		dataClientProxyFactory.setAddress(dataProvider.getUrl());

		DataProviderService dataService = (DataProviderService) dataClientProxyFactory.create();
		String responseXml = dataService.getStatus();
		LOG.debug("Response returned from data provider " + dataProvider.getInstitutionIdentifier());
		
		MeteorRsMsg response = MeteorRsMsg.unmarshal(new StringReader(responseXml));
		return response;
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public JaxWsProxyFactoryBean getDataClientProxyFactory() {
		return dataClientProxyFactory;
	}

	@Autowired
	@Qualifier("dataClientProxyFactory")
	public void setDataClientProxyFactory(JaxWsProxyFactoryBean dataClientProxyFactory) {
		this.dataClientProxyFactory = dataClientProxyFactory;
	}

}
