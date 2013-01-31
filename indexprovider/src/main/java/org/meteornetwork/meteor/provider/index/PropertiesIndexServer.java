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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;

/**
 * Loads data providers from a properties file on the classpath. Specify the
 * properties file in indexprovider.properties, e.g.
 * 
 * propertiesindexserver.propertyfile=indexresponsesample
 * 
 * Do not specify the ".properties" extension!
 * 
 * @author jlazos
 * 
 */
public class PropertiesIndexServer implements IndexServerAbstraction {

	private final ResourceBundle indexResponseProperties;

	public PropertiesIndexServer() {
		ResourceBundle indexRespProps;
		try {
			ResourceBundle indexProviderProperties = ResourceBundle.getBundle("indexprovider");
			indexRespProps = ResourceBundle.getBundle(indexProviderProperties.getString("propertiesindexserver.propertyfile"));
		} catch (MissingResourceException e) {
			indexRespProps = null;
		}

		indexResponseProperties = indexRespProps;
	}

	@Override
	public MeteorIndexResponseWrapper getDataProviders(MeteorContext context, String ssn) {
		MeteorIndexResponseWrapper response = new MeteorIndexResponseWrapper();

		if (indexResponseProperties == null) {
			response.addMessage("Could not find property file with index response data", RsMsgLevelEnum.E);
			return response;
		}

		try {
			int i = 1;
			while (true) {
				DataProvider dataProvider = new DataProvider();
				dataProvider.setEntityID(indexResponseProperties.getString("dp." + i + ".id"));

				try {
					dataProvider.setEntityName(indexResponseProperties.getString("dp." + i + ".name"));
				} catch (MissingResourceException e) {
					// allow null
				}

				try {
					dataProvider.setEntityURL(indexResponseProperties.getString("dp." + i + ".url"));
				} catch (MissingResourceException e) {
					// allow null
				}

				response.addDataProviders(dataProvider);
				++i;
			}
		} catch (MissingResourceException e) {
			// no more data providers can be found
		}

		return response;
	}

}
