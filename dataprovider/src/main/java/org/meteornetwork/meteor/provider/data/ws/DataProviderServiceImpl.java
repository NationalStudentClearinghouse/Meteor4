package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.provider.data.adapter.DataQueryAdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderService", serviceName = "DataProviderService")
public class DataProviderServiceImpl implements DataProviderService {

	private static final Log LOG = LogFactory.getLog(DataProviderServiceImpl.class);

	private DataProviderManager dataManager;
	private DataQueryAdapterImpl adapter;

	@Override
	public String queryDataForBorrower(String requestXml) {
		LOG.debug("DP received request: " + requestXml);

		adapter.setRequestXml(requestXml);
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseXml();
	}

	@Override
	public String getStatus() {
		LOG.debug("Received status request");
		return dataManager.createStatusResponse();
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}

	public DataQueryAdapterImpl getAdapter() {
		return adapter;
	}

	@Autowired
	public void setAdapter(DataQueryAdapterImpl adapter) {
		this.adapter = adapter;
	}

}
