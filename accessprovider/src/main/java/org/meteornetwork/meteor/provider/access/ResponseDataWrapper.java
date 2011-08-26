package org.meteornetwork.meteor.provider.access;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;

/**
 * Adds messages and other data to a single MeteorRsMsg object
 * 
 * @author jlazos
 */
public class ResponseDataWrapper {

	private static final Log LOG = LogFactory.getLog(ResponseDataWrapper.class);
	
	private MeteorRsMsg responseData;

	public ResponseDataWrapper(MeteorRsMsg responseData) {
		this.responseData = responseData;
	}

	public void addDataProviderInfo(MeteorRsMsg dataProviderResponse) {
		for (MeteorDataProviderInfo info : dataProviderResponse.getMeteorDataProviderInfo()) {
			responseData.addMeteorDataProviderInfo(info);
		}
	}

	public void addAllDataProviderInfo(Iterable<MeteorRsMsg> dataProviderResponses) {
		for (MeteorRsMsg response : dataProviderResponses) {
			addDataProviderInfo(response);
		}
	}

	public void addIndexProviderMessage(Message message) {
		// TODO: implement this method
		LOG.debug("Index provider wants to add message: " + message.getRsMsg());
	}

	public MeteorRsMsg getResponseData() {
		return responseData;
	}

}
