package org.meteornetwork.meteor.common.registry;

import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.ws.RegistryService;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.SerializationUtils;

public class RegistryWebServiceClient implements RegistryManager {

	private static final Log LOG = LogFactory.getLog(RegistryWebServiceClient.class);

	private RegistryService registryService;
	private RegistryService failoverRegistryService;

	@Override
	public X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		try {
			return (X509Certificate) SerializationUtils.deserialize(registryService.getCertificate(meteorInstitutionId, providerType));
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return (X509Certificate) SerializationUtils.deserialize(failoverRegistryService.getCertificate(meteorInstitutionId, providerType));
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<IndexProvider> getIndexProviders(Version meteorVersion) throws RegistryException {
		try {
			return registryService.getIndexProviders(meteorVersion);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return failoverRegistryService.getIndexProviders(meteorVersion);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public DataProvider getDataProvider(String meteorInstitutionId) throws RegistryException {
		try {
			return registryService.getDataProvider(meteorInstitutionId);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return failoverRegistryService.getDataProvider(meteorInstitutionId);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<DataProvider> getAllDataProviders() throws RegistryException {
		try {
			return registryService.getAllDataProviders();
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return failoverRegistryService.getAllDataProviders();
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<String> getAliases(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		try {
			return registryService.getAliases(meteorInstitutionId, providerType);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return failoverRegistryService.getAliases(meteorInstitutionId, providerType);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public Integer getAuthenticationLevel(String meteorInstitutionId, String authProcId, ProviderType providerType, Role role) throws RegistryException {
		try {
			return registryService.getAuthenticationLevel(meteorInstitutionId, authProcId, providerType, role);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return failoverRegistryService.getAuthenticationLevel(meteorInstitutionId, authProcId, providerType, role);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<Role> getRoles(String meteorInstitutionId, String authProcId, ProviderType providerType) throws RegistryException {
		try {
			return registryService.getRoles(meteorInstitutionId, authProcId, providerType);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return failoverRegistryService.getRoles(meteorInstitutionId, authProcId, providerType);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	public RegistryService getRegistryService() {
		return registryService;
	}

	@Autowired
	@Qualifier("registryService")
	public void setRegistryService(RegistryService registryService) {
		this.registryService = registryService;
	}

	public RegistryService getFailoverRegistryService() {
		return failoverRegistryService;
	}

	@Autowired
	@Qualifier("failoverRegistryService")
	public void setFailoverRegistryService(RegistryService failoverRegistryService) {
		this.failoverRegistryService = failoverRegistryService;
	}

}
