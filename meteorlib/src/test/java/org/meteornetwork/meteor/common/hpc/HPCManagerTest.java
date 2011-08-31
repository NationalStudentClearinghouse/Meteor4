package org.meteornetwork.meteor.common.hpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.helpers.IOUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;

public class HPCManagerTest {

	private HPCManager hpcManager;

	public HPCManagerTest() {
		hpcManager = new HPCManager();
	}

	@Test
	public void testRetrieveHPCContent() throws IOException, MarshalException, ValidationException, Base64Exception {
		String rawHPCMessage = getTestXml("hpcRequest1.xml");
		System.out.println(hpcManager.retrieveHPCContent(rawHPCMessage));
	}

	private String getTestXml(String fileName) throws IOException {
		File hpcRequestFile = new File(this.getClass().getResource(fileName).getFile());
		InputStream hpcRequestStream = new FileInputStream(hpcRequestFile);
		return IOUtils.toString(hpcRequestStream, IOUtils.UTF8_CHARSET.displayName());
	}

}
