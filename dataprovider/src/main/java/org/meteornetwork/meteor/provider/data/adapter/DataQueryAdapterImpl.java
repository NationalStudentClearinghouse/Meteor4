package org.meteornetwork.meteor.provider.data.adapter;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.meteornetwork.meteor.provider.data.MeteorDataResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DataQueryAdapterImpl implements DataQueryAdapter {

	private static final Log LOG = LogFactory.getLog(DataQueryAdapterImpl.class);

	private String requestXml;
	private String responseXml;

	private RequestInfo requestInfo;

	@Override
	public RequestWrapper getRequest() throws DataQueryAdapterException {
		if (!requestInfo.getSecurityToken().validateConditions()) {
			LOG.debug("SAML token expired");
			throw new DataQueryAdapterException(MeteorMessage.SECURITY_TOKEN_EXPIRED);
		}

		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(requestXml));
		} catch (Exception e) {
			LOG.error("Could not parse meteor data request", e);
			throw new DataQueryAdapterException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE);
		}

		RequestWrapper request = new RequestWrapper();
		request.setAccessProvider(meteorDataRequest.getAccessProvider());
		request.setSsn(meteorDataRequest.getSSN());
		return request;
	}

	@Override
	public void setResponse(MeteorDataResponseWrapper response) {
		if (response == null) {
			responseXml = null;
			return;
		}

		StringWriter marshalledResponse = new StringWriter();
		try {
			response.getResponse().marshal(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not marshal meteor response", e);
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

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	@Autowired
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

}
