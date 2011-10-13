package org.meteornetwork.meteor.provider.access.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;
import org.meteornetwork.meteor.provider.access.DataProviderInfo;
import org.meteornetwork.meteor.provider.access.ResponseDataWrapper;
import org.meteornetwork.meteor.provider.access.service.adapter.DataQueryAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class DataQueryService implements ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(DataQueryService.class);

	private static final Long DATAPROVIDER_TIMEOUT_DEFAULT = 20000L;

	private Map<String, String> dataQueryAdapterVersionMap;
	private Properties accessProviderProperties;
	private Properties authenticationProperties;
	private Properties meteorProperties;

	private RegistryManager registryManager;

	private ApplicationContext applicationContext;

	/**
	 * Asynchronously queries data providers for data on the given ssn
	 * 
	 * @param dataProviders
	 *            the data providers
	 * @param ssn
	 * @return responses from the queried data providers that were able to
	 *         respond
	 */
	public List<MeteorRsMsg> getData(ResponseDataWrapper responseData, Set<DataProviderInfo> dataProviders, String ssn) {
		List<MeteorRsMsg> responseDataList = new ArrayList<MeteorRsMsg>();

		ExecutorService threadPool = Executors.newFixedThreadPool(dataProviders.size());
		Map<DataProviderInfo, Future<MeteorRsMsg>> futures = new HashMap<DataProviderInfo, Future<MeteorRsMsg>>();

		Long timeout;
		try {
			timeout = Long.valueOf(accessProviderProperties.getProperty("dataprovider.timeout"));
		} catch (NumberFormatException e) {
			LOG.error("Invalid dataprovider timeout. Using default of " + DATAPROVIDER_TIMEOUT_DEFAULT, e);
			timeout = DATAPROVIDER_TIMEOUT_DEFAULT;
		}

		AccessProvider accessProvider = createAccessProvider(ssn);

		for (DataProviderInfo dataProviderInfo : dataProviders) {
			if (dataProviderInfo.getRegistryInfo() == null) {
				LOG.debug("Data provider (ID: " + dataProviderInfo.getMeteorInstitutionIdentifier() + ") is not configured correctly in the Meteor Registry");
				continue;
			}

			DataQueryAdapter adapter = getAdapter(dataProviderInfo.getRegistryInfo().getMeteorVersion());
			if (adapter == null) {
				LOG.error("Data provider (ID: " + dataProviderInfo.getMeteorInstitutionIdentifier() + ") has no version");
			} else {
				adapter.setDataProviderInfo(dataProviderInfo);
				adapter.setAccessProvider(accessProvider);
				adapter.setSsn(ssn);
				adapter.setMeteorVersion(meteorProperties.getProperty("meteor.version"));

				futures.put(dataProviderInfo, threadPool.submit(adapter));
			}
		}

		threadPool.shutdown();
		try {
			threadPool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOG.warn("ExecutorService was interrupted", e);
		}

		boolean atLeast1DataProviderCommError = false;
		boolean atLeast1DataProviderSuccess = false;

		for (Map.Entry<DataProviderInfo, Future<MeteorRsMsg>> futureEntry : futures.entrySet()) {
			if (futureEntry.getValue().isDone()) {
				try {
					responseDataList.add(futureEntry.getValue().get());
					atLeast1DataProviderSuccess = true;
				} catch (Exception e) {
					LOG.debug("Exception thrown when getting data from data provider (ID: " + futureEntry.getKey().getMeteorInstitutionIdentifier() + ")", e);
					atLeast1DataProviderCommError = true;
					// TODO add data provider to loan locator.
				}
			}
		}

		if (atLeast1DataProviderCommError) {
			if (atLeast1DataProviderSuccess) {
				responseData.addIndexProviderMessage(RsMsgLevelEnum.E, MeteorMessage.DATA_ERROR, null);
			} else {
				responseData.addIndexProviderMessage(RsMsgLevelEnum.E, MeteorMessage.DATA_ERROR_ALL, null);
			}
		}

		return responseDataList;
	}

	private DataQueryAdapter getAdapter(String version) {
		Version versionObj = new Version(version);

		for (Map.Entry<String, String> entry : dataQueryAdapterVersionMap.entrySet()) {
			if (versionObj.matches(entry.getKey())) {
				return (DataQueryAdapter) applicationContext.getBean(entry.getValue());
			}
		}

		return null;
	}

	private AccessProvider createAccessProvider(String ssn) {
		AccessProvider accessProvider = new AccessProvider();
		accessProvider.setMeteorInstitutionIdentifier(authenticationProperties.getProperty("authentication.identifier"));
		// TODO: set user handle
		accessProvider.setUserHandle("User");
		accessProvider.setIssueInstant(Calendar.getInstance().getTime());
		return accessProvider;
	}

	public Map<String, String> getDataQueryAdapterVersionMap() {
		return dataQueryAdapterVersionMap;
	}

	@Resource(name = "dataQueryAdapterVersionMap")
	public void setDataQueryAdapterVersionMap(Map<String, String> dataQueryAdapterVersionMap) {
		this.dataQueryAdapterVersionMap = dataQueryAdapterVersionMap;
	}

	public Properties getAccessProviderProperties() {
		return accessProviderProperties;
	}

	@Autowired
	@Qualifier("AccessProviderProperties")
	public void setAccessProviderProperties(Properties accessProviderProperties) {
		this.accessProviderProperties = accessProviderProperties;
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("AuthenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	public Properties getMeteorProperties() {
		return meteorProperties;
	}

	@Autowired
	@Qualifier("MeteorProperties")
	public void setMeteorProperties(Properties meteorProperties) {
		this.meteorProperties = meteorProperties;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
