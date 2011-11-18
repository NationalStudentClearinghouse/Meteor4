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
package org.meteornetwork.meteor.provider.ui.token;

import javax.servlet.http.HttpServletRequest;

import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

/**
 * Provides a Meteor Security Token for the authenticated user. Implementations
 * create security tokens based on whatever login system is employed (Sample
 * logon, Shibboleth, OpenAM etc)
 * 
 * @author jlazos
 * 
 */
public interface TokenProvider {

	/**
	 * Produces a Meteor Security Token for the authenticated user.
	 * 
	 * @return the token representing the authenticated user
	 * @throws SecurityTokenException
	 *             thrown when security token cant be returned for some reason.
	 *             The cause code parameter in the constructor of
	 *             SecurityTokenException affects the message returned to the
	 *             user by the UI provider. For example, if the cause code is
	 *             ACCESS_DENIED (default), the user is presented with the
	 *             Access Denied page. If the cause code is SESSION_EXPIRED, on
	 *             the other hand, the user is presented with the more innocent
	 *             "Please login again" page. Consult the SecurityTokenException
	 *             javadoc for further details.
	 */
	SecurityToken getSecurityToken(HttpServletRequest request) throws SecurityTokenException;
}
