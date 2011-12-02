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
package org.meteornetwork.meteor.provider.data;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import org.apache.cxf.helpers.IOUtils;
import org.meteornetwork.meteor.common.abstraction.data.DataServerAbstraction;
import org.meteornetwork.meteor.common.abstraction.data.MeteorContext;
import org.meteornetwork.meteor.common.abstraction.data.MeteorDataResponseWrapper;
import org.meteornetwork.meteor.common.xml.dataresponse.types.RsMsgLevelEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple data server implementation that gets Meteor XML data from a specified
 * URL
 * 
 * @author jlazos
 * 
 */
public class RemoteDataServer implements DataServerAbstraction {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteDataServer.class);

	private ResourceBundle dataProviderProperties;

	private final String ADDRESS;

	public RemoteDataServer() {
		dataProviderProperties = ResourceBundle.getBundle("dataprovider");
		ADDRESS = dataProviderProperties.getString("remotedataserver.url");
	}

	@Override
	public MeteorDataResponseWrapper getData(MeteorContext context, String ssn) {
		String charset = "UTF-8";

		String role = context.getSecurityToken() != null && context.getSecurityToken().getRole() != null ? context.getSecurityToken().getRole().getName() : "";

		try {
			String query = String.format("ssn=%s&role=%s", URLEncoder.encode(ssn, "UTF-8"), URLEncoder.encode(role, "UTF-8"));
			URLConnection connection = new URL(ADDRESS + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);

			return new MeteorDataResponseWrapper(IOUtils.readStringFromStream(connection.getInputStream()));
		} catch (Exception e) {
			LOG.error("Could not get data from remote resource", e);
			MeteorDataResponseWrapper minimal = new MeteorDataResponseWrapper();
			minimal.addMessage("Unable to retrieve data, please try again later", RsMsgLevelEnum.E.name());
			minimal.createMinimalResponse();

			return minimal;
		}
	}

}
