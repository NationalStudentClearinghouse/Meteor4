package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dataProviderServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderService", serviceName = "DataProviderService")
public class DataProviderServiceImpl implements DataProviderService {

	private static final Log LOG = LogFactory.getLog(DataProviderServiceImpl.class);

	private DataProviderManager dataManager;

	@Override
	public String queryDataForBorrower(String requestXML) {
		LOG.debug("DP received requestXML: " + requestXML);
		return "DP response to requestXML.";
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}

}
