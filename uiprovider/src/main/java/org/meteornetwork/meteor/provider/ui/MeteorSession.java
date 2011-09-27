package org.meteornetwork.meteor.provider.ui;

import org.meteornetwork.meteor.saml.SecurityToken;

public class MeteorSession {

	private SecurityToken token;
	private String ssn;
	private String responseXml;

	public SecurityToken getToken() {
		return token;
	}

	public void setToken(SecurityToken token) {
		this.token = token;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getResponseXml() {
		return responseXml;
	}

	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}
}
