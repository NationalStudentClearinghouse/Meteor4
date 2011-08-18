package org.meteornetwork.meteor.provider.access.ws;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.access.DataServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("accessProviderServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.AccessProviderService", serviceName = "AccessProviderService")
public class AccessProviderServiceImpl implements AccessProviderService {

	private static final Logger LOG = Logger.getLogger(AccessProviderServiceImpl.class);
	
	private DataServiceManager dataServiceManager;
	
	@Override
	public String findDataForBorrower(String requestXML) {
		LOG.debug("AP received requestXML: " + requestXML);
		LOG.debug(dataServiceManager.getData());
		return "AP response to requestXML.";
	}

	public DataServiceManager getDataServiceManager() {
		return dataServiceManager;
	}

	@Autowired
	public void setDataServiceManager(DataServiceManager dataServiceManager) {
		this.dataServiceManager = dataServiceManager;
	}

}
