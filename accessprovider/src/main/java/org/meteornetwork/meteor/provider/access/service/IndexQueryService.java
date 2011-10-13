package org.meteornetwork.meteor.provider.access.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.ws.IndexProviderService;
import org.meteornetwork.meteor.common.xml.indexrequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.indexrequest.MeteorIndexRequest;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;
import org.meteornetwork.meteor.provider.access.DataProviderInfo;
import org.meteornetwork.meteor.provider.access.MeteorQueryException;
import org.meteornetwork.meteor.provider.access.ResponseDataWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class IndexQueryService implements ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(IndexQueryService.class);

	private Properties authenticationProperties;
	private Properties meteorProperties;
	private ApplicationContext applicationContext;

	private RegistryManager registryManager;

	/**
	 * Gets a list of data providers that have data for the provided SSN
	 * 
	 * @param ssn
	 *            the ssn of the borrower to get data for
	 * @param responseData
	 *            the response data object to write any index provider messages
	 *            to
	 * @return data provider information returned from calls to index providers
	 * @throws RegistryException
	 */
	public Set<DataProviderInfo> getDataProviders(String ssn, ResponseDataWrapper responseData) throws MeteorQueryException {
		LOG.debug("Calling index providers");

		List<IndexProvider> indexProviders = null;
		try {
			indexProviders = registryManager.getIndexProviders(new Version(meteorProperties.getProperty("meteor.version")));
		} catch (RegistryException e1) {
			responseData.addIndexProviderMessage(RsMsgLevelEnum.E, MeteorMessage.REGISTRY_NO_CONNECTION, null);
			throw new MeteorQueryException("Could not get list of index providers from registry", e1);
		}

		if (indexProviders == null || indexProviders.isEmpty()) {
			return broadcastMode(responseData);
		}

		Set<DataProviderInfo> dataProviders = new HashSet<DataProviderInfo>();
		boolean atLeast1IndexProviderSuccessful = false;
		for (IndexProvider ip : indexProviders) {
			MeteorIndexResponse response = null;
			try {
				response = callIndexProvider(createRequest(ssn), ip.getUrl());
			} catch (MeteorQueryException e) {
				LOG.debug("Could not call index provider " + ip.getInstitutionIdentifier(), e);
				continue;
			}

			atLeast1IndexProviderSuccessful = true;

			if (response.getIndexProviderMessages() != null && response.getIndexProviderMessages().getMessageCount() > 0) {
				for (Message message : response.getIndexProviderMessages().getMessage()) {
					LOG.debug("Adding message from index provider: \"" + message.getRsMsg() + "\"");
					responseData.addIndexProviderMessage(message);
				}
			}

			if (response.getDataProviders().getDataProviderCount() > 0) {
				for (org.meteornetwork.meteor.common.xml.indexresponse.DataProvider dataProvider : response.getDataProviders().getDataProvider()) {
					LOG.debug("Adding data provider with ID '" + dataProvider.getEntityID() + "'");
					DataProviderInfo dpInfo = new DataProviderInfo(dataProvider.getEntityID());
					dpInfo.setIndexProviderInfo(dataProvider);
					try {
						dpInfo.setRegistryInfo(registryManager.getDataProvider(dpInfo.getMeteorInstitutionIdentifier()));
					} catch (RegistryException e) {
						LOG.debug("Could not get info from registry for data provider " + dpInfo.getMeteorInstitutionIdentifier(), e);
						continue;
					}
					dataProviders.add(dpInfo);
				}
			}
			break;
		}

		if (!atLeast1IndexProviderSuccessful) {
			return broadcastMode(responseData);
		}

		return dataProviders;
	}

	private MeteorIndexRequest createRequest(String ssn) {
		MeteorIndexRequest request = new MeteorIndexRequest();

		AccessProvider accessProvider = new AccessProvider();
		accessProvider.setMeteorInstitutionIdentifier(authenticationProperties.getProperty("authentication.identifier"));
		// TODO: set user handle
		accessProvider.setUserHandle("User");
		accessProvider.setIssueInstant(Calendar.getInstance().getTime());

		request.setAccessProvider(accessProvider);
		request.setSSN(ssn);
		return request;
	}

	private MeteorIndexResponse callIndexProvider(MeteorIndexRequest request, String indexProviderUrl) throws MeteorQueryException {
		StringWriter marshalledRequest = new StringWriter();
		try {
			request.marshal(marshalledRequest);

			JaxWsProxyFactoryBean indexClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("indexClientProxyFactory");
			indexClientProxyFactory.setAddress(indexProviderUrl);

			IndexProviderService indexService = (IndexProviderService) indexClientProxyFactory.create();
			String response = indexService.findDataProvidersForBorrower(marshalledRequest.toString());

			return MeteorIndexResponse.unmarshal(new StringReader(response));

		} catch (Exception e) {
			throw new MeteorQueryException("Exception calling index provider at " + indexProviderUrl, e);
		}
	}

	private Set<DataProviderInfo> broadcastMode(ResponseDataWrapper responseData) throws MeteorQueryException {
		LOG.debug("No index providers found. Attempting broadcast mode");
		List<org.meteornetwork.meteor.common.registry.data.DataProvider> registryDataProviders = null;
		try {
			registryDataProviders = registryManager.getAllDataProviders();
		} catch (RegistryException e) {
			responseData.addIndexProviderMessage(RsMsgLevelEnum.E, MeteorMessage.REGISTRY_NO_CONNECTION, null);
			throw new MeteorQueryException("Could not get list of data providers from the registry", e);
		}

		if (registryDataProviders == null || registryDataProviders.isEmpty()) {
			throw new MeteorQueryException("No data providers found in registry");
		}

		Set<DataProviderInfo> dataProviders = new HashSet<DataProviderInfo>();
		for (org.meteornetwork.meteor.common.registry.data.DataProvider dataProvider : registryDataProviders) {
			DataProviderInfo dpInfo = new DataProviderInfo(dataProvider.getInstitutionIdentifier());
			dpInfo.setRegistryInfo(dataProvider);
			dataProviders.add(dpInfo);
		}

		return dataProviders;
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
