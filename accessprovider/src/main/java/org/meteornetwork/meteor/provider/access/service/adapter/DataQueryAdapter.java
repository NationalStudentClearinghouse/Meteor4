package org.meteornetwork.meteor.provider.access.service.adapter;

import java.util.concurrent.Callable;

import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;

/**
 * Interface for the access provider translation layer when querying for
 * borrower data. Implementors translate the request parameters into a request
 * for whatever version of meteor the data provider supports.
 * 
 * @author jlazos
 * 
 */
public interface DataQueryAdapter extends Callable<MeteorRsMsg> {

	DataProvider getDataProvider();

	/**
	 * The data provider to query against
	 * 
	 * @param dataProvider
	 */
	void setDataProvider(DataProvider dataProvider);

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
