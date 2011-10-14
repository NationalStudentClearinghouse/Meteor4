package org.meteornetwork.meteor.provider.index;

import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProviders;
import org.meteornetwork.meteor.common.xml.indexresponse.IndexProviderData;
import org.meteornetwork.meteor.common.xml.indexresponse.IndexProviderMessages;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;

public class MeteorIndexResponseWrapper {

	private final MeteorIndexResponse response;

	public MeteorIndexResponseWrapper() {
		response = new MeteorIndexResponse();
		response.setDataProviders(new DataProviders());
	}
	
	public void setIndexProviderData(String id, String name, String url) {
		IndexProviderData data = new IndexProviderData();
		data.setEntityID(id);
		data.setEntityName(name);
		data.setEntityURL(url);
		response.setIndexProviderData(data);
	}
	
	public void addMessage(String messageText, RsMsgLevelEnum level) {
		Message message = new Message();
		message.setRsMsg(messageText);
		message.setRsMsgLevel(level.name());

		if (response.getIndexProviderMessages() == null) {
			response.setIndexProviderMessages(new IndexProviderMessages());
		}

		response.getIndexProviderMessages().addMessage(message);
	}

	public void addDataProviders(DataProvider... dataProviders) {
		for (DataProvider dataProvider : dataProviders) {
			response.getDataProviders().addDataProvider(dataProvider);
		}
	}

	public void addDataProviders(Iterable<DataProvider> dataProviders) {
		for (DataProvider dataProvider : dataProviders) {
			response.getDataProviders().addDataProvider(dataProvider);
		}
	}

	public MeteorIndexResponse getResponse() {
		return response;
	}

}
