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
