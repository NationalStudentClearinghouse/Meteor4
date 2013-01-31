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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.xml.transform.TransformerConfigurationException;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;

public class TestUtils {

	private TestUtils() {
	}
	
	@SuppressWarnings("rawtypes")
	public static Award loadAward(Class thisClass, String fileName) throws TransformerConfigurationException, IOException, MarshalException, ValidationException {
		String award = getXmlFromFile(thisClass, fileName);
		return Award.unmarshal(new StringReader(award));
	}

	@SuppressWarnings("rawtypes")
	private static File getFile(Class thisClass, String fileName) throws IOException, TransformerConfigurationException {
		return new File(thisClass.getResource(fileName).getFile());
	}

	@SuppressWarnings("rawtypes")
	private static String getXmlFromFile(Class thisClass, String fileName) throws IOException, TransformerConfigurationException {
		File file = getFile(thisClass, fileName);
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
