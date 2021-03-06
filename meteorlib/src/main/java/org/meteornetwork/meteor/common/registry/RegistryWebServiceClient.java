/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.common.registry;

import java.security.cert.X509Certificate;
import java.util.List;

import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.ws.RegistryService;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

public class RegistryWebServiceClient implements RegistryManager {

	private static final Logger LOG = LoggerFactory.getLogger(RegistryWebServiceClient.class);

	@Override
	public X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		try {
			return (X509Certificate) SerializationUtils.deserialize(getRegistryService().getCertificate(meteorInstitutionId, providerType));
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return (X509Certificate) SerializationUtils.deserialize(getFailoverRegistryService().getCertificate(meteorInstitutionId, providerType));
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<IndexProvider> getIndexProviders(Version meteorVersion) throws RegistryException {
		try {
			return getRegistryService().getIndexProviders(meteorVersion);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getIndexProviders(meteorVersion);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public DataProvider getDataProvider(String meteorInstitutionId) throws RegistryException {
		try {
			return getRegistryService().getDataProvider(meteorInstitutionId);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getDataProvider(meteorInstitutionId);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<DataProvider> getAllDataProviders() throws RegistryException {
		try {
			return getRegistryService().getAllDataProviders();
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getAllDataProviders();
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<String> getAliases(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		try {
			return getRegistryService().getAliases(meteorInstitutionId, providerType);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getAliases(meteorInstitutionId, providerType);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public Integer getAuthenticationLevel(String meteorInstitutionId, String authProcId, ProviderType providerType, Role role) throws RegistryException {
		try {
			return getRegistryService().getAuthenticationLevel(meteorInstitutionId, authProcId, providerType, role);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getAuthenticationLevel(meteorInstitutionId, authProcId, providerType, role);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public List<Role> getRoles(String meteorInstitutionId, String authProcId, ProviderType providerType) throws RegistryException {
		try {
			return getRegistryService().getRoles(meteorInstitutionId, authProcId, providerType);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getRoles(meteorInstitutionId, authProcId, providerType);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@Override
	public String getVersion(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		try {
			return getRegistryService().getVersion(meteorInstitutionId, providerType);
		} catch (Exception e) {
			LOG.debug("Exception occurred when trying to contact web service registry. Trying failover registry", e);
			try {
				return getFailoverRegistryService().getVersion(meteorInstitutionId, providerType);
			} catch (Exception e1) {
				LOG.debug("Exception occurred when trying to contact failover web service registry.", e1);
				throw new RegistryException(e1);
			}
		}
	}

	public RegistryService getRegistryService() {
		// overridden by spring method-injection
		return null;
	}

	public RegistryService getFailoverRegistryService() {
		// overridden by spring method-injection
		return null;
	}

}
