package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface IndexProviderService {

	@WebMethod(operationName = "FindDataProvidersForBorrower")
	@WebResult(name = "DataProvidersList")
	String findDataProvidersForBorrower(@WebParam(name = "RequestXml") String requestXML);
}
