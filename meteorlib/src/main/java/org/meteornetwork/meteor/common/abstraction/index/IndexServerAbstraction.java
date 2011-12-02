/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.common.abstraction.index;

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
