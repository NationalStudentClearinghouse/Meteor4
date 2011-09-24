package org.meteornetwork.meteor.common.hpc;

import java.io.IOException;

import org.apache.cxf.common.util.Base64Exception;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;

import util.TestUtils;

public class HPCManagerTest {

	private HPCManager hpcManager;

	public HPCManagerTest() {
		hpcManager = new HPCManager();
	}

	@Test
	public void testRetrieveHPCContent() throws IOException, MarshalException, ValidationException, Base64Exception {
		String rawHPCMessage = TestUtils.getTestFile(this.getClass(), "hpcRequest1.xml");
		hpcManager.retrieveHPCContent(rawHPCMessage);
		
		rawHPCMessage = TestUtils.getTestFile(this.getClass(), "hpcRequest2.xml");
		hpcManager.retrieveHPCContent(rawHPCMessage);
	}

}
