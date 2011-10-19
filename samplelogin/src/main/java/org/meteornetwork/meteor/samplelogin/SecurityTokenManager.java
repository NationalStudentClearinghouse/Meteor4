package org.meteornetwork.meteor.samplelogin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

public class SecurityTokenManager {

	private static SecurityTokenManager instance = new SecurityTokenManager();

	private Map<UUID, String> tokenMap;

	private SecurityTokenManager() {
		tokenMap = new HashMap<UUID, String>();
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
