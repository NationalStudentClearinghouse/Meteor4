package org.meteornetwork.meteor.business;

public enum LoanTypeEnum {

	// TODO: update this enum with correct loan types
	FFELCONSL("FFELConsl", "FFEL Consolidation", true),
	FFELCSUB("FFELCSub", "FFEL Consolidation Stafford Subsidized", true),
	FFELCUSUB("FFELCUsub", "FFEL Consolidation Stafford Unsubsidized", true),
	FFELCHEAL("FFELCHEAL", "FFEL Consolidation HEAL", true),
	FFELCOTHR("FFELCOthr", "FFEL Consolidation Other", true),

	FFELPLUS("FFELPLUS", "FFELP PLUS"),
	FFELSUB("FFELSub", "FFELP Subsidized"),
	FFELUNSUB("FFELUnSub", "FFELP UnSubsidized"),
	FFELPGB("FFELPGB", "FFELPGB"),

	SLS("SLS", "SLS"),
	ALTLOAN("AltLoan", "Alternative Loan"),
	HEAL("HEAL", "HEAL"),

	DLCONSL("DLConsl", "DL Consolidation", true),
	DLCONSOLIDATION("DLConsolidation", "DL Consolidation", true),
	DLCSUB("DLConsolidationSubsidized", "DL Consolidation Subsidized", true),
	DLCUSUB("DLConsolidationUnsubsidized", "DL Consolidation Unsubsidized", true),
	DLCOTHR("DLConsolidationOther", "DL Consolidation Other", true),
	
	DLPLUS("DLPLUS", "DLPLUS");

	private String name;
	private String longName;
	private boolean isConsolidation;

	private LoanTypeEnum(String name, String longName) {
		this(name, longName, false);
	}

	private LoanTypeEnum(String name, String longName, boolean isConsolidation) {
		this.name = name;
		this.longName = longName;
		this.isConsolidation = isConsolidation;
	}

	public String getName() {
		return name;
	}

	public String getLongName() {
		return longName;
	}

	public boolean isConsolidation() {
		return isConsolidation;
	}

	public static LoanTypeEnum getNameIgnoreCase(String name) {
		for (LoanTypeEnum value : LoanTypeEnum.values()) {
			if (name.equalsIgnoreCase(value.getName())) {
				return value;
			}
		}

		return null;
	}
}
