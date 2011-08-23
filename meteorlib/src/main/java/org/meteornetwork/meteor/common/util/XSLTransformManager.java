package org.meteornetwork.meteor.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XSLTransformManager {

	/**
	 * Transforms source XML to result using the provided template
	 * 
	 * @param source
	 *            source XML as string
	 * @param xslTemplate
	 *            template to use for transformation
	 * @return transform results as XML string
	 * @throws TransformerException
	 */
	public String transformXML(String source, Templates xslTemplate) throws TransformerException {
		Transformer transformer = xslTemplate.newTransformer();

		StreamSource streamSource = new StreamSource(new ByteArrayInputStream(source.getBytes()));

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(outStream);

		transformer.transform(streamSource, streamResult);
		return outStream.toString();
	}
}
