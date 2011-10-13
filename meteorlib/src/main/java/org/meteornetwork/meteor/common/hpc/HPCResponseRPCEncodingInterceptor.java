package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.helpers.IOUtils;
import org.apache.xpath.XPathAPI;
import org.meteornetwork.meteor.common.util.AbstractXMLTransformInterceptor;
import org.meteornetwork.meteor.saml.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HPCResponseRPCEncodingInterceptor extends AbstractXMLTransformInterceptor {

	@Override
	protected String transformMessage(String soapMessage) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();

		ByteArrayInputStream inputStream = new ByteArrayInputStream(soapMessage.getBytes(IOUtils.UTF8_CHARSET));
		try {
			Document doc = docBuilder.parse(inputStream);
			Element root = doc.getDocumentElement();
			root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

			Element returnElem = (Element) XPathAPI.selectSingleNode(root, "//return");
			returnElem.setAttribute("xsi:type", "xsd:string");

			return DOMUtils.domToString(doc);
		} finally {
			inputStream.close();
		}
		
		
	}

}
