package org.meteornetwork.meteor.provider.access.manager;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.provider.access.ResponseDataWrapper;
import org.meteornetwork.meteor.provider.access.service.DataQueryService;
import org.meteornetwork.meteor.provider.access.service.IndexQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessProviderManager {

	private static final Log LOG = LogFactory.getLog(AccessProviderManager.class);

	private IndexQueryService indexQueryService;
	private DataQueryService dataQueryService;

	/**
	 * Queries the meteor network for data on the provided ssn
	 * 
	 * @param ssn
	 *            the ssn of the borrower to query for
	 * @return response XML string (instance of Meteor Schema)
	 */
	public String queryMeteor(String ssn) {
		ResponseDataWrapper responseData = new ResponseDataWrapper(new MeteorRsMsg());

		Set<DataProvider> dataProviders = indexQueryService.getDataProviders(ssn, responseData);
		List<MeteorRsMsg> dataProviderResponses = dataQueryService.getData(dataProviders, ssn);

		// TODO: authentication bump process
		// TODO: perform business logic on dataProviderResponses

		responseData.addAllDataProviderInfo(dataProviderResponses);

		return marshalResponseData(responseData.getResponseData());
	}

	private String marshalResponseData(MeteorRsMsg responseData) {
		StringWriter marshalledResponse = new StringWriter();
		try {
			responseData.marshal(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not marshal response data", e);
			return null;
		}

		return marshalledResponse.toString();
	}

	public IndexQueryService getIndexQueryService() {
		return indexQueryService;
	}

	@Autowired
	public void setIndexQueryService(IndexQueryService indexQueryService) {
		this.indexQueryService = indexQueryService;
	}

	public DataQueryService getDataQueryService() {
		return dataQueryService;
	}

	@Autowired
	public void setDataQueryService(DataQueryService dataQueryService) {
		this.dataQueryService = dataQueryService;
	}

}
