package org.meteornetwork.meteor.provider.data;

/**
 * All modules that implement this interface are excluded from the license that
 * Meteor is distributed under.
 * 
 * To be a Data Provider, create a class that implements this interface. The
 * implementations of this class will tie to the back end systems to retrieve
 * data
 * 
 * @since Meteor 1.0
 */
public interface DataServerAbstraction {

	/**
	 * Implement this method to provide data for Meteor.
	 * 
	 * @param context
	 *            MeteorContext object containing all of the information about
	 *            the request
	 * @param ssn
	 *            SSN to query
	 * @return the meteor data response embedded in a convenience wrapper
	 */
	MeteorDataResponseWrapper getData(MeteorContext context, String ssn);
}
