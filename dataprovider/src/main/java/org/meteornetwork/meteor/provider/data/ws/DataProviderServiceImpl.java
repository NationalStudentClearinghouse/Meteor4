package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.provider.data.adapter.DataQueryAdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderService", serviceName = "DataProviderService")
public class DataProviderServiceImpl implements DataProviderService {

	private static final Logger LOG = LoggerFactory.getLogger(DataProviderServiceImpl.class);

	private DataProviderManager dataManager;

	@Override
	public String queryDataForBorrower(String requestXml) {
		LOG.debug("DP received request: " + requestXml);

		DataQueryAdapterImpl adapter = getAdapter();
		adapter.setRequestXml(requestXml);
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseXml();
	}

	@Override
	public String getStatus() {
		LOG.debug("Received status request");
		return dataManager.createStatusResponse();
	}

	public DataQueryAdapterImpl getAdapter() {
		// overridden by spring method-injection
		return null;
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}

}
