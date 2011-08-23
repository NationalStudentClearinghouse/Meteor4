package org.meteornetwork.meteor.provider.data.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.provider.data.adapter.RequestWrapper;
import org.meteornetwork.meteor.provider.data.adapter.TranslationAdapter;
import org.springframework.stereotype.Service;

/**
 * Initializes DataServerAbstraction and directs requests to it
 * 
 * @author jlazos
 * 
 */
@Service
public class DataProviderManager {

	private static final Log LOG = LogFactory.getLog(DataProviderManager.class);

	/**
	 * Executes query for single SSN
	 * 
	 * @param adapter
	 *            - translation layer object. This method gets request data and
	 *            sets response data using this adapter
	 */
	public void queryDataForBorrower(TranslationAdapter adapter) {
		RequestWrapper request = adapter.getRequest();
		if (request == null) {
			return;
		}

		// TODO: initialize data server abstraction and call it
		LOG.debug("Request received for data on SSN: " + request.getSsn());

		// TODO: set response on adapter
	}
}
