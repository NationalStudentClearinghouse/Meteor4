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
package org.meteornetwork.meteor.business;

public enum LoanTypeEnum {

	FFELCONSL("FFELConsl"),
	FFELCSUB("FFELCSub"),
	FFELCUSUB("FFELCUsub"),
	FFELCHEAL("FFELCHeal"),
	FFELCOTHR("FFELCOthr"),

	FFELPLUS("FFELPLUS"),
	FFELSUB("FFELSub"),
	FFELUNSUB("FFELUnSub"),
	FFELGB("FFELGB"),

	SLS("SLS"),
	ALTLOAN("AltLoan"),
	HEAL("HEAL"),

	DLCONSL("DLConsl"),
	DLCSUB("DLCSub"),
	DLCUSUB("DLCUsub"),
	DLCHEAL("DLCHeal"),
	DLCOTHR("DLCOthr"),

	DLSUB("DLSub"),
	DLUNSUB("DLUnsub"),
	DLPLUS("DLPLUS"),
	DLGB("DLGB"),

	PERKINS("Perkins"),
	
	FWSP("FWSP"),
	SEOG("SEOG"),
	CWC("CWC"),
	PELL("Pell"),
	OTHER("Other"),
	STATEGRNT("StateGrnt"),
	STATESCHL("StateSchl");

	private String name;

	private LoanTypeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static LoanTypeEnum getNameIgnoreCase(String name) {
		for (LoanTypeEnum value : LoanTypeEnum.values()) {
			if (value.getName().equalsIgnoreCase(name)) {
				return value;
			}
		}

		return null;
	}

	/**
	 * Returns true if loan type is a consolidation loan
	 * 
	 * @param type
	 * @return true if loan type is a consolidation loan
	 */
	public static boolean isConsolidation(LoanTypeEnum type) {
		switch (type) {
		case FFELCONSL:
		case FFELCSUB:
		case FFELCUSUB:
		case FFELCHEAL:
		case FFELCOTHR:
		case DLCONSL:
		case DLCSUB:
		case DLCUSUB:
		case DLCHEAL:
		case DLCOTHR:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true if loan type is a grant/scholarship loan
	 * 
	 * @param type
	 * @return true if loan type is a consolidation loan
	 */
	public static boolean isGrantScholarship(LoanTypeEnum type) {
		switch (type) {
		case FWSP:
		case SEOG:
		case PELL:
		case STATEGRNT:
		case STATESCHL:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true if loan type is a plus loan
	 * 
	 * @param type
	 * @return true if loan type is a plus loan
	 */
	public static boolean isPlus(LoanTypeEnum type) {
		switch (type) {
		case FFELPLUS:
		case DLPLUS:
			return true;
		default:
			return false;
		}
	}
}
