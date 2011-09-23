package org.meteornetwork.meteor.provider.access.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.access.manager.AccessProviderManager;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
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
		requestInfo.setMeteorInstitutionIdentifier("LTI_AP33");
		requestInfo.setSecurityToken(new SecurityTokenImpl());
		requestInfo.getSecurityToken().setLevel(3);
		requestInfo.getSecurityToken().setRole(Role.FAA);
		requestInfo.getSecurityToken().setUserHandle("faa");
		requestInfo.getSecurityToken().setOrganizationId("12324");
		requestInfo.getSecurityToken().setOrganizationIdType("OPEID");
		requestInfo.getSecurityToken().setOrganizationType("SCHOOL");
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
