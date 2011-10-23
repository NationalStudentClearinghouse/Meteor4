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
