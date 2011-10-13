package org.meteornetwork.meteor.provider.index;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;

/**
 * Loads data providers from a properties file on the classpath. Specify the
 * properties file in indexprovider.properties, e.g.
 * 
 * propertiesindexserver.propertyfile=indexresponsesample
 * 
 * Do not specify the ".properties" extension!
 * 
 * @author jlazos
 * 
 */
public class PropertiesIndexServer implements IndexServerAbstraction {

	private final ResourceBundle indexResponseProperties;

	public PropertiesIndexServer() {
		ResourceBundle indexRespProps;
		try {
			ResourceBundle indexProviderProperties = ResourceBundle.getBundle("indexprovider");
			indexRespProps = ResourceBundle.getBundle(indexProviderProperties.getString("propertiesindexserver.propertyfile"));
		} catch (MissingResourceException e) {
			indexRespProps = null;
		}

		indexResponseProperties = indexRespProps;
	}

	@Override
	public MeteorIndexResponseWrapper getDataProviders(MeteorContext context, String ssn) {
		MeteorIndexResponseWrapper response = new MeteorIndexResponseWrapper();

		if (indexResponseProperties == null) {
			response.addMessage("Could not find property file with index response data", RsMsgLevelEnum.E);
			return response;
		}

		try {
			int i = 1;
			while (true) {
				DataProvider dataProvider = new DataProvider();
				dataProvider.setEntityID(indexResponseProperties.getString("dp." + i + ".id"));

				try {
					dataProvider.setEntityName(indexResponseProperties.getString("dp." + i + ".name"));
				} catch (MissingResourceException e) {
					// allow null
				}

				try {
					dataProvider.setEntityURL(indexResponseProperties.getString("dp." + i + ".url"));
				} catch (MissingResourceException e) {
					// allow null
				}

				response.addDataProviders(dataProvider);
				++i;
			}
		} catch (MissingResourceException e) {
			// no more data providers can be found
		}

		return response;
	}

}
