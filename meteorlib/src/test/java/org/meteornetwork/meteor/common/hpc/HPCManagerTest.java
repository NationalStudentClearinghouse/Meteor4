package org.meteornetwork.meteor.common.hpc;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.cxf.common.util.Base64Exception;
import org.custommonkey.xmlunit.Diff;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.xml.sax.SAXException;

import util.TestUtils;

public class HPCManagerTest {

	private HPCManager hpcManager;

	public HPCManagerTest() {
		hpcManager = new HPCManager();
	}

	@Test
	public void testRetrieveHPCContent1() throws IOException, MarshalException, ValidationException, Base64Exception, SAXException {

		String expected = TestUtils.getFileString(this.getClass(), "hpcRequest1-expected.xml");
		String rawHPCMessage = TestUtils.getFileString(this.getClass(), "hpcRequest1.xml");
		String content = hpcManager.retrieveHPCContent(rawHPCMessage);
		
		Diff hpcDiff = new Diff(expected, content);
		if (!hpcDiff.identical()) {
			TestUtils.printDifferences("testRetrieveHPCContent1()", hpcDiff);
		}
		Assert.assertTrue(hpcDiff.identical());
	}
	
	@Test
	public void testRetrieveHPCContent2() throws IOException, MarshalException, ValidationException, Base64Exception, SAXException {

		String expected = TestUtils.getFileString(this.getClass(), "hpcRequest2-expected.xml");
		String rawHPCMessage = TestUtils.getFileString(this.getClass(), "hpcRequest2.xml");
		String content = hpcManager.retrieveHPCContent(rawHPCMessage);
		
		Diff hpcDiff = new Diff(expected, content);
		if (!hpcDiff.identical()) {
			TestUtils.printDifferences("testRetrieveHPCContent2()", hpcDiff);
		}
		Assert.assertTrue(hpcDiff.identical());
	}

}
