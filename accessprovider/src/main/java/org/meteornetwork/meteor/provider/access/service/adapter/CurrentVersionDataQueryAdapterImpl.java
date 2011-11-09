package org.meteornetwork.meteor.provider.access.service.adapter;

import java.io.StringReader;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.provider.access.DataProviderInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("currentVersionDataQueryAdapterImpl")
@Scope("prototype")
public class CurrentVersionDataQueryAdapterImpl implements DataQueryAdapter, ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(CurrentVersionDataQueryAdapterImpl.class);

	private DataProviderInfo dataProvider;
	private AccessProvider accessProvider;
	private String ssn;
	private String meteorVersion;

	private ApplicationContext applicationContext;

	@Override
	public MeteorRsMsg call() throws Exception {
		LOG.debug("Calling data provider (ID: " + dataProvider.getMeteorInstitutionIdentifier() + ", Version: " + dataProvider.getRegistryInfo().getMeteorVersion() + " for ssn " + ssn + ")");

		LOG.debug("Marshalling request (ID: " + dataProvider.getMeteorInstitutionIdentifier() + ", Version: " + dataProvider.getRegistryInfo().getMeteorVersion() + " for ssn " + ssn + ")");
		MeteorDataRequest request = createRequest();
		StringWriter marshalledRequest = new StringWriter();
		request.marshal(marshalledRequest);

		LOG.debug("Creating client proxy factory (ID: " + dataProvider.getMeteorInstitutionIdentifier() + ", Version: " + dataProvider.getRegistryInfo().getMeteorVersion() + " for ssn " + ssn + ")");
		JaxWsProxyFactoryBean dataClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("dataClientProxyFactory");
		dataClientProxyFactory.setAddress(dataProvider.getRegistryInfo().getUrl());

		LOG.debug("Creating web service client (ID: " + dataProvider.getMeteorInstitutionIdentifier() + ", Version: " + dataProvider.getRegistryInfo().getMeteorVersion() + " for ssn " + ssn + ")");
		DataProviderService dataService = (DataProviderService) dataClientProxyFactory.create();
		LOG.debug("Calling web service (ID: " + dataProvider.getMeteorInstitutionIdentifier() + ", Version: " + dataProvider.getRegistryInfo().getMeteorVersion() + " for ssn " + ssn + ")");
		String responseXml = dataService.queryDataForBorrower(marshalledRequest.toString());

		MeteorRsMsg response = MeteorRsMsg.unmarshal(new StringReader(responseXml));
		LOG.debug("Response from data provider (ID: " + dataProvider.getMeteorInstitutionIdentifier() + "): " + responseXml);
		return response;
	}

	private MeteorDataRequest createRequest() {
		MeteorDataRequest request = new MeteorDataRequest();
		request.setAccessProvider(accessProvider);
		request.setMeteorVersion(meteorVersion);
		request.setSSN(ssn);
		return request;
	}

	@Override
	public DataProviderInfo getDataProviderInfo() {
		return dataProvider;
	}

	@Override
	public void setDataProviderInfo(DataProviderInfo dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	public String getSsn() {
		return ssn;
	}

	@Override
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	@Override
	public String getMeteorVersion() {
		return meteorVersion;
	}

	@Override
	public void setMeteorVersion(String meteorVersion) {
		this.meteorVersion = meteorVersion;
	}

	@Override
	public AccessProvider getAccessProvider() {
		return accessProvider;
	}

	@Override
	public void setAccessProvider(AccessProvider accessProvider) {
		this.accessProvider = accessProvider;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
