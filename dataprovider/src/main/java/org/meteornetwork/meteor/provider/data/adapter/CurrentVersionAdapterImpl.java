package org.meteornetwork.meteor.provider.data.adapter;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.util.LoggingUtil;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CurrentVersionAdapterImpl implements VersionAdapter {

	private static final Log LOG = LogFactory.getLog(CurrentVersionAdapterImpl.class);

	private String requestXml;
	private String responseXml;

	@Override
	public RequestWrapper getRequest() {
		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(requestXml));
		} catch (Exception e) {
			LoggingUtil.logError("Could not parse meteor data request", e, LOG);
			return null;
		}

		RequestWrapper request = new RequestWrapper();
		request.setAccessProvider(meteorDataRequest.getAccessProvider());
		request.setSsn(meteorDataRequest.getSSN());
		return request;
	}

	@Override
	public void setResponse(ResponseWrapper response) {
		if (response == null) {
			responseXml = null;
			return;
		}
		
		StringWriter marshalledResponse = new StringWriter();
		try {
			response.getResponse().marshal(marshalledResponse);
		} catch (Exception e) {
			LoggingUtil.logError("Could not marshal meteor response", e, LOG);
			return;
		}
		
		responseXml = marshalledResponse.toString();
	}

	public String getRequestXml() {
		return requestXml;
	}

	/**
	 * @param meteorDataRequest
	 *            the request to load into the RequestWrapper
	 */
	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	/**
	 * @return meteor response as xml string
	 */
	public String getResponseXml() {
		return responseXml;
	}

	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}

}
