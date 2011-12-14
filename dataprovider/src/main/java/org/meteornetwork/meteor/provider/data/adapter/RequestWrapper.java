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
package org.meteornetwork.meteor.provider.data.adapter;

import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;

public class RequestWrapper {

	private AccessProvider accessProvider;
	private String ssn;
	private Version meteorVersion;

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

	public Version getMeteorVersion() {
		return meteorVersion;
	}

	public void setMeteorVersion(Version meteorVersion) {
		this.meteorVersion = meteorVersion;
	}

}
