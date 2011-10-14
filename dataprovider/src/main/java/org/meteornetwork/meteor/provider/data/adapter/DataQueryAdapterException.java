package org.meteornetwork.meteor.provider.data.adapter;

import org.meteornetwork.meteor.common.util.message.MeteorMessage;

public class DataQueryAdapterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1550120110570217159L;
	
	private MeteorMessage meteorError;

	public DataQueryAdapterException(MeteorMessage meteorError) {
		super();
		this.meteorError = meteorError;
	}

	public MeteorMessage getMeteorError() {
		return meteorError;
	}

	public void setMeteorError(MeteorMessage meteorError) {
		this.meteorError = meteorError;
	}
}
