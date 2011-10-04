package org.meteornetwork.meteor.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Assert;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;

public class DuplicateCheckerTest {

	@Test
	public void testAwardsAreDuplicates() throws MarshalException, ValidationException, TransformerConfigurationException, IOException {
		Award award1 = loadAward("award1.xml");
		Award award2 = loadAward("award2.xml");

		Assert.assertTrue(runDuplicateChecker(award1, award2));

		Award award3 = loadAward("award3.xml");
		Assert.assertFalse(runDuplicateChecker(award1, award3));
		Assert.assertTrue(runDuplicateChecker(award2, award3));

		Award award4 = loadAward("award4.xml");
		Assert.assertFalse(runDuplicateChecker(award1, award4));

		Award award5 = loadAward("award5.xml");
		Assert.assertFalse(runDuplicateChecker(award4, award5));

		Award award6 = loadAward("award6.xml");
		Assert.assertTrue(runDuplicateChecker(award4, award6));
		Assert.assertTrue(runDuplicateChecker(award5, award6));

		Award award7 = loadAward("award7.xml");
		Assert.assertFalse(runDuplicateChecker(award4, award7));
		Assert.assertFalse(runDuplicateChecker(award5, award7));
		Assert.assertFalse(runDuplicateChecker(award6, award7));

		Award award8 = loadAward("award8.xml");
		Assert.assertFalse(runDuplicateChecker(award4, award8));
		Assert.assertFalse(runDuplicateChecker(award5, award8));
		Assert.assertFalse(runDuplicateChecker(award6, award8));
		Assert.assertFalse(runDuplicateChecker(award7, award8));

		Award award9 = loadAward("award9.xml");
		Assert.assertTrue(runDuplicateChecker(award8, award9));

		Award award10 = loadAward("award10.xml");
		Assert.assertTrue(runDuplicateChecker(award1, award10));
		Assert.assertFalse(runDuplicateChecker(award2, award10));

		Award award11 = loadAward("award11.xml");
		Assert.assertFalse(runDuplicateChecker(award7, award11));

		Award award12 = loadAward("award12.xml");
		Assert.assertFalse(runDuplicateChecker(award7, award12));

		Award award13 = loadAward("award13.xml");
		Assert.assertFalse(runDuplicateChecker(award10, award13));

		Award award14 = loadAward("award14.xml");
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

	private Award loadAward(String fileName) throws TransformerConfigurationException, IOException, MarshalException, ValidationException {
		String award = getXmlFromFile(fileName);
		return Award.unmarshal(new StringReader(award));
	}

	private File getFile(String fileName) throws IOException, TransformerConfigurationException {
		return new File(this.getClass().getResource(fileName).getFile());
	}

	private String getXmlFromFile(String fileName) throws IOException, TransformerConfigurationException {
		File file = getFile(fileName);
		FileInputStream fileInputStream = new FileInputStream(file);
		try {
			FileChannel fc = fileInputStream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.forName("utf-8").decode(bb).toString();
		} finally {
			fileInputStream.close();
		}
	}
}
