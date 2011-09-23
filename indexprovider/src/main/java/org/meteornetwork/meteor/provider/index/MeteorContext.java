package org.meteornetwork.meteor.provider.index;

import org.meteornetwork.meteor.common.xml.indexrequest.AccessProvider;
import org.meteornetwork.meteor.saml.SecurityToken;

/**
 * This object is passed into the DataServerAbstraction and the
 * IndexServerAbstraction methods where each provider will write custom logic.
 * This will allow the providers to get all of the information they want about
 * who made the request, who signed the assertion and just about everything else
 * available
 * 
 * @author jlazos
 * 
 */
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
