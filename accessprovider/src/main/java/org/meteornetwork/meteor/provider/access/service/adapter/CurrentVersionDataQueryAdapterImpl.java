package org.meteornetwork.meteor.provider.access.service.adapter;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static final Log LOG = LogFactory.getLog(CurrentVersionDataQueryAdapterImpl.class);
	
	private DataProviderInfo dataProvider;
	private AccessProvider accessProvider;
	private String ssn;
	private String meteorVersion;

	private ApplicationContext applicationContext;

	@Override
	public MeteorRsMsg call() throws Exception {
		LOG.debug("Calling data provider (ID: " + dataProvider.getMeteorInstitutionIdentifier() + ", Version: " + dataProvider.getRegistryInfo().getMeteorVersion());
		
		MeteorDataRequest request = createRequest();
		StringWriter marshalledRequest = new StringWriter();
		request.marshal(marshalledRequest);
		
		JaxWsProxyFactoryBean dataClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("dataClientProxyFactory");
		dataClientProxyFactory.setAddress(dataProvider.getRegistryInfo().getUrl());

		DataProviderService dataService = (DataProviderService) dataClientProxyFactory.create();
		String responseXml = dataService.queryDataForBorrower(marshalledRequest.toString());
		
		MeteorRsMsg response = MeteorRsMsg.unmarshal(new StringReader(responseXml));
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
