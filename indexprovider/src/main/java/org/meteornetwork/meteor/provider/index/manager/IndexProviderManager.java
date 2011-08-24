package org.meteornetwork.meteor.provider.index.manager;

import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.xml.indexrequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class IndexProviderManager {

	private static final Log LOG = LogFactory.getLog(IndexProviderManager.class);
	
	private ClassPathResource indexResponseSample;
	
	public MeteorIndexResponse findDataProvidersForBorrower(AccessProvider accessProvider, String ssn) {
		
		LOG.debug("Received request from AP " + accessProvider.getMeteorInstitutionIdentifier() + " for SSN " + ssn);
		
		// TODO: implement index server abstraction
		MeteorIndexResponse indexResponse;
		try {
			indexResponse = MeteorIndexResponse.unmarshal(new InputStreamReader(indexResponseSample.getInputStream()));
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
		
		return indexResponse;
	}

	public ClassPathResource getIndexResponseSample() {
		return indexResponseSample;
	}

	@Autowired
	@Qualifier("IndexResponseSample")
	public void setIndexResponseSample(ClassPathResource indexResponseSample) {
		this.indexResponseSample = indexResponseSample;
	}
}
