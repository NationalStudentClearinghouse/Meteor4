package org.meteornetwork.meteor.common.hpc;

import java.util.Calendar;
import java.util.UUID;

import org.meteornetwork.meteor.common.xml.hpc.types.HPCCompressionType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCEncodingType;
import org.meteornetwork.meteor.common.xml.hpc.types.HPCMode;

public class HPCMessageParams {

	private UUID messageId = UUID.randomUUID();
	// timestamp set this way for compatibility with HPC
	private String timestamp = Calendar.getInstance().getTime().toString();

	private HPCEncodingType encoding = HPCEncodingType.BASE64;
	private HPCCompressionType compression = HPCCompressionType.ZLIB;

	private String recipientId;
	private HPCMode transactionMode = HPCMode.SYNC;
	private String transactionType = "METEORDATA";

	public UUID getMessageId() {
		return messageId;
	}

	public void setMessageId(UUID messageId) {
		this.messageId = messageId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public HPCEncodingType getEncoding() {
		return encoding;
	}

	public void setEncoding(HPCEncodingType encoding) {
		this.encoding = encoding;
	}

	public HPCCompressionType getCompression() {
		return compression;
	}

	public void setCompression(HPCCompressionType compression) {
		this.compression = compression;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public HPCMode getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(HPCMode transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

}
