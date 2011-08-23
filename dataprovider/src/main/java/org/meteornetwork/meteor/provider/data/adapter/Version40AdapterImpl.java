package org.meteornetwork.meteor.provider.data.adapter;

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.AccessProvider;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Version40AdapterImpl implements TranslationAdapter {

	private static final Log LOG = LogFactory.getLog(Version40AdapterImpl.class);

	private Exception processingException;

	private String requestXml;

	@Override
	public RequestWrapper getRequest() {
		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(requestXml));
		} catch (Exception e) {
			LOG.error("Could not parse meteor data request: " + e.getMessage());
			LOG.debug("Could not parse meteor data request", e);
			processingException = e;
			return null;
		}
		
		RequestWrapper request = new RequestWrapper();
		request.setAccessProvider(new AccessProvider(meteorDataRequest.getAccessProvider()));
		request.setSsn(meteorDataRequest.getSSN());
		return request;
	}

	@Override
	public void setResponse(ResponseWrapper response) {
		// TODO Auto-generated method stub
	}

	@Override
	public Exception getProcessingException() {
		return processingException;
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

}
