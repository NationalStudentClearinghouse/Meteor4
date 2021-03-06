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
package org.meteornetwork.meteor.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

@Service
public class XSLTransformManager {

	private static final Logger LOG = LoggerFactory.getLogger(XSLTransformManager.class);

	private static final String METEOR_DEFAULT_VERSION = "3.3.4";
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	/**
	 * @param source
	 *            the xml request to get the meteor version attribute of
	 * @return the meteorVersion attribute of the MeteorDataRequest element
	 */
	public String getMeteorVersion(String source) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		XPathExpression expression;
		try {
			expression = xpath.compile("MeteorDataRequest/@meteorVersion");
			String version = expression.evaluate(new InputSource(new ByteArrayInputStream(source.getBytes())));
			return version == null || "".equals(version) ? METEOR_DEFAULT_VERSION : version;
		} catch (XPathExpressionException e) {
			LOG.error("Could not evaluate XPath expression", e);
			return METEOR_DEFAULT_VERSION;
		}
	}

	/**
	 * Transforms source XML to result using the provided template
	 * 
	 * @param source
	 *            source XML as string
	 * @param xslTemplate
	 *            template to use for transformation
	 * @return transform results as XML string
	 * @throws TransformerException
	 * @throws IOException
	 */
	public String transformXML(String source, Templates xslTemplate) throws TransformerException, IOException {
		Transformer transformer = xslTemplate.newTransformer();

		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		Date currentDate = Calendar.getInstance().getTime();
		transformer.setParameter("curdate", dateFormatter.format(currentDate));
		
		StreamSource streamSource = new StreamSource(new ByteArrayInputStream(source.getBytes()));

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			StreamResult streamResult = new StreamResult(outStream);

			transformer.transform(streamSource, streamResult);
			String result = outStream.toString();
			return result;
		} finally {
			outStream.close();
		}
	}
}
