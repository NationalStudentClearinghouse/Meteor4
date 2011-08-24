package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.helpers.IOUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.xml.hpc.Envelope;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCCompressionType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCEncodingType;

/**
 * Provides support for legacy HPC communication protocol
 * 
 * @author jlazos
 */
// TODO: fully support HPC (e.g. abstract encoding and decoding based on
// specified encoding/compression type)
public class HPCManager {

	/**
	 * Decode and decompress HPC content from raw HPC xml
	 * 
	 * @param hpcEnvelope
	 *            the hpc envelope containing the content element to retrieve
	 * @return
	 * @throws Base64Exception
	 *             Unable to decode
	 * @throws IOException
	 *             Unable to decompress
	 * @throws ValidationException
	 * @throws MarshalException
	 */
	public String retrieveContent(String rawHPCMessage) throws Base64Exception, IOException, MarshalException, ValidationException {
		Envelope hpcEnvelope = Envelope.unmarshal(new StringReader(rawHPCMessage));
		return decompress(decode(hpcEnvelope.getPackage().getContent().getContent()));
	}

	/**
	 * Compresses and encodes a meteor response into an HPC message
	 * 
	 * @param meteorResponse
	 * @return hpc response xml string
	 * @throws IOException
	 * @throws MarshalException
	 * @throws ValidationException
	 */
	public String generateHPCResponse(String meteorResponse) throws IOException, MarshalException, ValidationException {
		Envelope hpcEnvelope = new Envelope();

		hpcEnvelope.setPackage(new org.meteornetwork.meteor.common.xml.hpc.Package());
		hpcEnvelope.getPackage().setContent(new org.meteornetwork.meteor.common.xml.hpc.Content());
		hpcEnvelope.getPackage().getContent().setCompression(HPCCompressionType.ZLIB);
		hpcEnvelope.getPackage().getContent().setEncoding(HPCEncodingType.BASE64);

		hpcEnvelope.getPackage().getContent().setContent(encode(compress(meteorResponse)));

		StringWriter hpcResponseWriter = new StringWriter();
		hpcEnvelope.marshal(hpcResponseWriter);
		return hpcResponseWriter.toString();
	}

	private byte[] decode(String encodedContent) throws Base64Exception {
		return Base64Utility.decode(encodedContent);
	}

	private String encode(byte[] decodedContent) {
		return Base64Utility.encode(decodedContent);
	}

	private String decompress(byte[] compressedContent) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(compressedContent);
		InflaterInputStream inflater = new InflaterInputStream(input);

		return IOUtils.toString(inflater, IOUtils.UTF8_CHARSET.displayName());
	}

	private byte[] compress(String decompressedContent) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DeflaterOutputStream deflater = new DeflaterOutputStream(output);

		deflater.write(decompressedContent.getBytes(IOUtils.UTF8_CHARSET));
		deflater.close();

		return output.toByteArray();
	}
}
