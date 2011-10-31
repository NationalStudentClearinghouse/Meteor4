package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.ws.DataProviderHPCService;
import org.meteornetwork.meteor.provider.data.adapter.HPCDataQueryAdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderHPCService", serviceName = "DataProviderHPCService")
public class DataProviderHPCServiceImpl implements DataProviderHPCService {

	private static final Logger LOG = LoggerFactory.getLogger(DataProviderHPCServiceImpl.class);

	private DataProviderManager dataManager;

	@Override
	public String submitHPC(String rawHPCMessage) {
		LOG.debug("DP received HPC request: " + rawHPCMessage);

		HPCDataQueryAdapterImpl adapter = getAdapter();
		adapter.setRawHPCMessage(rawHPCMessage.trim());
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseHPCMessage();
	}

	public HPCDataQueryAdapterImpl getAdapter() {
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
