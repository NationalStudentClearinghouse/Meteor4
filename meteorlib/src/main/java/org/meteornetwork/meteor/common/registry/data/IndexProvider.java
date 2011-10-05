package org.meteornetwork.meteor.common.registry.data;

/**
 * Contains information about an Index Provider returned from the Meteor
 * Registry
 * 
 * @author jlazos
 * 
 */
public class IndexProvider {

	private String institutionIdentifier;
	private String url;
	private String failoverUrl;

	public String getInstitutionIdentifier() {
		return institutionIdentifier;
	}

	public void setInstitutionIdentifier(String institutionIdentifier) {
		this.institutionIdentifier = institutionIdentifier;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFailoverUrl() {
		return failoverUrl;
	}

	public void setFailoverUrl(String failoverUrl) {
		this.failoverUrl = failoverUrl;
	}

}
