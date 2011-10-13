package org.meteornetwork.meteor.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

@Service
public class XSLTransformManager {

	private static final Log LOG = LogFactory.getLog(XSLTransformManager.class);

	private static final String METEOR_DEFAULT_VERSION = "3.3.4";

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
			return null;
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
