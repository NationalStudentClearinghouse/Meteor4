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
package org.meteornetwork.meteor.provider.access.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;

import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.XSLTransformManager;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;
import org.meteornetwork.meteor.provider.access.DataProviderInfo;
import org.meteornetwork.meteor.provider.access.MeteorQueryException;
import org.meteornetwork.meteor.provider.access.ResponseDataWrapper;
import org.meteornetwork.meteor.provider.access.service.DataQueryService;
import org.meteornetwork.meteor.provider.access.service.IndexQueryService;
import org.meteornetwork.meteor.saml.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//@Service
public class AccessProviderManager {

	private static final Logger LOG = LoggerFactory.getLogger(AccessProviderManager.class);

	private IndexQueryService indexQueryService;
	private DataQueryService dataQueryService;

	private XSLTransformManager xslTransformManager;
	private Templates maskSSNsTemplate;

	/**
	 * Queries the meteor network for data on the provided ssn
	 * 
	 * @param ssn
	 *            the ssn of the borrower to query for
	 * @return response XML string (instance of Meteor Schema)
	 */
	public ResponseDataWrapper queryMeteor(String ssn) {
		ResponseDataWrapper responseData = new ResponseDataWrapper();
		responseData.setBorrowerSsn(ssn);
		
		Set<DataProviderInfo> dataProviders = null;
		try {
			dataProviders = indexQueryService.getDataProviders(ssn, responseData);
		} catch (MeteorQueryException e) {
			// could not get list of data providers. return response message as
			// it is with whatever errors were set
			return responseData;
		}

		RequestInfo requestInfo = getRequestInfo();
		if (dataProviders == null || dataProviders.isEmpty()) {
			LOG.debug("No data providers found");
			if (Role.BORROWER.equals(requestInfo.getSecurityToken().getRole())) {
				responseData.addIndexProviderMessage(RsMsgLevelEnum.E, MeteorMessage.INDEX_NO_DATA_PROVIDERS_FOUND_BORROWER, null);
			} else {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("ssn", ssn);
				responseData.addIndexProviderMessage(RsMsgLevelEnum.E, MeteorMessage.INDEX_NO_DATA_PROVIDERS_FOUND_FAA, parameters);
			}
			return responseData;
		}

		List<MeteorRsMsg> dataProviderResponses = dataQueryService.getData(responseData, dataProviders, ssn);

		responseData.addAllDataProviderInfo(dataProviderResponses);

		return responseData;
	}

	/**
	 * Masks the SSNs in the response xml
	 * 
	 * @param xml
	 *            the response xml
	 * @return response xml with SSNs masked and unmasked attribute added
	 * @throws IOException
	 * @throws TransformerException
	 */
	public String maskSSNs(String xml) throws TransformerException, IOException {
		return xslTransformManager.transformXML(xml, maskSSNsTemplate);
	}

	public IndexQueryService getIndexQueryService() {
		return indexQueryService;
	}

	@Autowired
	public void setIndexQueryService(IndexQueryService indexQueryService) {
		this.indexQueryService = indexQueryService;
	}

	public DataQueryService getDataQueryService() {
		return dataQueryService;
	}

	@Autowired
	public void setDataQueryService(DataQueryService dataQueryService) {
		this.dataQueryService = dataQueryService;
	}

	public RequestInfo getRequestInfo() {
		// method injection implemented by spring
		return null;
	}

	public XSLTransformManager getXslTransformManager() {
		return xslTransformManager;
	}

	@Autowired
	public void setXslTransformManager(XSLTransformManager xslTransformManager) {
		this.xslTransformManager = xslTransformManager;
	}

	public Templates getMaskSSNsTemplate() {
		return maskSSNsTemplate;
	}

	@Autowired
	@Qualifier("MaskSSNTemplate")
	public void setMaskSSNsTemplate(Templates maskSSNsTemplate) {
		this.maskSSNsTemplate = maskSSNsTemplate;
	}
}
