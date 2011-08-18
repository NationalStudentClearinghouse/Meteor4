package org.meteornetwork.meteor.provider.access;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class DataServiceManager implements ApplicationContextAware {

	private ApplicationContext appContext;
	
	public String getData() {
		String address = "http://localhost:8080/dataprovider/services/DataProviderService";
		
		JaxWsProxyFactoryBean dataClientProxyFactory = (JaxWsProxyFactoryBean) appContext.getBean("dataClientProxyFactory");
		dataClientProxyFactory.setAddress(address);
		
		DataProviderService dataService = (DataProviderService) dataClientProxyFactory.create();
		return dataService.queryDataForBorrower("Hai.");
	}

	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		this.appContext = appContext;		
	}
}
