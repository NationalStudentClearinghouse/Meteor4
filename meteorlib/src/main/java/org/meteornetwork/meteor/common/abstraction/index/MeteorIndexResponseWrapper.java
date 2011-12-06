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
package org.meteornetwork.meteor.common.abstraction.index;

import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProviders;
import org.meteornetwork.meteor.common.xml.indexresponse.IndexProviderData;
import org.meteornetwork.meteor.common.xml.indexresponse.IndexProviderMessages;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;

public class MeteorIndexResponseWrapper {

	private final MeteorIndexResponse response;

	public MeteorIndexResponseWrapper() {
		response = new MeteorIndexResponse();
		response.setDataProviders(new DataProviders());
	}

	/**
	 * Add index provider information to the response.
	 * 
	 * @param id
	 *            the ID of this index provider
	 * @param name
	 *            the name of this index provider
	 * @param url
	 *            the contact URL of this index provider
	 */
	public void setIndexProviderData(String id, String name, String url) {
		IndexProviderData data = new IndexProviderData();
		data.setEntityID(id);
		data.setEntityName(name);
		data.setEntityURL(url);
		response.setIndexProviderData(data);
	}

	/**
	 * Add a message to this response
	 * 
	 * @param messageText
	 *            the text of the message
	 * @param level
	 *            the severity level of the message
	 */
	public void addMessage(String messageText, RsMsgLevelEnum level) {
		Message message = new Message();
		message.setRsMsg(messageText);
		message.setRsMsgLevel(level.name());

		if (response.getIndexProviderMessages() == null) {
			response.setIndexProviderMessages(new IndexProviderMessages());
		}

		response.getIndexProviderMessages().addMessage(message);
	}

	/**
	 * Add one or more Data Provider objects to the response
	 * 
	 * @param dataProviders
	 *            the data providers to add to the response
	 */
	public void addDataProviders(DataProvider... dataProviders) {
		for (DataProvider dataProvider : dataProviders) {
			response.getDataProviders().addDataProvider(dataProvider);
		}
	}

	/**
	 * Add Data Provider objects to the response
	 * 
	 * @param dataProviders
	 *            an iterable collection of Data Providers to add to the
	 *            response
	 */
	public void addDataProviders(Iterable<DataProvider> dataProviders) {
		for (DataProvider dataProvider : dataProviders) {
			response.getDataProviders().addDataProvider(dataProvider);
		}
	}

	/**
	 * Access a mutable version of the response.
	 * 
	 * @return A mutable version of the internal MeteorIndexResponse object
	 */
	public MeteorIndexResponse getResponse() {
		return response;
	}

}
