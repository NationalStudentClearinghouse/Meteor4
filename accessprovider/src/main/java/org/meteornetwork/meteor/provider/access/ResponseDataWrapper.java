package org.meteornetwork.meteor.provider.access;

import java.util.Map;
import java.util.Set;

import org.meteornetwork.meteor.business.BestSourceAggregator;
import org.meteornetwork.meteor.common.util.message.Messages;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.Contacts;
import org.meteornetwork.meteor.common.xml.dataresponse.DataProviderAggregateTotal;
import org.meteornetwork.meteor.common.xml.dataresponse.DataProviderData;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderAwardDetails;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderDetailInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderMsg;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.Message;
import org.meteornetwork.meteor.common.xml.indexresponse.types.RsMsgLevelEnum;

/**
 * Adds messages and other data to a single MeteorRsMsg object
 * 
 * @author jlazos
 */
public class ResponseDataWrapper {

	// special DataProviderType to indicate index response message container
	private static final String IDX = "IDX";

	private transient MeteorRsMsg responseData;
	private transient MeteorDataProviderInfo indexMessageMdpi;
	private transient BestSourceAggregator bestSourceAggregator;

	private transient Integer nextAwardId = 0;

	public ResponseDataWrapper() {
		this.responseData = new MeteorRsMsg();
	}

	public void addDataProviderInfo(MeteorRsMsg dataProviderResponse) {
		for (MeteorDataProviderInfo info : dataProviderResponse.getMeteorDataProviderInfo()) {
			responseData.addMeteorDataProviderInfo(info);
			setAPSUniqueAwardIds(info);
		}
	}

	private void setAPSUniqueAwardIds(MeteorDataProviderInfo info) {
		MeteorDataProviderAwardDetails awardDetails = info.getMeteorDataProviderAwardDetails();
		if (awardDetails == null || awardDetails.getAwardCount() <= 0) {
			return;
		}

		for (Award award : awardDetails.getAward()) {
			award.setAPSUniqueAwardID(nextAwardId++);
		}
	}

	public void addAllDataProviderInfo(Iterable<MeteorRsMsg> dataProviderResponses) {
		if (dataProviderResponses != null) {
			for (MeteorRsMsg response : dataProviderResponses) {
				addDataProviderInfo(response);
			}
		}
	}

	/**
	 * Adds message from index provider to response
	 * 
	 * @param message
	 *            the message to add to the response
	 */
	public void addIndexProviderMessage(Message message) {
		addIndexProviderMessage(message.getRsMsg(), message.getRsMsgLevel());
	}

	/**
	 * Add message to index provider messages
	 * 
	 * @param msgLevel
	 *            severity level of message
	 * @param message
	 *            message property reference
	 * @param msgParameters
	 *            parameters for message template referenced from property
	 */
	public void addIndexProviderMessage(RsMsgLevelEnum msgLevel, MeteorMessage message, Map<String, String> msgParameters) {
		String messageContent = Messages.getMessage(message.getPropertyRef(), msgParameters);

		addIndexProviderMessage(messageContent, msgLevel.name());
	}

	private void addIndexProviderMessage(String message, String messageLevel) {
		if (indexMessageMdpi == null) {
			indexMessageMdpi = createMinimalMeteorDataProviderInfo(IDX);
		}

		MeteorDataProviderMsg mdpMessage = new MeteorDataProviderMsg();
		mdpMessage.setRsMsgLevel(messageLevel);
		mdpMessage.setRsMsg(message);
		indexMessageMdpi.addMeteorDataProviderMsg(mdpMessage);
	}

	public MeteorDataProviderInfo createMinimalMeteorDataProviderInfo(String dataProviderType) {
		MeteorDataProviderInfo mdpi = new MeteorDataProviderInfo();
		MeteorDataProviderDetailInfo mdpdi = new MeteorDataProviderDetailInfo();
		mdpi.setMeteorDataProviderDetailInfo(mdpdi);

		mdpdi.setDataProviderType(dataProviderType);

		DataProviderData dpd = new DataProviderData();
		mdpdi.setDataProviderData(dpd);

		Contacts contacts = new Contacts();
		dpd.setContacts(contacts);

		mdpdi.setDataProviderAggregateTotal(new DataProviderAggregateTotal());
		return mdpi;
	}

	/**
	 * Returns response data without best source filtering logic applied to
	 * awards
	 * 
	 * @return response data without best source logic applied to awards
	 */
	public MeteorRsMsg getUnfilteredResponseData() {
		return responseData;
	}

	/**
	 * Get response data with awards filtered by best source logic. Creates a
	 * new BestSourceAggregator to filter the awards, which is accessible by
	 * invoking getBestSourceAggregator() after calling this method
	 * 
	 * @return response data with awards filtered by best source logic
	 */
	public MeteorRsMsg getResponseDataBestSource() {
		bestSourceAggregator = new BestSourceAggregator();

		for (MeteorDataProviderInfo info : responseData.getMeteorDataProviderInfo()) {
			if (info.getMeteorDataProviderAwardDetails() != null) {
				for (Award award : info.getMeteorDataProviderAwardDetails().getAward()) {
					bestSourceAggregator.add(award);
				}
			}
		}

		Set<Award> bestAwards = bestSourceAggregator.getBest();

		MeteorRsMsg withBestSource = new MeteorRsMsg();
		withBestSource.setMeteorIndexProviderData(responseData.getMeteorIndexProviderData());
		withBestSource.setMeteorDataAggregates(responseData.getMeteorDataAggregates());

		for (MeteorDataProviderInfo info : responseData.getMeteorDataProviderInfo()) {
			MeteorDataProviderInfo newInfo = new MeteorDataProviderInfo();
			newInfo.setMeteorDataProviderMsg(info.getMeteorDataProviderMsg());
			newInfo.setMeteorDataProviderDetailInfo(info.getMeteorDataProviderDetailInfo());
			withBestSource.addMeteorDataProviderInfo(newInfo);

			if (info.getMeteorDataProviderAwardDetails() == null) {
				continue;
			}

			newInfo.setMeteorDataProviderAwardDetails(new MeteorDataProviderAwardDetails());
			for (Award award : info.getMeteorDataProviderAwardDetails().getAward()) {
				if (bestAwards.contains(award)) {
					newInfo.getMeteorDataProviderAwardDetails().addAward(award);
				}
			}
		}

		return withBestSource;
	}

	/**
	 * Get the best source aggregator used after the last call to
	 * getResponseDataBestSource()
	 * 
	 * @return the best source aggregator used after the last call to
	 *         getResponseDataBestSource()
	 */
	public BestSourceAggregator getBestSourceAggregator() {
		return bestSourceAggregator;
	}
}
