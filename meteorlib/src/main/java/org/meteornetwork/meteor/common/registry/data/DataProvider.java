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
package org.meteornetwork.meteor.common.registry.data;

/**
 * Contains information about a Data Provider returned from the Meteor Registry
 * 
 * @author jlazos
 * 
 */
public class DataProvider {

	private String institutionIdentifier;
	private String description;
	private String url;
	private String meteorVersion;

	public String getInstitutionIdentifier() {
		return institutionIdentifier;
	}

	public void setInstitutionIdentifier(String institutionIdentifier) {
		this.institutionIdentifier = institutionIdentifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMeteorVersion() {
		return meteorVersion;
	}

	public void setMeteorVersion(String meteorVersion) {
		this.meteorVersion = meteorVersion;
	}

}
