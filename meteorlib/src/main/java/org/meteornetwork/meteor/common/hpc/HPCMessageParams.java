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
	private String contentType = transactionType;

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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
