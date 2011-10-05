package org.meteornetwork.meteor.business;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Assert;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;

public class LoanListTest {

	@Test
	public void testGetBestSource() throws MarshalException, ValidationException, TransformerConfigurationException, IOException {
		Award award15 = TestUtils.loadAward(this.getClass(), "award15.xml");
		Award award16 = TestUtils.loadAward(this.getClass(), "award16.xml");
		Award award17 = TestUtils.loadAward(this.getClass(), "award17.xml");
		Award award18 = TestUtils.loadAward(this.getClass(), "award18.xml");

		LoanList loanList = new LoanList();
		loanList.addUnique(award18);
		loanList.addDuplicate(award18, award15);
		loanList.addDuplicate(award18, award16);

		List<Award> bestSourceAwards = loanList.getBestSource();
		Assert.assertTrue(bestSourceAwards.contains(award15));
		Assert.assertTrue(bestSourceAwards.contains(award18));
		Assert.assertFalse(bestSourceAwards.contains(award17));

		loanList = new LoanList();
		loanList.addUnique(award15);
		loanList.addDuplicate(award15, award17);

		bestSourceAwards = loanList.getBestSource();
		Assert.assertTrue(bestSourceAwards.contains(award17));
		Assert.assertFalse(bestSourceAwards.contains(award15));
		
		Award award19 = TestUtils.loadAward(this.getClass(), "award19.xml");
		Award award20 = TestUtils.loadAward(this.getClass(), "award20.xml");
		Award award21 = TestUtils.loadAward(getClass(), "award21.xml");
		Award award22 = TestUtils.loadAward(getClass(), "award22.xml");
		Award award23 = TestUtils.loadAward(getClass(), "award23.xml");
		
		loanList = new LoanList();
		loanList.addUnique(award19);
		loanList.addDuplicate(award19, award20);
		
		bestSourceAwards = loanList.getBestSource();
		Assert.assertTrue(bestSourceAwards.contains(award19));
		Assert.assertFalse(bestSourceAwards.contains(award20));
		
		loanList = new LoanList();
		loanList.addUnique(award19);
		loanList.addDuplicate(award19, award21);
		
		bestSourceAwards = loanList.getBestSource();
		Assert.assertTrue(bestSourceAwards.contains(award21));
		Assert.assertFalse(bestSourceAwards.contains(award19));
		
		loanList = new LoanList();
		loanList.addUnique(award19);
		loanList.addDuplicate(award19, award22);
		
		bestSourceAwards = loanList.getBestSource();
		Assert.assertTrue(bestSourceAwards.contains(award22));
		Assert.assertFalse(bestSourceAwards.contains(award19));
		
		loanList = new LoanList();
		loanList.addUnique(award19);
		loanList.addDuplicate(award19, award23);
		
		bestSourceAwards = loanList.getBestSource();
		Assert.assertTrue(bestSourceAwards.contains(award19));
		Assert.assertFalse(bestSourceAwards.contains(award23));
	}

}
