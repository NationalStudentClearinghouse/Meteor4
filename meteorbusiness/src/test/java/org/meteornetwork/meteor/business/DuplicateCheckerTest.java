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
package org.meteornetwork.meteor.business;

import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Assert;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;

public class DuplicateCheckerTest {

	@Test
	public void testAwardsAreDuplicates() throws MarshalException, ValidationException, TransformerConfigurationException, IOException {
		Award award1 = TestUtils.loadAward(this.getClass(), "award1.xml");
		Award award2 = TestUtils.loadAward(this.getClass(), "award2.xml");

		Assert.assertTrue(runDuplicateChecker(award1, award2));

		Award award3 = TestUtils.loadAward(this.getClass(), "award3.xml");
		Assert.assertFalse(runDuplicateChecker(award1, award3));
		Assert.assertTrue(runDuplicateChecker(award2, award3));

		Award award4 = TestUtils.loadAward(this.getClass(), "award4.xml");
		Assert.assertFalse(runDuplicateChecker(award1, award4));

		Award award5 = TestUtils.loadAward(this.getClass(), "award5.xml");
		Assert.assertFalse(runDuplicateChecker(award4, award5));

		Award award6 = TestUtils.loadAward(this.getClass(), "award6.xml");
		Assert.assertTrue(runDuplicateChecker(award4, award6));
		Assert.assertTrue(runDuplicateChecker(award5, award6));

		Award award7 = TestUtils.loadAward(this.getClass(), "award7.xml");
		Assert.assertFalse(runDuplicateChecker(award4, award7));
		Assert.assertFalse(runDuplicateChecker(award5, award7));
		Assert.assertFalse(runDuplicateChecker(award6, award7));

		Award award8 = TestUtils.loadAward(this.getClass(), "award8.xml");
		Assert.assertFalse(runDuplicateChecker(award4, award8));
		Assert.assertFalse(runDuplicateChecker(award5, award8));
		Assert.assertFalse(runDuplicateChecker(award6, award8));
		Assert.assertFalse(runDuplicateChecker(award7, award8));

		Award award9 = TestUtils.loadAward(this.getClass(), "award9.xml");
		Assert.assertTrue(runDuplicateChecker(award8, award9));

		Award award10 = TestUtils.loadAward(this.getClass(), "award10.xml");
		Assert.assertTrue(runDuplicateChecker(award1, award10));
		Assert.assertFalse(runDuplicateChecker(award2, award10));

		Award award11 = TestUtils.loadAward(this.getClass(), "award11.xml");
		Assert.assertFalse(runDuplicateChecker(award7, award11));

		Award award12 = TestUtils.loadAward(this.getClass(), "award12.xml");
		Assert.assertFalse(runDuplicateChecker(award7, award12));

		Award award13 = TestUtils.loadAward(this.getClass(), "award13.xml");
		Assert.assertFalse(runDuplicateChecker(award10, award13));

		Award award14 = TestUtils.loadAward(this.getClass(), "award14.xml");
		Assert.assertFalse(runDuplicateChecker(award5, award14));
	}

	/*
	 * DuplicateChecker awardsAreDuplicates must be reflexive (i.e. if award 1
	 * is a duplicate of award 2, award 2 must be a duplicate of award 1)
	 */
	private boolean runDuplicateChecker(Award firstAward, Award secondAward) {
		DuplicateChecker dupeChecker = new DuplicateChecker(firstAward, secondAward);
		boolean firstTry = dupeChecker.awardsAreDuplicates();
		dupeChecker.setExistingAward(secondAward);
		dupeChecker.setNewAward(firstAward);
		boolean reversed = dupeChecker.awardsAreDuplicates();

		Assert.assertEquals(firstTry, reversed);
		return firstTry;
	}

	
}
