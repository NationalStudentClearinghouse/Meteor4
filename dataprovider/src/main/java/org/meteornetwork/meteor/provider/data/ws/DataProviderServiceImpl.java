package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.DataProviderService;
import org.meteornetwork.meteor.provider.data.adapter.CurrentVersionAdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("dataProviderServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.DataProviderService", serviceName = "DataProviderService")
public class DataProviderServiceImpl implements DataProviderService, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(DataProviderServiceImpl.class);

	private ApplicationContext applicationContext;
	private DataProviderManager dataManager;

	@Override
	public String queryDataForBorrower(String requestXml) {
		LOG.debug("DP received request: " + requestXml);

		CurrentVersionAdapterImpl adapter = (CurrentVersionAdapterImpl) applicationContext.getBean(CurrentVersionAdapterImpl.class);
		adapter.setRequestXml(requestXml);
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseXml();
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}
}
