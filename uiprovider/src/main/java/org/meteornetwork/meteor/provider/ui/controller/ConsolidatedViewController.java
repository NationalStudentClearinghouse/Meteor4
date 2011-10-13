package org.meteornetwork.meteor.provider.ui.controller;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderAwardDetails;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.springframework.web.servlet.ModelAndView;

public class ConsolidatedViewController extends AwardDetailController {

	@Override
	protected void handleMeteorRequest(ModelAndView modelAndView, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.handleMeteorRequest(modelAndView, httpRequest, httpResponse);
		
		// tailor response to show only specified award and its duplicates
		
		Integer awardId = Integer.valueOf(getAPSUniqueAwardId(httpRequest));
		
		MeteorRsMsg allAwards;
		HashMap<Integer, ArrayList<Integer>> duplicateMap = getSession().getDuplicateAwardIds();
		Set<Integer> apsUniqueAwardIds = new HashSet<Integer>();
		apsUniqueAwardIds.add(awardId);
		
		if (duplicateMap != null && duplicateMap.containsKey(awardId) && duplicateMap.get(awardId) != null) {
			apsUniqueAwardIds.addAll(duplicateMap.get(awardId));
		}
		
		StringReader reader = new StringReader(getSession().getResponseXmlUnfiltered());
		try {
			allAwards = MeteorRsMsg.unmarshal(reader);
		} finally {
			reader.close();
		}
		
		MeteorRsMsg duplicateAwardsOnly = new MeteorRsMsg();
		duplicateAwardsOnly.setMeteorIndexProviderData(allAwards.getMeteorIndexProviderData());
		duplicateAwardsOnly.setMeteorDataAggregates(allAwards.getMeteorDataAggregates());
		
		for (MeteorDataProviderInfo info : allAwards.getMeteorDataProviderInfo()) {
			MeteorDataProviderInfo newInfo = new MeteorDataProviderInfo();
			newInfo.setMeteorDataProviderMsg(info.getMeteorDataProviderMsg());
			newInfo.setMeteorDataProviderDetailInfo(info.getMeteorDataProviderDetailInfo());
			duplicateAwardsOnly.addMeteorDataProviderInfo(newInfo);

			if (info.getMeteorDataProviderAwardDetails() == null) {
				continue;
			}

			newInfo.setMeteorDataProviderAwardDetails(new MeteorDataProviderAwardDetails());
			
			for (Award award : info.getMeteorDataProviderAwardDetails().getAward()) {
				if (apsUniqueAwardIds.contains(award.getAPSUniqueAwardID())) {
					newInfo.getMeteorDataProviderAwardDetails().addAward(award);
				}
			}
		}
		
		StringWriter writer = new StringWriter();
		duplicateAwardsOnly.marshal(writer);
		addMeteorDataToModelView(modelAndView, writer.toString()); 
	}
}
