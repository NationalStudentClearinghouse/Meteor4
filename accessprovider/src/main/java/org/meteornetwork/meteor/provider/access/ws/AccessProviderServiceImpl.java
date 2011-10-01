package org.meteornetwork.meteor.provider.access.ws;

import java.util.Properties;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.provider.access.manager.AccessProviderManager;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.TokenAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.AccessProviderService", serviceName = "AccessProviderService")
public class AccessProviderServiceImpl implements AccessProviderService {

	private static final Log LOG = LogFactory.getLog(AccessProviderServiceImpl.class);

	private AccessProviderManager accessProviderManager;

	private Properties authenticationProperties;

	private RequestInfo requestInfo;
	private RegistryManager registryManager;

	@Override
	public String findDataForBorrower(String ssn, TokenAttributes meteorAttributes) {
		// TODO refactor the way saml attributes are passed -- can we now pass
		// around a signed assertion using a custom CXF interceptor?
		LOG.debug("AP received request for ssn: " + ssn);

		requestInfo.setMeteorInstitutionIdentifier(authenticationProperties.getProperty("authentication.identifier"));
		requestInfo.setSecurityToken(new SecurityTokenImpl());
		requestInfo.getSecurityToken().setMeteorAttributes(meteorAttributes);

		return accessProviderManager.queryMeteor(ssn);
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

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("AuthenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

}
