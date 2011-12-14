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
package org.meteornetwork.meteor.provider.data.manager;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.abstraction.data.DataServerAbstraction;
import org.meteornetwork.meteor.common.abstraction.data.MeteorContext;
import org.meteornetwork.meteor.common.abstraction.data.MeteorDataResponseWrapper;
import org.meteornetwork.meteor.common.abstraction.data.MeteorDataResponseWrapper.DataProviderDataParams;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderAwardDetails;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.dataresponse.types.PhoneNumTypeEnum;
import org.meteornetwork.meteor.common.xml.dataresponse.types.RsMsgLevelEnum;
import org.meteornetwork.meteor.provider.data.adapter.DataQueryAdapter;
import org.meteornetwork.meteor.provider.data.adapter.DataQueryAdapterException;
import org.meteornetwork.meteor.provider.data.adapter.RequestWrapper;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes DataServerAbstraction and directs requests to it
 * 
 * @author jlazos
 * 
 */
// @Service
public class DataProviderManager {

	private static final Logger LOG = LoggerFactory.getLogger(DataProviderManager.class);

	private DataServerAbstraction dataServer;
	private Properties dataProviderProperties;
	private Properties meteorProperties;
	private Properties authenticationProperties;

	private RegistryManager registryManager;

	/**
	 * Creates meteor status response
	 * 
	 * @return MeteorRsMsg xml string with status response
	 */
	public String createStatusResponse() {
		MeteorDataResponseWrapper dataResponse;

		RequestInfo requestInfo = getRequestInfo();
		if (requestInfo.getSecurityToken() == null || requestInfo.getSecurityToken().getRole() == null || !Role.HELPDESK.equals(requestInfo.getSecurityToken().getRole())) {
			dataResponse = createResponseWithMessage(MeteorMessage.DATA_SSN_NOTAUTHORIZED, RsMsgLevelEnum.E);
		} else {

			dataResponse = new MeteorDataResponseWrapper();

			DataProviderDataParams params = dataResponse.new DataProviderDataParams();
			params.setName(dataProviderProperties.getProperty("DataProvider.Data.Name"));
			params.setId(dataProviderProperties.getProperty("DataProvider.Data.ID"));
			params.setUrl(dataProviderProperties.getProperty("DataProvider.Data.URL"));
			params.setType(dataProviderProperties.getProperty("DataProvider.Data.Type"));

			params.setDataProviderData();

			String version = meteorProperties.getProperty("meteor.version");
			dataResponse.addMessage("Version " + version == null ? "" : version, RsMsgLevelEnum.I.name());
		}

		StringWriter marshalledResponse = new StringWriter();
		try {
			dataResponse.getResponse().marshal(marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not marshal meteor response", e);
			return null;
		}

		return marshalledResponse.toString();
	}

	/**
	 * Executes query for single SSN
	 * 
	 * @param adapter
	 *            - translation layer object. This method gets request data and
	 *            sets response data using this adapter
	 */
	public void queryDataForBorrower(DataQueryAdapter adapter) {

		/* ****************************************
		 * handle any errors that happened during adapter processing
		 */
		RequestWrapper request = null;
		try {
			request = adapter.getRequest();
		} catch (DataQueryAdapterException e) {
			MeteorDataResponseWrapper response = createResponseWithMessage(e.getMeteorError(), RsMsgLevelEnum.E);
			setDataProviderData(response);
			adapter.setResponse(response);
			return;
		}

		if (request == null) {
			return;
		}

		/* *****************************************
		 * verify info on security token
		 * 
		 * If meteor version is 3.2, then the check should be skipped because
		 * the SAML token does not have valid conditions
		 */
		SecurityToken token = getRequestInfo().getSecurityToken();
		if (!token.validateConditions() && !request.getMeteorVersion().matches("3.2")) {
			LOG.debug("SAML conditions are not valid or expired");
			MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.SECURITY_TOKEN_EXPIRED, RsMsgLevelEnum.E);
			setDataProviderData(response);
			adapter.setResponse(response);
			return;
		}

		String minimumAuthLevelStr = dataProviderProperties.getProperty("accessprovider.minimum.authentication.level");
		if (minimumAuthLevelStr == null) {
			LOG.error("Data Provider not configured correctly -- missing accessprovider.minimum.authentication.level property");
			MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.DATA_NO_MINIMUM_LEVEL, RsMsgLevelEnum.E);
			setDataProviderData(response);
			adapter.setResponse(response);
			return;
		}

