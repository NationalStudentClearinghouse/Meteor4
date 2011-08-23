package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.provider.data.adapter.Version334AdapterImpl;
import org.meteornetwork.meteor.provider.data.manager.DataProviderManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("dataProviderHPCServiceImpl")
@WebService(endpointInterface = "org.meteornetwork.meteor.provider.data.ws.DataProviderHPCService", serviceName = "DataProviderHPCService")
public class DataProviderHPCServiceImpl implements DataProviderHPCService, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(DataProviderHPCServiceImpl.class);

	private ApplicationContext applicationContext;
	private DataProviderManager dataManager;

	@Override
	public String submitHPC(String rawHPCMessage) {
		LOG.debug("DP received HPC request: " + rawHPCMessage);

		Version334AdapterImpl adapter = (Version334AdapterImpl) applicationContext.getBean(Version334AdapterImpl.class);
		adapter.setRawHPCMessage(rawHPCMessage.trim());
		dataManager.queryDataForBorrower(adapter);

		return adapter.getResponseHPCMessage();
	}

	public DataProviderManager getDataManager() {
		return dataManager;
	}

	@Autowired
	public void setDataManager(DataProviderManager dataManager) {
		this.dataManager = dataManager;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
