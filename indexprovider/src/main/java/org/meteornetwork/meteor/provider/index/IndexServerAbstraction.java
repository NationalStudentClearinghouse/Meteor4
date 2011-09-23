package org.meteornetwork.meteor.provider.index;

/**
 * Interface to integrate with your existing backend systems.
 * 
 */
public interface IndexServerAbstraction {

	/**
	 * Find all data providers that have data for the given SSN and return them
	 * in the meteor index response. Append any messages to the response.
	 * 
	 * @param context
	 *            information about the request, including the requesting access
	 *            provider and security token
	 * @param ssn
	 *            the SSN returned data providers will have information for
	 * @return the meteor index response embedded in a convenience wrapper
	 */
	MeteorIndexResponseWrapper getDataProviders(MeteorContext context, String ssn);
}
