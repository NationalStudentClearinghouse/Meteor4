package org.meteornetwork.meteor.provider.index.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.IndexProviderService;
import org.springframework.stereotype.Component;

@Component("indexProviderServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.IndexProviderService", serviceName = "IndexProviderService")
public class IndexProviderServiceImpl implements IndexProviderService {

	private static final Log LOG = LogFactory.getLog(IndexProviderServiceImpl.class);

	@Override
	public String findDataProvidersForBorrower(String requestXML) {
		LOG.debug("IP received requestXML: " + requestXML);
		return "IP response to requestXML.";
	}
}
