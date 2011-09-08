package org.meteornetwork.meteor.provider.access.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.security.Role;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.access.manager.AccessProviderManager;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.AccessProviderService", serviceName = "AccessProviderService")
public class AccessProviderServiceImpl implements AccessProviderService {

	private static final Log LOG = LogFactory.getLog(AccessProviderServiceImpl.class);

	private AccessProviderManager accessProviderManager;

	private RequestInfo requestInfo;
	private RegistryManager registryManager;

	@Override
	public String findDataForBorrower(String requestXML) {
		LOG.debug("AP received requestXML: " + requestXML);

		// TODO delete
		requestInfo.setMeteorInstitutionIdentifier("AP33LTI");
		requestInfo.setLevel(3);
		requestInfo.setRole(Role.FAA);
		requestInfo.setUserHandle("faa");
		requestInfo.setOrganizationID("OPEID");
		requestInfo.setOrganizationIDType("SCHOOL");
		requestInfo.setOrganizationType("SCHOOL");
		// end delete

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

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	@Autowired
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
