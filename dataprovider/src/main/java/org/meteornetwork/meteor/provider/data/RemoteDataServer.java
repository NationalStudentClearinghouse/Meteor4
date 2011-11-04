package org.meteornetwork.meteor.provider.data;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import org.apache.cxf.helpers.IOUtils;
import org.meteornetwork.meteor.common.xml.dataresponse.types.RsMsgLevelEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple data server implementation that gets Meteor XML data from a specified
 * URL
 * 
 * @author jlazos
 * 
 */
public class RemoteDataServer implements DataServerAbstraction {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteDataServer.class);

	private ResourceBundle dataProviderProperties;

	private final String ADDRESS;

	public RemoteDataServer() {
		dataProviderProperties = ResourceBundle.getBundle("dataprovider");
		ADDRESS = dataProviderProperties.getString("remotedataserver.url");
	}

	@Override
	public MeteorDataResponseWrapper getData(MeteorContext context, String ssn) {
		String charset = "UTF-8";

		String role = context.getSecurityToken() != null && context.getSecurityToken().getRole() != null ? context.getSecurityToken().getRole().getName() : "";

		try {
			String query = String.format("ssn=%s&role=%s", URLEncoder.encode(ssn, "UTF-8"), URLEncoder.encode(role, "UTF-8"));
			URLConnection connection = new URL(ADDRESS + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);

			return new MeteorDataResponseWrapper(IOUtils.readStringFromStream(connection.getInputStream()));
		} catch (Exception e) {
			LOG.error("Could not get data from remote resource", e);
			MeteorDataResponseWrapper minimal = new MeteorDataResponseWrapper();
			minimal.addMessage("Unable to retrieve data, please try again later", RsMsgLevelEnum.E.name());
			minimal.createMinimalResponse();

			return minimal;
		}
	}

}
