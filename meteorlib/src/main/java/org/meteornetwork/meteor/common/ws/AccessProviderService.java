package org.meteornetwork.meteor.common.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.meteornetwork.meteor.saml.TokenAttributes;

@WebService
public interface AccessProviderService {

	@WebMethod(operationName = "findDataForBorrower")
	@WebResult(name = "DataForBorrower")
	String findDataForBorrower(@WebParam(name = "ssn") String ssn, @WebParam(name = "tokenAttrs") TokenAttributes meteorAttributes);

	/**
	 * Returns all meteor data, best source meteor data, and a map of best
	 * source awards to duplicates. Used for the consolidated view.
	 * 
	 * @param ssn
	 *            the ssn to query the meteor network with
	 * @param meteorAttributes
	 *            user data
	 * @param resultBestSource
	 *            serialized MeteorRsMsg with awards filtered by best source
	 *            logic
	 * @param resultAll
	 *            serialized MeteorRsMsg with all awards returned by the data
	 *            providers
	 * @param duplicateAwardsMap
	 *            maps APSUniqueAwardIDs of best source awards to the
	 *            APSUniqueAwardIDs of their duplicates
	 */
	@WebMethod(operationName = "findDataForBorrowerWithConsolidated")
	void findDataForBorrowerWithConsolidated(@WebParam(name = "ssn") String ssn, @WebParam(name = "tokenAttrs") TokenAttributes meteorAttributes, @WebParam(name = "resultBestSource", mode = WebParam.Mode.OUT) Holder<String> resultBestSource, @WebParam(name = "resultAll", mode = WebParam.Mode.OUT) Holder<String> resultAll, @WebParam(name = "duplicateAwardsMap", mode = WebParam.Mode.OUT) Holder<byte[]> duplicateAwardsMap);
}
