package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.springframework.stereotype.Component;

@Component("dataProviderServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderService", serviceName = "DataProviderService")
public class DataProviderServiceImpl implements DataProviderService {

	private static final Logger LOG = Logger.getLogger(DataProviderServiceImpl.class);
	
	@Override
	public String queryDataForBorrower(String requestXML) {
		LOG.debug("DP received requestXML: " + requestXML);
		return "DP response to requestXML.";
	}

}
