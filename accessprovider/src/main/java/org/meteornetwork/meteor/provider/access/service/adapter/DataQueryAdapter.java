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
package org.meteornetwork.meteor.provider.access.service.adapter;

import java.util.concurrent.Callable;

import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.provider.access.DataProviderInfo;


/**
 * Interface for the access provider translation layer when querying for
 * borrower data. Implementors translate the request parameters into a request
 * for whatever version of meteor the data provider supports.
 * 
 * @author jlazos
 * 
 */
public interface DataQueryAdapter extends Callable<MeteorRsMsg> {

	DataProviderInfo getDataProviderInfo();

	/**
	 * The data provider to query against
	 * 
	 * @param dataProvider
	 */
	void setDataProviderInfo(DataProviderInfo dataProvider);

	String getSsn();

	/**
	 * The ssn to query
	 * 
	 * @param ssn
	 */
	void setSsn(String ssn);

	AccessProvider getAccessProvider();

	/**
	 * Access provider details to pass to data provider
	 * 
	 * @param accessProvider
	 */
	void setAccessProvider(AccessProvider accessProvider);

	String getMeteorVersion();

	/**
	 * The version of the meteor schema to return
	 * 
	 * @param meteorVersion
	 */
	void setMeteorVersion(String meteorVersion);
}
