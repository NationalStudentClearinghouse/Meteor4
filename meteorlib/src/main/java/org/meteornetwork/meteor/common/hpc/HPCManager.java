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
import org.meteornetwork.meteor.common.xml.hpc.Content;
import org.meteornetwork.meteor.common.xml.hpc.Envelope;
import org.meteornetwork.meteor.common.xml.hpc.Message;
import org.meteornetwork.meteor.common.xml.hpc.Recipient;
import org.meteornetwork.meteor.common.xml.hpc.Sender;
import org.meteornetwork.meteor.common.xml.hpc.Transaction;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCCompressionType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCEncodingType;
import org.springframework.stereotype.Service;

/**
 * Provides compatibility support for legacy HPC communication protocol
 * 
 * @author jlazos
 */
@Service
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
	public String retrieveHPCContent(String rawHPCMessage) throws Base64Exception, IOException, MarshalException, ValidationException {
		Envelope hpcEnvelope = Envelope.unmarshal(new StringReader(rawHPCMessage));

		Content hpcContent = hpcEnvelope.getPackage().getContent();

		byte[] contentBytes = HPCEncodingType.BASE64.equals(hpcContent.getEncoding()) ? decode(hpcContent.getContent()) : hpcContent.getContent().getBytes();
		return HPCCompressionType.ZLIB.equals(hpcContent.getCompression()) ? decompress(contentBytes) : new String(contentBytes);
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
	public String generateHPCMessage(String content, HPCMessageParams messageParams) throws IOException, MarshalException, ValidationException {
		Envelope hpcEnvelope = new Envelope();

		hpcEnvelope.setMessage(new Message());
		hpcEnvelope.getMessage().setID(messageParams.getMessageId().toString());
		hpcEnvelope.getMessage().setTimestamp(messageParams.getTimestamp());

		hpcEnvelope.setSender(new Sender());
		hpcEnvelope.setRecipient(new Recipient());
		hpcEnvelope.getRecipient().setID(messageParams.getRecipientId());

		hpcEnvelope.setTransaction(new Transaction());
		hpcEnvelope.getTransaction().setType(messageParams.getTransactionType());
		hpcEnvelope.getTransaction().setMode(messageParams.getTransactionMode());

		hpcEnvelope.setPackage(new org.meteornetwork.meteor.common.xml.hpc.Package());
		hpcEnvelope.getPackage().setContent(new org.meteornetwork.meteor.common.xml.hpc.Content());
		hpcEnvelope.getPackage().getContent().setCompression(messageParams.getCompression());
		hpcEnvelope.getPackage().getContent().setEncoding(messageParams.getEncoding());
		hpcEnvelope.getPackage().getContent().setType(messageParams.getContentType());

		byte[] contentBytes = HPCCompressionType.ZLIB.equals(messageParams.getCompression()) ? compress(content) : content.getBytes();
		hpcEnvelope.getPackage().getContent().setContent(HPCEncodingType.BASE64.equals(messageParams.getEncoding()) ? encode(contentBytes) : new String(contentBytes));

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
		try {
			return IOUtils.toString(inflater, IOUtils.UTF8_CHARSET.displayName());
		} finally {
			inflater.close();
		}
	}

	private byte[] compress(String decompressedContent) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DeflaterOutputStream deflater = new DeflaterOutputStream(output);

		try {
			deflater.write(decompressedContent.getBytes(IOUtils.UTF8_CHARSET));
		} finally {
			deflater.close();
		}

		return output.toByteArray();
	}
}
