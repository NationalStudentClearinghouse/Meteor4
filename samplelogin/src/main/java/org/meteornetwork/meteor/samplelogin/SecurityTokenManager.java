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
package org.meteornetwork.meteor.samplelogin;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

public class SecurityTokenManager {

	private static SecurityTokenManager instance = new SecurityTokenManager();

	private class LimitedLinkedHashMap extends LinkedHashMap<UUID, String> {

		private static final int MAX_SIZE = 100;
		/**
		 * 
		 */
		private static final long serialVersionUID = -2586837279594436696L;

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<UUID, String> eldest) {
			return size() > MAX_SIZE;
		}
	}

	private LimitedLinkedHashMap tokenMap;

	private SecurityTokenManager() {
		tokenMap = new LimitedLinkedHashMap();
	}

	public static SecurityTokenManager getInstance() {
		return instance;
	}

	public UUID putToken(SecurityToken token) throws SecurityTokenException {
		UUID uuid = UUID.randomUUID();
		tokenMap.put(uuid, token.toXMLString());
		return uuid;
	}

	public String getToken(UUID key) {
		return tokenMap.remove(key);
	}
}