		Integer minimumAuthLevel = Integer.valueOf(minimumAuthLevelStr);
		Integer registryMinimumAuthLevel;
		try {
			registryMinimumAuthLevel = registryManager.getAuthenticationLevel(authenticationProperties.getProperty("authentication.identifier"), token.getAuthenticationProcessId(), ProviderType.DATA, token.getRole());
		} catch (RegistryException e) {
			LOG.debug("Could not access the registry", e);
			MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.REGISTRY_NO_CONNECTION, RsMsgLevelEnum.E);
			setDataProviderData(response);
			adapter.setResponse(response);
			return;
		}

		minimumAuthLevel = minimumAuthLevel.compareTo(registryMinimumAuthLevel) > 0 ? minimumAuthLevel : registryMinimumAuthLevel;

		if (minimumAuthLevel.compareTo(token.getLevel()) > 0) {
			LOG.debug("Minimum authentication level not met");
			MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.DATA_INSUFFICIENT_LEVEL, RsMsgLevelEnum.E);
			setDataProviderData(response);
			adapter.setResponse(response);
			return;
		}

		if (Role.BORROWER.equals(token.getRole())) {
			String assertionSsn = token.getSsn();
			if (assertionSsn == null) {
				LOG.debug("Borrower's SAML is missing assertion SSN");
				MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.DATA_NOSSN, RsMsgLevelEnum.E);
				setDataProviderData(response);
				adapter.setResponse(response);
				return;
			}

			if (!assertionSsn.equals(request.getSsn())) {
				LOG.debug("Borrower's SAML Ssn does not match request SSN");
				MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.DATA_SSN_NOTAUTHORIZED, RsMsgLevelEnum.E);
				setDataProviderData(response);
				adapter.setResponse(response);
				return;
			}
		}

		if (Role.HELPDESK.equals(token.getRole())) {
			LOG.debug("Help desk users are not allowed to query for data provider data.");
			MeteorDataResponseWrapper response = createResponseWithMessage(MeteorMessage.DATA_SSN_NOTAUTHORIZED, RsMsgLevelEnum.E);
			setDataProviderData(response);
			adapter.setResponse(response);
			return;
		}

		/* ******************************************
		 * Query data
		 */
		LOG.info("Received request from Access Provider: " + request.getAccessProvider().getMeteorInstitutionIdentifier() + " with the user handle: " + token.getUserHandle() + " and role: " + token.getRole() + " for the SSN: " + request.getSsn());
		MeteorContext context = new MeteorContext();
		context.setAccessProvider(request.getAccessProvider());
		context.setSecurityToken(token);
		MeteorDataResponseWrapper dataResponse = dataServer.getData(context, request.getSsn());

		if ("Y".equals(dataProviderProperties.getProperty("DataProvider.Data.usepropertydata"))) {
			setDataProviderData(dataResponse);
		}

		if (Role.APCSR.equals(token.getRole()) || Role.LENDER.equals(token.getRole())) {
			filterAliases(dataResponse, request, token);
		}

		adapter.setResponse(dataResponse);
	}

	private MeteorDataResponseWrapper createResponseWithMessage(MeteorMessage message, RsMsgLevelEnum level) {
		MeteorDataResponseWrapper dataResponse = new MeteorDataResponseWrapper();
		dataResponse.addMessage(message.getPropertyRef(), level.name());
		return dataResponse;
	}

	private void setDataProviderData(MeteorDataResponseWrapper response) {
		MeteorDataResponseWrapper.DataProviderDataParams params = response.new DataProviderDataParams();
		params.setName(dataProviderProperties.getProperty("DataProvider.Data.Name"));
		params.setId(dataProviderProperties.getProperty("DataProvider.Data.ID"));
		params.setUrl(dataProviderProperties.getProperty("DataProvider.Data.URL"));
		params.setType(dataProviderProperties.getProperty("DataProvider.Data.Type"));
		params.setPhone(dataProviderProperties.getProperty("DataProvider.Data.Contacts.PhoneNum"));
		params.setPhoneType(PhoneNumTypeEnum.P.name());
		params.setEmail(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Email"));
		params.setAddr1(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Addr"));
		params.setAddr2(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Addr2"));
		params.setAddr3(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Addr3"));
		params.setCity(dataProviderProperties.getProperty("DataProvider.Data.Contacts.City"));
		params.setStateProvince(dataProviderProperties.getProperty("DataProvider.Data.Contacts.StateProv"));
		params.setPostalCode(dataProviderProperties.getProperty("DataProvider.Data.Contacts.PostalCd"));

		params.setDataProviderData();
	}

	private void filterAliases(MeteorDataResponseWrapper dataResponse, RequestWrapper request, SecurityToken token) {
		boolean couldntFindAliases = false;
		List<String> aliases;
		try {
			if (Role.LENDER.equals(token.getRole())) {
				aliases = registryManager.getAliases(token.getLender(), ProviderType.LENDER);
			} else {
				aliases = registryManager.getAliases(request.getAccessProvider().getMeteorInstitutionIdentifier(), ProviderType.ACCESS);
			}
		} catch (RegistryException e) {
			aliases = null;
			couldntFindAliases = true;
		}

		MeteorRsMsg responseMsg = dataResponse.getResponse();

		if (responseMsg.getMeteorDataProviderInfoCount() <= 0) {
			return;
		}

		if ((aliases == null || aliases.isEmpty())) {
			removeAllAwardsAndMessages(responseMsg);
			if (couldntFindAliases) {
				setDataProviderData(dataResponse);
				dataResponse.addMessage(MeteorMessage.ACCESS_ALIAS_ERROR.getPropertyRef(), RsMsgLevelEnum.E.name());
			}
			return;
		}

		for (MeteorDataProviderInfo info : responseMsg.getMeteorDataProviderInfo()) {
			if (aliases.contains(getDataProviderId(info))) {
				continue;
			}

			MeteorDataProviderAwardDetails awardDetails = info.getMeteorDataProviderAwardDetails();
			if (awardDetails != null && awardDetails.getAwardCount() > 0) {
				for (Award award : awardDetails.getAward()) {
					filterAwardIfNotAlias(aliases, award, awardDetails);
				}
			}
		}
	}

	private void removeAllAwardsAndMessages(MeteorRsMsg responseMsg) {
		for (MeteorDataProviderInfo info : responseMsg.getMeteorDataProviderInfo()) {
			if (info.getMeteorDataProviderAwardDetails() != null) {
				info.getMeteorDataProviderAwardDetails().removeAllAward();
			}

			info.removeAllMeteorDataProviderMsg();
		}
	}

	private String getDataProviderId(MeteorDataProviderInfo info) {
		if (info.getMeteorDataProviderDetailInfo() == null) {
			return null;
		}

		if (info.getMeteorDataProviderDetailInfo().getDataProviderData() == null) {
			return null;
		}

		return info.getMeteorDataProviderDetailInfo().getDataProviderData().getEntityID();
	}

	private void filterAwardIfNotAlias(List<String> aliases, Award award, MeteorDataProviderAwardDetails awardDetails) {
		if (award.getConsolLender() != null && award.getConsolLender().getEntityID() != null && aliases.contains(award.getConsolLender().getEntityID())) {
			return;
		}

		if (award.getDisbursingAgent() != null && award.getDisbursingAgent().getEntityID() != null && aliases.contains(award.getDisbursingAgent().getEntityID())) {
			return;
		}

		if (award.getFinAidTranscript() != null && award.getFinAidTranscript().getEntityID() != null && aliases.contains(award.getFinAidTranscript().getEntityID())) {
			return;
		}

		if (award.getGrantScholarshipProvider() != null && award.getGrantScholarshipProvider().getEntityID() != null && aliases.contains(award.getGrantScholarshipProvider().getEntityID())) {
			return;
		}

		if (award.getGuarantor() != null && award.getGuarantor().getEntityID() != null && aliases.contains(award.getGuarantor().getEntityID())) {
			return;
		}

		if (award.getLender() != null && award.getLender().getEntityID() != null && aliases.contains(award.getLender().getEntityID())) {
			return;
		}

		if (award.getServicer() != null && award.getServicer().getEntityID() != null && aliases.contains(award.getServicer().getEntityID())) {
			return;
		}

		if (award.getSchool() != null && award.getSchool().getEntityID() != null && aliases.contains(award.getSchool().getEntityID())) {
			return;
		}

		awardDetails.removeAward(award);
	}

	public RequestInfo getRequestInfo() {
		// overridden by spring method injection
		return null;
	}

	public DataServerAbstraction getDataServer() {
		return dataServer;
	}

	@Autowired
	@Qualifier("DataServerAbstractionImpl")
	public void setDataServer(DataServerAbstraction dataServer) {
		this.dataServer = dataServer;
	}

	public Properties getDataProviderProperties() {
		return dataProviderProperties;
	}

	@Autowired
	@Qualifier("DataProviderProperties")
	public void setDataProviderProperties(Properties dataProviderProperties) {
		this.dataProviderProperties = dataProviderProperties;
	}

	public Properties getMeteorProperties() {
		return meteorProperties;
	}

	@Autowired
	@Qualifier("MeteorProperties")
	public void setMeteorProperties(Properties meteorProperties) {
		this.meteorProperties = meteorProperties;
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("AuthenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
