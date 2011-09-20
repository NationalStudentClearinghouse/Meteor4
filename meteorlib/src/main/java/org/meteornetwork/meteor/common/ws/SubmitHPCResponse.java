package org.meteornetwork.meteor.common.ws;

import java.io.Serializable;

public class SubmitHPCResponse implements Serializable {

	private static final long serialVersionUID = 7409528203878968269L;
	
	private String returnXml;

	public SubmitHPCResponse() {
	}

	public SubmitHPCResponse(String returnXml) {
		this.returnXml = returnXml;
	}

	public String getReturnXml() {
		return returnXml;
	}

	public void setReturnXml(String returnXml) {
		this.returnXml = returnXml;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((returnXml == null) ? 0 : returnXml.hashCode());
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
		SubmitHPCResponse other = (SubmitHPCResponse) obj;
		if (returnXml == null) {
			if (other.returnXml != null)
				return false;
		} else if (!returnXml.equals(other.returnXml))
			return false;
		return true;
	}

}
