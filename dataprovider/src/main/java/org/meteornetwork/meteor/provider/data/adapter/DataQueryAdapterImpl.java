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

import java.io.StringReader;
import java.io.StringWriter;

import org.meteornetwork.meteor.common.abstraction.data.MeteorDataResponseWrapper;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("DataQueryAdapterImpl")
@Scope("prototype")
public class DataQueryAdapterImpl implements DataQueryAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(DataQueryAdapterImpl.class);

	private String requestXml;
	private String responseXml;

	private RequestInfo requestInfo;

	@Override
	public RequestWrapper getRequest() throws DataQueryAdapterException {
		if (!requestInfo.getSecurityToken().validateConditions()) {
			LOG.debug("SAML token expired");
			throw new DataQueryAdapterException(MeteorMessage.SECURITY_TOKEN_EXPIRED);
		}

		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(requestXml));
		} catch (Exception e) {
			LOG.error("Could not parse meteor data request", e);
			throw new DataQueryAdapterException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE);
		}

		RequestWrapper request = new RequestWrapper();
		request.setMeteorVersion(new Version(meteorDataRequest.getMeteorVersion()));
		request.setAccessProvider(meteorDataRequest.getAccessProvider());
		request.setSsn(meteorDataRequest.getSSN());
		return request;
	}

	@Override
	public void setResponse(MeteorDataResponseWrapper response) {
		if (response == null) {
			responseXml = null;
			return;
		}

		StringWriter marshalledResponse = new StringWriter();
		try {
			response.getResponse().marshal(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not marshal meteor response", e);
			return;
		}

		responseXml = marshalledResponse.toString();
	}

	public String getRequestXml() {
		return requestXml;
	}

	/**
	 * @param meteorDataRequest
	 *            the request to load into the RequestWrapper
	 */
	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	/**
	 * @return meteor response as xml string
	 */
	public String getResponseXml() {
		return responseXml;
	}

	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	@Autowired
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

}
