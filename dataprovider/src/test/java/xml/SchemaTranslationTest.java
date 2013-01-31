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
package xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.Transform;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

public class SchemaTranslationTest {

	public SchemaTranslationTest() {
		XMLUnit.setIgnoreWhitespace(true);
	}

	@Test
	public void testResponseTranslation1() throws IOException, TransformerException, ParserConfigurationException, SAXException {
		File xslFile = getFile("AP3.3.4-DP4.0.0-response.xsl");
		String source = getXmlFromFile("schema-translation-1-input.xml");

		Transform transform = new Transform(source, xslFile);
		String expected = getXmlFromFile("schema-translation-1-expected.xml");
		
		Diff xmlDiff = new Diff(expected, transform);

		if (!xmlDiff.identical()) {
			printDifferences("testResponseTranslation1()", xmlDiff);
		}
		Assert.assertTrue(xmlDiff.identical());
	}

	@Test
	public void testResponseTranslation2() throws IOException, TransformerException, ParserConfigurationException, SAXException {
		File xslFile = getFile("AP3.3.4-DP4.0.0-response.xsl");
		String source = getXmlFromFile("schema-translation-2-input.xml");

		Transform transform = new Transform(source, xslFile);
		String expected = getXmlFromFile("schema-translation-2-expected.xml");
		
		Diff xmlDiff = new Diff(expected, transform);

		if (!xmlDiff.identical()) {
			printDifferences("testResponseTranslation2()", xmlDiff);
		}
		Assert.assertTrue(xmlDiff.identical());
	}

	@Test
	public void testRequestTranslation1() throws IOException, TransformerException, SAXException {
		File xslFile = getFile("AP3.3.4-DP4.0.0-request.xsl");
		String source = getXmlFromFile("request-translation-1-input.xml");

		Transform transform = new Transform(source, xslFile);
		String expected = getXmlFromFile("request-translation-1-expected.xml");
		
		Diff xmlDiff = new Diff(expected, transform);

		if (!xmlDiff.identical()) {
			printDifferences("testResponseTranslation2()", xmlDiff);
		}
		Assert.assertTrue(xmlDiff.identical());
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

	@SuppressWarnings("unchecked")
	private void printDifferences(String testMethod, Diff xmlDiff) {
		DetailedDiff detail = new DetailedDiff(xmlDiff);
		System.out.println(testMethod + ": Differences found: ");
		for (Difference diff : (List<Difference>) detail.getAllDifferences()) {
			if (diff.isRecoverable()) {
				continue;
			}
			System.out.println("\t" + diff.getDescription() + " ... expected: " + diff.getControlNodeDetail().getValue() + " (" + diff.getControlNodeDetail().getXpathLocation() + "), actual: " + diff.getTestNodeDetail().getValue());
		}
	}
}
