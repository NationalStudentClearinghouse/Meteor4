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

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.helpers.IOUtils;
import org.apache.xpath.XPathAPI;
import org.meteornetwork.meteor.common.util.AbstractXMLTransformInterceptor;
import org.meteornetwork.meteor.saml.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts HPC soap request to rpc encoding by hand. RPC Encoding is deprecated
 * and not supported by JAX-WS, or any web services framework that implememts
 * the WS-I basic profile
 * 
 * @author jlazos
 * 
 */
public class HPCRequestRPCEncodingInterceptor extends AbstractXMLTransformInterceptor {

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

			Element rawHPCMessage = (Element) XPathAPI.selectSingleNode(root, "//rawHPCMessage");
			rawHPCMessage.setAttribute("xsi:type", "xsd:string");

			return DOMUtils.domToString(doc);
		} finally {
			inputStream.close();
		}
	}

}
