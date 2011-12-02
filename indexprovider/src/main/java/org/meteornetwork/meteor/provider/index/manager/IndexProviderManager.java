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
package org.meteornetwork.meteor.provider.index.manager;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.abstraction.index.IndexServerAbstraction;
import org.meteornetwork.meteor.common.abstraction.index.MeteorContext;
import org.meteornetwork.meteor.common.abstraction.index.MeteorIndexResponseWrapper;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.xml.indexrequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//@Service
public class IndexProviderManager {

	private static final Logger LOG = LoggerFactory.getLogger(IndexProviderManager.class);

	private IndexServerAbstraction indexServer;
	private Properties indexProviderProperties;
	
	public MeteorIndexResponse findDataProvidersForBorrower(AccessProvider accessProvider, String ssn) {

		LOG.debug("Received request from AP " + accessProvider.getMeteorInstitutionIdentifier() + " for SSN " + ssn);

		MeteorContext context = new MeteorContext();
		context.setAccessProvider(accessProvider);
		context.setSecurityToken(getRequestInfo().getSecurityToken());
		MeteorIndexResponseWrapper response = indexServer.getDataProviders(context, ssn);
		
		String indexProviderId = indexProviderProperties.getProperty("IndexProvider.ID");
		String indexProviderName = indexProviderProperties.getProperty("IndexProvider.Name");
		String indexProviderUrl = indexProviderProperties.getProperty("IndexProvider.URL");
		response.setIndexProviderData(indexProviderId, indexProviderName, indexProviderUrl);
		
		return response.getResponse();
	}

	public RequestInfo getRequestInfo() {
		// overridden by spring method injection
		return null;
	}

	public IndexServerAbstraction getIndexServer() {
		return indexServer;
	}

	@Autowired
	@Qualifier("IndexServerAbstractionImpl")
	public void setIndexServer(IndexServerAbstraction indexServer) {
		this.indexServer = indexServer;
	}

	public Properties getIndexProviderProperties() {
		return indexProviderProperties;
	}

	@Autowired
	@Qualifier("IndexProviderProperties")
	public void setIndexProviderProperties(Properties indexProviderProperties) {
		this.indexProviderProperties = indexProviderProperties;
	}

}
