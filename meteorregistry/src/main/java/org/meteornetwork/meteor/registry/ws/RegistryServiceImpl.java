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
package org.meteornetwork.meteor.registry.ws;

import java.util.ArrayList;

import javax.jws.WebService;

import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.ws.RegistryService;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.RegistryService", serviceName = "RegistryService")
public class RegistryServiceImpl implements RegistryService {

	private RegistryManager registryManager;

	@Override
	public byte[] getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		return SerializationUtils.serialize(registryManager.getCertificate(meteorInstitutionId, providerType));
	}

	@Override
	public ArrayList<IndexProvider> getIndexProviders(Version meteorVersion) throws RegistryException {
		ArrayList<IndexProvider> returnList = new ArrayList<IndexProvider>();
		returnList.addAll(registryManager.getIndexProviders(meteorVersion));
		return returnList;
	}

	@Override
	public DataProvider getDataProvider(String meteorInstitutionId) throws RegistryException {
		return registryManager.getDataProvider(meteorInstitutionId);
	}

	@Override
	public ArrayList<DataProvider> getAllDataProviders() throws RegistryException {
		ArrayList<DataProvider> returnList = new ArrayList<DataProvider>();
		returnList.addAll(registryManager.getAllDataProviders());
		return returnList;
	}

	@Override
	public ArrayList<String> getAliases(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		ArrayList<String> returnList = new ArrayList<String>();
		returnList.addAll(registryManager.getAliases(meteorInstitutionId, providerType));
		return returnList;
	}

	@Override
	public Integer getAuthenticationLevel(String meteorInstitutionId, String authProcId, ProviderType providerType, Role role) throws RegistryException {
		return registryManager.getAuthenticationLevel(meteorInstitutionId, authProcId, providerType, role);
	}

	@Override
	public ArrayList<Role> getRoles(String meteorInstitutionId, String authProcId, ProviderType providerType) throws RegistryException {
		ArrayList<Role> returnList = new ArrayList<Role>();
		returnList.addAll(registryManager.getRoles(meteorInstitutionId, authProcId, providerType));
		return returnList;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
