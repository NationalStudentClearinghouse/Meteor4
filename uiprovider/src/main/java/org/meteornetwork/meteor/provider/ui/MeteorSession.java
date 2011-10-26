package org.meteornetwork.meteor.provider.ui;

import java.util.ArrayList;
import java.util.TreeMap;

import org.meteornetwork.meteor.saml.SecurityToken;

public class MeteorSession {

	private SecurityToken token;
	private String ssn;
	private String inquiryRole;
	private String responseXml;
	private String responseXmlUnfiltered;
	private TreeMap<Integer, ArrayList<Integer>> duplicateAwardIds;

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

	public String getResponseXmlUnfiltered() {
		return responseXmlUnfiltered;
	}

	public void setResponseXmlUnfiltered(String responseXmlUnfiltered) {
		this.responseXmlUnfiltered = responseXmlUnfiltered;
	}

	public TreeMap<Integer, ArrayList<Integer>> getDuplicateAwardIds() {
		return duplicateAwardIds;
	}

	public void setDuplicateAwardIds(TreeMap<Integer, ArrayList<Integer>> duplicateAwardIds) {
		this.duplicateAwardIds = duplicateAwardIds;
	}

	/**
	 * nullifies all parameters on this session object
	 */
	public void clear() {
		token = null;
		ssn = null;
		responseXml = null;
		responseXmlUnfiltered = null;
		duplicateAwardIds = null;
		inquiryRole = null;
	}

	public String getInquiryRole() {
		return inquiryRole;
	}

	public void setInquiryRole(String inquiryRole) {
		this.inquiryRole = inquiryRole;
	}
}
