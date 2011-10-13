package org.meteornetwork.meteor.saml.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;

public class DOMUtils {

	private DOMUtils() {
	}

	public static String domToString(Node dom) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			XMLUtils.outputDOM(dom, outputStream);
			String xml = outputStream.toString();

			return xml;
		} finally {
			outputStream.close();
		}
	}
}
