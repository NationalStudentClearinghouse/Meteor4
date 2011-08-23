package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface DataProviderService {

	/**
	 * Query data on borrower based on the borrower's SSN
	 * 
	 * @param requestXML
	 * @return
	 */
	@WebMethod(operationName = "QueryDataForBorrower")
	@WebResult(name = "QueryDataForBorrowerResponse")
	String queryDataForBorrower(@WebParam(name = "requestXML") String requestXML);
}
