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
package org.meteornetwork.meteor.provider.access.ws;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;

import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.annotation.Resource;

import org.meteornetwork.meteor.business.BestSourceAggregator;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.ws.AccessProviderService;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.provider.access.ResponseDataWrapper;
import org.meteornetwork.meteor.provider.access.manager.AccessProviderManager;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.TokenAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.SerializationUtils;

@WebService(endpointInterface = "org.meteornetwork.meteor.common.ws.AccessProviderService", serviceName = "AccessProviderService")
public class AccessProviderServiceImpl implements AccessProviderService {

	private static final Logger LOG = LoggerFactory.getLogger(AccessProviderServiceImpl.class);

	private AccessProviderManager accessProviderManager;

	private Properties authenticationProperties;

	@Override
	public String findDataForBorrower(String ssn, TokenAttributes meteorAttributes) {
		LOG.debug("AP received request for best source data for ssn: " + ssn);

		RequestInfo requestInfo = getRequestInfo();
		requestInfo.setMeteorInstitutionIdentifier(authenticationProperties.getProperty("authentication.identifier"));
		requestInfo.setSecurityToken(new SecurityTokenImpl());
		requestInfo.getSecurityToken().setMeteorAttributes(meteorAttributes);

		ResponseDataWrapper response = accessProviderManager.queryMeteor(ssn);
		try {
			String marshalledResponse = marshalResponseData(response.getResponseDataBestSource());
			if (marshalledResponse == null) {
				return null;
			}
			LOG.debug("Returning data for ssn: " + ssn);
			return accessProviderManager.maskSSNs(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not mask SSNs", e);
			return null;
		}
	}

	@Override
	public void findDataForBorrowerWithConsolidated(String ssn, TokenAttributes meteorAttributes, Holder<String> resultBestSource, Holder<String> resultAll, Holder<byte[]> duplicateAwardsMap) {
		LOG.debug("AP received request for best source data for ssn: " + ssn);

		RequestInfo requestInfo = getRequestInfo();
		requestInfo.setMeteorInstitutionIdentifier(authenticationProperties.getProperty("authentication.identifier"));
		requestInfo.setSecurityToken(new SecurityTokenImpl());
		requestInfo.getSecurityToken().setMeteorAttributes(meteorAttributes);

		ResponseDataWrapper response = accessProviderManager.queryMeteor(ssn);
		try {
			String marshalledResponse = marshalResponseData(response.getUnfilteredResponseData());
			resultAll.value = marshalledResponse == null ? null : accessProviderManager.maskSSNs(marshalledResponse);
			marshalledResponse = marshalResponseData(response.getResponseDataBestSource());
			resultBestSource.value = marshalledResponse == null ? null : accessProviderManager.maskSSNs(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not mask SSNs", e);
			return;
		}

		/*
		 * map best source award ids to duplicate award ids
		 */
		TreeMap<Integer, ArrayList<Integer>> duplicateAwardsMapObj = new TreeMap<Integer, ArrayList<Integer>>();
		BestSourceAggregator aggregator = response.getBestSourceAggregator();
		for (Award award : aggregator.getBest()) {
			ArrayList<Integer> duplicateAwardIds = new ArrayList<Integer>();
			for (Award dupAward : aggregator.getDuplicates(award.getAPSUniqueAwardID())) {
				duplicateAwardIds.add(dupAward.getAPSUniqueAwardID());
			}

			duplicateAwardsMapObj.put(award.getAPSUniqueAwardID(), duplicateAwardIds);
		}

		duplicateAwardsMap.value = duplicateAwardsMapObj == null || duplicateAwardsMapObj.isEmpty() ? null : SerializationUtils.serialize(duplicateAwardsMapObj);

		LOG.debug("Returning data for ssn: " + ssn);
	}

	private String marshalResponseData(MeteorRsMsg responseData) {
		StringWriter marshalledResponse = new StringWriter();
		try {
			responseData.marshal(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not marshal response data", e);
			return null;
		}

		return marshalledResponse.toString();
	}

	public AccessProviderManager getAccessProviderManager() {
		return accessProviderManager;
	}

	@Autowired
	public void setAccessProviderManager(AccessProviderManager accessProviderManager) {
		this.accessProviderManager = accessProviderManager;
	}

	public RequestInfo getRequestInfo() {
		// overridden by spring method injection
		return null;
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired()
	@Qualifier("authenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		
		this.authenticationProperties = authenticationProperties;
	}

}
