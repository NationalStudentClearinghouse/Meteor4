package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "urn:HPCServer")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface DataProviderHPCService {

	/**
	 * HPC borrower data request for backwards compatibility with Meteor 3.3.4
	 * 
	 * @param rawHPCMessage
	 * @return
	 */
	@WebMethod(operationName = "SubmitHPC")
	@WebResult(name = "return", partName = "return")
	String submitHPC(@WebParam(name = "rawHPCMessage") String rawHPCMessage);
}
