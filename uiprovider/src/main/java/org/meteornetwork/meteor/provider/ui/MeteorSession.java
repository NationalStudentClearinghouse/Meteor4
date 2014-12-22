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
package org.meteornetwork.meteor.provider.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import org.meteornetwork.meteor.saml.SecurityToken;

public class MeteorSession implements Serializable{

	private static final long serialVersionUID = 1L;
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
