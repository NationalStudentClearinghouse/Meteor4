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

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
