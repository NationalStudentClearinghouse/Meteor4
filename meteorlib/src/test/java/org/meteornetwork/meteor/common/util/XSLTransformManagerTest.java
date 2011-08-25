package org.meteornetwork.meteor.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XSLTransformManagerTest {

	private XSLTransformManager xslTransformManager = new XSLTransformManager();
	
	@Test
	public void testGetMeteorVersion() {
		String xml = "<MeteorDataRequest meteorVersion=\"4.1.3\"><AccessProvider /><SSN /></MeteorDataRequest>";
		assertEquals("4.1.3", xslTransformManager.getMeteorVersion(xml));
		
		xml = "<MeteorDataRequest><AccessProvider /><SSN /></MeteorDataRequest>";
		assertEquals("3.3.4", xslTransformManager.getMeteorVersion(xml));
	}

}
