package org.meteornetwork.meteor.provider.access.service.adapter;

import java.util.concurrent.Callable;

import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;

public interface DataQueryAdapter extends Callable<MeteorRsMsg> {

	DataProvider getDataProvider();

	void setDataProvider(DataProvider dataProvider);

	String getSsn();

	void setSsn(String ssn);

	AccessProvider getAccessProvider();

	void setAccessProvider(AccessProvider accessProvider);
}
