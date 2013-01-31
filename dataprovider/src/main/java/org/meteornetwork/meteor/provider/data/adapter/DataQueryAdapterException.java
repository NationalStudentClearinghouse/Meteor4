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
