package org.meteornetwork.meteor.common.security;

import org.meteornetwork.meteor.saml.SecurityToken;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("requestInfo")
@Scope("request")
public class RequestInfo {

	private String meteorInstitutionIdentifier;
	private SecurityToken securityToken;

	public String getMeteorInstitutionIdentifier() {
		return meteorInstitutionIdentifier;
	}

	public void setMeteorInstitutionIdentifier(String meteorInstitutionIdentifier) {
		this.meteorInstitutionIdentifier = meteorInstitutionIdentifier;
	}

	public SecurityToken getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(SecurityToken securityToken) {
		this.securityToken = securityToken;
	}

}
