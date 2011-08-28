package org.meteornetwork.meteor.provider.access.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.ws.IndexProviderService;
import org.meteornetwork.meteor.common.xml.indexrequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.indexrequest.MeteorIndexRequest;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
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
	private ApplicationContext applicationContext;

	/**
	 * Gets a list of data providers that have data for the provided SSN
	 * 
	 * @param ssn
	 *            the ssn of the borrower to get data for
	 * @param responseData
	 *            the response data object to write any index provider messages
	 *            to
	 * @return data provider information returned from calls to index providers
	 */
	public Set<DataProvider> getDataProviders(String ssn, ResponseDataWrapper responseData) {
		// TODO: get index providers from registry. edit this method to call
		// all index providers with the same version as this access provider and
		// aggregate their results
		String addresses[] = new String[] { "http://localhost:8080/indexprovider/services/IndexProviderService" };

		Set<DataProvider> dataProviders = new HashSet<DataProvider>();
		for (String address : addresses) {
			MeteorIndexResponse response = callIndexProvider(createRequest(ssn), address);

			if (response == null) {
				continue;
			}

			if (response.getIndexProviderMessages() != null && response.getIndexProviderMessages().getMessageCount() > 0) {
				for (Message message : response.getIndexProviderMessages().getMessage()) {
					LOG.debug("Adding message from index provider: \"" + message.getRsMsg() + "\"");
					responseData.addIndexProviderMessage(message);
				}
			}

			if (response.getDataProviders().getDataProviderCount() > 0) {
				for (DataProvider dataProvider : response.getDataProviders().getDataProvider()) {
					LOG.debug("Adding data provider with ID '" + dataProvider.getEntityID() + "'");
					dataProviders.add(dataProvider);
				}
			}
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

	private MeteorIndexResponse callIndexProvider(MeteorIndexRequest request, String indexProviderUrl) {
		StringWriter marshalledRequest = new StringWriter();
		try {
			request.marshal(marshalledRequest);
		} catch (Exception e) {
			LOG.error("Cannot marshal index provider request", e);
			return null;
		}

		JaxWsProxyFactoryBean indexClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("indexClientProxyFactory");
		indexClientProxyFactory.setAddress(indexProviderUrl);

		IndexProviderService indexService = (IndexProviderService) indexClientProxyFactory.create();
		String response = indexService.findDataProvidersForBorrower(marshalledRequest.toString());

		if (response == null) {
			return null;
		}

		try {
			return MeteorIndexResponse.unmarshal(new StringReader(response));
		} catch (Exception e) {
			LOG.error("Cannot unmarshal index provider response", e);
			return null;
		}
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("AuthenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
