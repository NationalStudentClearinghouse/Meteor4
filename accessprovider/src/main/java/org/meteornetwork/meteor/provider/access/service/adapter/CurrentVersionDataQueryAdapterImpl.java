package org.meteornetwork.meteor.provider.access.service.adapter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("currentVersionDataQueryAdapterImpl")
@Scope("prototype")
public class CurrentVersionDataQueryAdapterImpl implements DataQueryAdapter, ApplicationContextAware {

	private DataProvider dataProvider;
	private AccessProvider accessProvider;
	private String ssn;

	private Properties authenticationProperties;
	private Properties meteorProperties;

	private ApplicationContext applicationContext;

	@Override
	public MeteorRsMsg call() throws Exception {
		MeteorDataRequest request = createRequest();
		StringWriter marshalledRequest = new StringWriter();
		request.marshal(marshalledRequest);
		
		JaxWsProxyFactoryBean dataClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("dataClientProxyFactory");
		dataClientProxyFactory.setAddress(dataProvider.getEntityURL());

		DataProviderService dataService = (DataProviderService) dataClientProxyFactory.create();
		String responseXml = dataService.queryDataForBorrower(marshalledRequest.toString());
		
		MeteorRsMsg response = MeteorRsMsg.unmarshal(new StringReader(responseXml));
		return response;
	}

	private MeteorDataRequest createRequest() {
		MeteorDataRequest request = new MeteorDataRequest();
		request.setAccessProvider(accessProvider);
		request.setMeteorVersion(meteorProperties.getProperty("meteor.version"));
		request.setSSN(ssn);
		return request;
	}

	@Override
	public DataProvider getDataProvider() {
		return dataProvider;
	}

	@Override
	public void setDataProvider(DataProvider dataProvider) {
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
	public AccessProvider getAccessProvider() {
		return accessProvider;
	}

	@Override
	public void setAccessProvider(AccessProvider accessProvider) {
		this.accessProvider = accessProvider;
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

}
