package org.meteornetwork.meteor.provider.index.ws;

import java.io.StringReader;
import java.io.StringWriter;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.ws.IndexProviderService;
import org.meteornetwork.meteor.common.xml.indexrequest.MeteorIndexRequest;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.meteornetwork.meteor.provider.index.manager.IndexProviderManager;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.IndexProviderService", serviceName = "IndexProviderService")
public class IndexProviderServiceImpl implements IndexProviderService {

	private static final Log LOG = LogFactory.getLog(IndexProviderServiceImpl.class);

	private IndexProviderManager manager;
	@Override
	public String findDataProvidersForBorrower(String requestXML) {
		LOG.debug("IP received requestXML: " + requestXML);
		
		MeteorIndexRequest indexRequest;
		try {
			indexRequest = MeteorIndexRequest.unmarshal(new StringReader(requestXML));
		} catch (Exception e) {
			LOG.error("Could not unmarshal index request", e);
			// TODO implement error handling across web services
			return null;
		}
		
		MeteorIndexResponse response = manager.findDataProvidersForBorrower(indexRequest.getAccessProvider(), indexRequest.getSSN());
		
		StringWriter marshalledResponseWriter = new StringWriter();
		try {
			response.marshal(marshalledResponseWriter);
		} catch (Exception e) {
			LOG.error("Could not marshal index response", e);
			return null;
		}
		
		return marshalledResponseWriter.toString();
	}
	public IndexProviderManager getManager() {
		return manager;
	}
	
	@Autowired
	public void setManager(IndexProviderManager manager) {
		this.manager = manager;
	}
}
