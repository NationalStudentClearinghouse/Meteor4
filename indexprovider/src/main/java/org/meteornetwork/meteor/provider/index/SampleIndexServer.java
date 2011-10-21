package org.meteornetwork.meteor.provider.index;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Retrieves all data providers available in the registry
 * 
 * @author jlazos
 * 
 */
public class SampleIndexServer implements IndexServerAbstraction {

	private static final Log LOG = LogFactory.getLog(SampleIndexServer.class);

	private RegistryManager registryManager;

	@Override
	public MeteorIndexResponseWrapper getDataProviders(MeteorContext context, String ssn) {
		MeteorIndexResponseWrapper response = new MeteorIndexResponseWrapper();

		List<DataProvider> dataProviders;
		try {
			dataProviders = registryManager.getAllDataProviders();
		} catch (RegistryException e) {
			LOG.error("Could not connect to registry", e);
			response.addMessage(MeteorMessage.REGISTRY_NO_CONNECTION.getPropertyRef(), RsMsgLevelEnum.E);
			return response;
		}

		for (DataProvider dataProvider : dataProviders) {
			org.meteornetwork.meteor.common.xml.indexresponse.DataProvider indexDp = new org.meteornetwork.meteor.common.xml.indexresponse.DataProvider();
			indexDp.setEntityID(dataProvider.getInstitutionIdentifier());
			indexDp.setEntityName(dataProvider.getDescription());
			indexDp.setEntityURL(dataProvider.getUrl());
			response.addDataProviders(indexDp);
		}

		return response;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
