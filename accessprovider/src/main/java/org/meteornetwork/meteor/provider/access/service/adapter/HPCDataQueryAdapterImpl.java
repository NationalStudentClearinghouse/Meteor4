package org.meteornetwork.meteor.provider.access.service.adapter;

import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;

public class HPCDataQueryAdapterImpl implements DataQueryAdapter {

	private DataProvider dataProvider;
	private AccessProvider accessProvider;
	private String ssn;
	private String meteorVersion;
	
	@Override
	public MeteorRsMsg call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataProvider getDataProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataProvider(DataProvider dataProvider) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSsn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSsn(String ssn) {
		// TODO Auto-generated method stub

	}

	@Override
	public AccessProvider getAccessProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccessProvider(AccessProvider accessProvider) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMeteorVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMeteorVersion(String meteorVersion) {
		// TODO Auto-generated method stub
		
	}

}
