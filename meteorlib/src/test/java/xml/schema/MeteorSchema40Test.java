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
package xml.schema;

import static junit.framework.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.Assert;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.Contacts;
import org.meteornetwork.meteor.common.xml.dataresponse.Deferment;
import org.meteornetwork.meteor.common.xml.dataresponse.Forbearance;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;

import util.TestUtils;

public class MeteorSchema40Test {

	@Test
	public void testNewSchemaElementsAll() throws IOException, IndexOutOfBoundsException, ParseException {
		String testXml = TestUtils.getFileString(this.getClass(), "meteor-schema-40-new-elements.xml");

		MeteorRsMsg response = null;
		try {
			response = MeteorRsMsg.unmarshal(new StringReader(testXml));
		} catch (MarshalException e) {
			e.printStackTrace();
			fail("Could not marshal xml");
		} catch (ValidationException e) {
			e.printStackTrace();
			fail("Could not validate xml");
		}

		Award award = response.getMeteorDataProviderInfo(0).getMeteorDataProviderAwardDetails().getAward(0);
		Assert.assertEquals(TestUtils.parseDate("2063-04-05 -0000", "yyyy-MM-dd Z"), TestUtils.zeroTime(award.getLoanDt().toDate()));
		Assert.assertEquals("0000111122K334455ABCD", award.getAwardId());

		Assert.assertEquals("123", award.getRepayment().getRepaymentTermRemaining());
		Assert.assertEquals(BigDecimal.valueOf(999999999.99), award.getRepayment().getCurrentMonthlyPayment());
		Assert.assertEquals("999", award.getRepayment().getDaysPastDue());

		Contacts contacts = response.getMeteorDataProviderInfo(0).getMeteorDataProviderDetailInfo().getDataProviderData().getContacts();
		Assert.assertTrue(contacts.getPhone(0).getPhoneValidInd());
		Assert.assertEquals(TestUtils.parseDate("2063-04-05 -0000", "yyyy-MM-dd Z"), TestUtils.zeroTime(contacts.getPhone(0).getPhoneValidDt().toDate()));
		Assert.assertTrue(contacts.getEmail().getEmailValidInd());
		Assert.assertEquals(TestUtils.parseDate("2063-04-05 -0000", "yyyy-MM-dd Z"), TestUtils.zeroTime(contacts.getEmail().getEmailValidDt().toDate()));
		
		Assert.assertEquals(2, award.getRepayment().getDefermentCount());
		Deferment deferment = award.getRepayment().getDeferment(0);
		Assert.assertEquals("Armed Forces", deferment.getDefermentForbearanceTypeCode());
		deferment = award.getRepayment().getDeferment(1);
		Assert.assertEquals("240", deferment.getDefermentForbearanceTimeUsed());
		
		Assert.assertEquals(2, award.getRepayment().getForbearanceCount());
		Forbearance forbearance = award.getRepayment().getForbearance(0);
		Assert.assertEquals("Administrative", forbearance.getDefermentForbearanceTypeCode());
		forbearance = award.getRepayment().getForbearance(1);
		Assert.assertEquals("99", forbearance.getDefermentForbearanceTimeUsed());
		
		Assert.assertEquals("http://not-a-real-domain-false1.com", award.getOnlinePaymentProcessURL());
		Assert.assertEquals("http://not-a-real-domain-false2.com", award.getOnlineDeferForbProcessURL());
		Assert.assertTrue(response.getMeteorDataProviderInfo(0).getLoanLocatorActivationIndicator());
	}

}
