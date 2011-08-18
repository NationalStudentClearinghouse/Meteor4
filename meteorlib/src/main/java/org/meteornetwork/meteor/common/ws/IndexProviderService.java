package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface IndexProviderService {

	@WebMethod(operationName = "findDataProvidersForBorrower")
	@WebResult(name = "DataProvidersList")
	String findDataProvidersForBorrower(String requestXML);
}
