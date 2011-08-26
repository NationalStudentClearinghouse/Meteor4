package org.meteornetwork.meteor.provider.access.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.access.manager.AccessProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("accessProviderServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.AccessProviderService", serviceName = "AccessProviderService")
public class AccessProviderServiceImpl implements AccessProviderService {

	private static final Log LOG = LogFactory.getLog(AccessProviderServiceImpl.class);

	private AccessProviderManager accessProviderManager;

	@Override
	public String findDataForBorrower(String requestXML) {
		LOG.debug("AP received requestXML: " + requestXML);
		
		// TODO: parse ssn from request xml
		return accessProviderManager.queryMeteor(requestXML);
	}

	public AccessProviderManager getAccessProviderManager() {
		return accessProviderManager;
	}

	@Autowired
	public void setAccessProviderManager(AccessProviderManager accessProviderManager) {
		this.accessProviderManager = accessProviderManager;
	}

}
