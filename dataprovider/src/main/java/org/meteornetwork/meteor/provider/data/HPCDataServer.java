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
package org.meteornetwork.meteor.provider.data;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.meteornetwork.meteor.common.abstraction.data.DataServerAbstraction;
import org.meteornetwork.meteor.common.abstraction.data.MeteorContext;
import org.meteornetwork.meteor.common.abstraction.data.MeteorDataResponseWrapper;
import org.meteornetwork.meteor.common.hpc.HPCManager;
import org.meteornetwork.meteor.common.hpc.HPCMessageParams;
import org.meteornetwork.meteor.common.hpc.HPCRequestRPCEncodingInterceptor;
import org.meteornetwork.meteor.common.ws.DataProviderHPCService;
import org.meteornetwork.meteor.common.xml.dataresponse.types.RsMsgLevelEnum;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCCompressionType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCEncodingType;
import org.meteornetwork.meteor.saml.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Legacy HPC implementation for backwards-compatibility with existing data
 * provider software
 * 
 * @author jlazos
 */
public class HPCDataServer implements DataServerAbstraction {

	private static final Logger LOG = LoggerFactory.getLogger(HPCDataServer.class);

	private ResourceBundle dataProviderProperties;

	private final String HPC_ADDRESS;
	private HPCManager hpcManager;

	public HPCDataServer() {
		dataProviderProperties = ResourceBundle.getBundle("dataprovider");
		HPC_ADDRESS = dataProviderProperties.getString("DataProvider.HPC.AccessURL");
	}

	@Override
	public MeteorDataResponseWrapper getData(MeteorContext context, String ssn) {
		LOG.debug("Entering HPC data server");

		HPCMessageParams messageParams = new HPCMessageParams();
		messageParams.setRecipientId(getResource("DataProvider.HPC.AccessPoint", "HPC.DATAPROVIDER.ACCESSPOINT"));
		messageParams.setTransactionType(getResource("DataProvider.HPC.DataSource", "EEAT-HPCP-DATASOURCE"));
		messageParams.setCompression(HPCCompressionType.fromValue(getResource("DataProvider.HPC.Compression", "zlib")));
		messageParams.setEncoding(HPCEncodingType.fromValue(getResource("DataProvider.HPC.Encoding", "base64")));

		LOG.debug("Message id: " + messageParams.getMessageId());

		String requestTag;
		Role role = null;
		if (context.getSecurityToken() == null || context.getSecurityToken().getRole() == null) {
			requestTag = "BadRoleSSN";
		} else {
			role = context.getSecurityToken().getRole();
			switch (role) {
			case BORROWER:
				requestTag = getResource("DataProvider.HPC.ContentTAG.BORROWER", "BorrowerSSN");
				messageParams.setContentType(getResource("DataProvider.HPC.ContentID.BORROWER", "METEOR"));
				break;
			case FAA:
				requestTag = getResource("DataProvider.HPC.ContentTAG.FAA", "StudentSSN");
				messageParams.setContentType(getResource("DataProvider.HPC.ContentID.FAA", "METEOR"));
				break;
			case APCSR:
				requestTag = getResource("DataProvider.HPC.ContentTAG.APCSR", "CustomerSSN");
				messageParams.setContentType(getResource("DataProvider.HPC.ContentID.APCSR", "METEOR"));
				break;
			case LENDER:
				requestTag = getResource("DataProvider.HPC.ContentTAG.LENDER", "StudentSSN");
				messageParams.setContentType(getResource("DataProvider.HPC.ContentID.LENDER", "METEOR"));
				break;
			default:
				requestTag = "BadRoleSSN";
			}
		}

		String hpcRequest;
		try {
			hpcRequest = hpcManager.generateHPCMessage("<" + requestTag + ">" + ssn + "</" + requestTag + ">", messageParams);
		} catch (Exception e) {
			LOG.error("Unable to create HPC request", e);
			return createMinimalErrorResponse();
		}

		LOG.debug("Sending message to HPC endpoint for SSN " + ssn + " and role " + (role == null ? "" : role.getName()));
		DataProviderHPCService dataService = (DataProviderHPCService) getHPCClientProxy().create();
		String hpcResponse = dataService.submitHPC(hpcRequest);

		LOG.debug("Received response from HPC endpoint");
		String unwrappedResponse;
		try {
			unwrappedResponse = hpcManager.retrieveHPCContent(hpcResponse);
		} catch (Exception e) {
			LOG.error("Unable to parse HPC response", e);
			return createMinimalErrorResponse();
		}

		try {
			return new MeteorDataResponseWrapper(unwrappedResponse);
		} catch (Exception e) {
			LOG.error("Unable to unmarshal MeteorRsMsg from response xml", e);
			return createMinimalErrorResponse();
		}
	}

	private String getResource(String key, String defaultValue) {
		try {
			return dataProviderProperties.getString(key);
		} catch (MissingResourceException e) {
			return defaultValue;
		}
	}

	private JaxWsProxyFactoryBean getHPCClientProxy() {
		JaxWsProxyFactoryBean hpcClientFactory = new JaxWsProxyFactoryBean();
		hpcClientFactory.setAddress(HPC_ADDRESS);
		hpcClientFactory.setServiceClass(DataProviderHPCService.class);

		List<Interceptor<? extends Message>> outInterceptors = new ArrayList<Interceptor<? extends Message>>();
		outInterceptors.add(new HPCRequestRPCEncodingInterceptor());

		hpcClientFactory.setOutInterceptors(outInterceptors);
		return hpcClientFactory;
	}

	private MeteorDataResponseWrapper createMinimalErrorResponse() {
		MeteorDataResponseWrapper minimal = new MeteorDataResponseWrapper();
		minimal.createMinimalResponse();
		minimal.addMessage("Unable to retrieve data, please try again later", RsMsgLevelEnum.E.name());
		return minimal;
	}

	public HPCManager getHpcManager() {
		return hpcManager;
	}

	@Autowired
	public void setHpcManager(HPCManager hpcManager) {
		this.hpcManager = hpcManager;
	}

}
