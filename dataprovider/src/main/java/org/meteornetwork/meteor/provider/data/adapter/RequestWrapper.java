package org.meteornetwork.meteor.provider.data.adapter;

import org.meteornetwork.meteor.common.xml.AccessProvider;

public class RequestWrapper {

	private AccessProvider accessProvider;
	private String ssn;

	public AccessProvider getAccessProvider() {
		return accessProvider;
	}

	public void setAccessProvider(AccessProvider accessProvider) {
		this.accessProvider = accessProvider;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

}
