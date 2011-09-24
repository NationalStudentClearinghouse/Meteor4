package org.meteornetwork.meteor.provider.data.manager;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.types.DataProviderTypeType;
import org.meteornetwork.meteor.common.xml.dataresponse.types.PhoneNumTypeType;
import org.meteornetwork.meteor.provider.data.DataServerAbstraction;
import org.meteornetwork.meteor.provider.data.MeteorContext;
import org.meteornetwork.meteor.provider.data.MeteorDataResponseWrapper;
import org.meteornetwork.meteor.provider.data.adapter.DataQueryAdapter;
import org.meteornetwork.meteor.provider.data.adapter.RequestWrapper;
import org.meteornetwork.meteor.provider.data.adapter.ResponseWrapper;
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

	private static final Log LOG = LogFactory.getLog(DataProviderManager.class);

	private DataServerAbstraction dataServer;
	private Properties dataProviderProperties;

	/**
	 * Executes query for single SSN
	 * 
	 * @param adapter
	 *            - translation layer object. This method gets request data and
	 *            sets response data using this adapter
	 */
	public void queryDataForBorrower(DataQueryAdapter adapter) {
		RequestWrapper request = adapter.getRequest();
		if (request == null) {
			// TODO: meteor error message?
			return;
		}

		LOG.debug("Request received for data on SSN: " + request.getSsn() + " from Access Provider " + request.getAccessProvider().getMeteorInstitutionIdentifier());

		MeteorContext context = new MeteorContext();
		context.setAccessProvider(request.getAccessProvider());
		context.setSecurityToken(getRequestInfo().getSecurityToken());
		MeteorDataResponseWrapper dataResponse = dataServer.getData(context, request.getSsn());

		if ("Y".equals(dataProviderProperties.getProperty("DataProvider.Data.usepropertydata"))) {
			setDataProviderData(dataResponse);
		}

		ResponseWrapper response = new ResponseWrapper();
		response.setResponse(dataResponse.getResponse());
		adapter.setResponse(response);
	}

	private void setDataProviderData(MeteorDataResponseWrapper response) {
		MeteorDataResponseWrapper.DataProviderDataParams params = response.new DataProviderDataParams();
		params.setName(dataProviderProperties.getProperty("DataProvider.Data.Name"));
		params.setId(dataProviderProperties.getProperty("DataProvider.Data.ID"));
		params.setUrl(dataProviderProperties.getProperty("DataProvider.Data.URL"));
		params.setType(DataProviderTypeType.valueOf(dataProviderProperties.getProperty("DataProvider.Data.Type")));
		params.setPhone(dataProviderProperties.getProperty("DataProvider.Data.Contacts.PhoneNum"));
		params.setPhoneType(PhoneNumTypeType.P);
		params.setEmail(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Email"));
		params.setAddr1(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Addr"));
		params.setAddr2(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Addr2"));
		params.setAddr3(dataProviderProperties.getProperty("DataProvider.Data.Contacts.Addr3"));
		params.setCity(dataProviderProperties.getProperty("DataProvider.Data.Contacts.City"));
		params.setStateProvince(dataProviderProperties.getProperty("DataProvider.Data.Contacts.StateProv"));
		params.setPostalCode(dataProviderProperties.getProperty("DataProvider.Data.Contacts.PostalCd"));

		params.setDataProviderData();
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
}
