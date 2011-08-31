package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.DataProviderHPCService;
import org.meteornetwork.meteor.provider.data.adapter.HPCDataQueryAdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderHPCService", serviceName = "DataProviderHPCService")
public class DataProviderHPCServiceImpl implements DataProviderHPCService {

	private static final Log LOG = LogFactory.getLog(DataProviderHPCServiceImpl.class);

	private DataProviderManager dataManager;
	private HPCDataQueryAdapterImpl adapter;

	@Override
	public String submitHPC(String rawHPCMessage) {
		LOG.debug("DP received HPC request: " + rawHPCMessage);

		adapter.setRawHPCMessage(rawHPCMessage.trim());
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseHPCMessage();
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}

	public HPCDataQueryAdapterImpl getAdapter() {
		return adapter;
	}

	@Autowired
	public void setAdapter(HPCDataQueryAdapterImpl adapter) {
		this.adapter = adapter;
	}
	
}
