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
package org.meteornetwork.meteor.provider.ui.controller;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderAwardDetails;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

public class ConsolidatedViewController extends AwardDetailController {

	private static final Logger LOG = LoggerFactory.getLogger(ConsolidatedViewController.class);
	
	@Override
	protected void handleMeteorRequest(ModelAndView modelAndView, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.handleMeteorRequest(modelAndView, httpRequest, httpResponse);
		
		// tailor response to show only specified award and its duplicates
		LOG.debug("Preparing consolidated view");
		Integer awardId = Integer.valueOf(getAPSUniqueAwardId(httpRequest));
		
		MeteorRsMsg allAwards;
		TreeMap<Integer, ArrayList<Integer>> duplicateMap = getSession().getDuplicateAwardIds();
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
