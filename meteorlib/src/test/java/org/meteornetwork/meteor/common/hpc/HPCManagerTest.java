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
package org.meteornetwork.meteor.common.hpc;

import java.io.IOException;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.cxf.common.util.Base64Exception;
import org.custommonkey.xmlunit.Diff;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCCompressionType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCEncodingType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCMode;
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
	
//	Utility method to generate HPC messages for testing
//
//	@Test
//	public void testGenerateHPCMessage() throws Exception {
//		
//		String content = TestUtils.getFileString(this.getClass(), "hpcResponse.xml");
//		
//		HPCMessageParams params = new HPCMessageParams();
//		params.setCompression(HPCCompressionType.ZLIB);
//		params.setEncoding(HPCEncodingType.BASE64);
//		params.setContentType("METEORDATA");
//		params.setRecipientId("ap33");
//		params.setMessageId(UUID.fromString("d3e3a870-a3dc-4968-a078-2c55e037a18c"));
//		params.setTimestamp("Tue May 27 09:12:20 EDT 2014");
//		params.setTransactionMode(HPCMode.SYNC);
//		params.setTransactionType("METEORDATA");
//		
//		String generatedHPCMessage = hpcManager.generateHPCMessage(content, params);
//		System.out.println(generatedHPCMessage);
//	}

}
