package org.meteornetwork.meteor.provider.data.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace="urn:HPCServer")
public interface DataProviderHPCService {

	/**
	 * HPC borrower data request for backwards compatibility with Meteor 3.3.4
	 * 
	 * @param rawHPCMessage
	 * @return
	 */
	@WebMethod(operationName = "SubmitHPC")
	@WebResult(name = "SubmitHPCResponse")
	String submitHPC(@WebParam(name = "rawHPCMessage") String rawHPCMessage);
}
