package org.meteornetwork.meteor.provider.data;

import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.saml.SecurityToken;

public class MeteorContext {

	private AccessProvider accessProvider;
	private SecurityToken securityToken;

	public AccessProvider getAccessProvider() {
		return accessProvider;
	}

	public void setAccessProvider(AccessProvider accessProvider) {
		this.accessProvider = accessProvider;
	}

	public SecurityToken getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(SecurityToken securityToken) {
		this.securityToken = securityToken;
	}
}
