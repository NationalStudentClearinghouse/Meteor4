package org.meteornetwork.meteor.provider.index;

import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProviders;
import org.meteornetwork.meteor.common.xml.indexresponse.IndexProviderMessages;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;
import org.meteornetwork.meteor.common.xml.indexresponse.MeteorIndexResponse;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelType;

public class MeteorIndexResponseWrapper {

	private final MeteorIndexResponse response = new MeteorIndexResponse();

	public void addMessage(String messageText, RsMsgLevelType level) {
		Message message = new Message();
		message.setRsMsg(messageText);
		message.setRsMsgLevel(level);

		if (response.getIndexProviderMessages() == null) {
			response.setIndexProviderMessages(new IndexProviderMessages());
		}

		response.getIndexProviderMessages().addMessage(message);
	}

	public void addDataProviders(DataProvider... dataProviders) {
		initDataProviders();

		for (DataProvider dataProvider : dataProviders) {
			response.getDataProviders().addDataProvider(dataProvider);
		}
	}

	public void addDataProviders(Iterable<DataProvider> dataProviders) {
		initDataProviders();

		for (DataProvider dataProvider : dataProviders) {
			response.getDataProviders().addDataProvider(dataProvider);
		}
	}

	private void initDataProviders() {
		if (response.getDataProviders() == null) {
			response.setDataProviders(new DataProviders());
		}
	}

	public MeteorIndexResponse getResponse() {
		return response;
	}

}
