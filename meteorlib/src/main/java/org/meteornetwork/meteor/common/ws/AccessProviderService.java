package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface AccessProviderService {

	@WebMethod(operationName = "findDataForBorrower")
	@WebResult(name = "DataForBorrower")
	String findDataForBorrower(@WebParam(name = "requestXML") String requestXML);
}
