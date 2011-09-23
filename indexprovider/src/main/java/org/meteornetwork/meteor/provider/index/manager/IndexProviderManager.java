package org.meteornetwork.meteor.provider.index.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.xml.indexrequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.meteornetwork.meteor.provider.index.IndexServerAbstraction;
import org.meteornetwork.meteor.provider.index.MeteorContext;
import org.meteornetwork.meteor.provider.index.MeteorIndexResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//@Service
public class IndexProviderManager {

	private static final Log LOG = LogFactory.getLog(IndexProviderManager.class);

	private IndexServerAbstraction indexServer;

	public MeteorIndexResponse findDataProvidersForBorrower(AccessProvider accessProvider, String ssn) {

		LOG.debug("Received request from AP " + accessProvider.getMeteorInstitutionIdentifier() + " for SSN " + ssn);

		MeteorContext context = new MeteorContext();
		context.setAccessProvider(accessProvider);
		context.setSecurityToken(getRequestInfo().getSecurityToken());
		MeteorIndexResponseWrapper response = indexServer.getDataProviders(context, ssn);

		return response.getResponse();
	}

	public RequestInfo getRequestInfo() {
		// overridden by spring method injection
		return null;
	}

	public IndexServerAbstraction getIndexServer() {
		return indexServer;
	}

	@Autowired
	@Qualifier("IndexServerAbstractionImpl")
	public void setIndexServer(IndexServerAbstraction indexServer) {
		this.indexServer = indexServer;
	}

}
