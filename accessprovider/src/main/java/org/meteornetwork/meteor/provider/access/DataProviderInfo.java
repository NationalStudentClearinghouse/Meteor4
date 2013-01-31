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
package org.meteornetwork.meteor.provider.access;

/**
 * Combines info from registry and index provider about a particular data
 * provider
 * 
 * @author jlazos
 * 
 */
public class DataProviderInfo {

	private final String meteorInstitutionIdentifier;
	private org.meteornetwork.meteor.common.xml.indexresponse.DataProvider indexProviderInfo;
	private org.meteornetwork.meteor.common.registry.data.DataProvider registryInfo;

	public DataProviderInfo(String meteorInstitutionIdentifier) {
		this.meteorInstitutionIdentifier = meteorInstitutionIdentifier;
	}

	public String getMeteorInstitutionIdentifier() {
		return meteorInstitutionIdentifier;
	}

	public org.meteornetwork.meteor.common.xml.indexresponse.DataProvider getIndexProviderInfo() {
		return indexProviderInfo;
	}

	public void setIndexProviderInfo(org.meteornetwork.meteor.common.xml.indexresponse.DataProvider indexProviderInfo) {
		this.indexProviderInfo = indexProviderInfo;
	}

	public org.meteornetwork.meteor.common.registry.data.DataProvider getRegistryInfo() {
		return registryInfo;
	}

	public void setRegistryInfo(org.meteornetwork.meteor.common.registry.data.DataProvider registryInfo) {
		this.registryInfo = registryInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((meteorInstitutionIdentifier == null) ? 0 : meteorInstitutionIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataProviderInfo other = (DataProviderInfo) obj;
		if (meteorInstitutionIdentifier == null) {
			if (other.meteorInstitutionIdentifier != null)
				return false;
		} else if (!meteorInstitutionIdentifier.equals(other.meteorInstitutionIdentifier))
			return false;
		return true;
	}
}
