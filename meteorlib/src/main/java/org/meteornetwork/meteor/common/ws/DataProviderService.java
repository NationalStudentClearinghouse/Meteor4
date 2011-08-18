package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface DataProviderService {

	@WebMethod(operationName = "QueryDataForBorrower")
	@WebResult(name = "QueryResults")
	String queryDataForBorrower(@WebParam(name = "requestXML") String requestXML);

}
