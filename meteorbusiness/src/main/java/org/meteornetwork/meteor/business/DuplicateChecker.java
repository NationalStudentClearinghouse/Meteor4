package org.meteornetwork.meteor.business;

import java.util.Calendar;

import org.exolab.castor.types.Date;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.Disbursement;
import org.meteornetwork.meteor.common.xml.dataresponse.types.DataProviderTypeEnum;

public class DuplicateChecker {

	private Award existingAward;
	private Award newAward;

	public DuplicateChecker(Award existingAward, Award newAward) {
		this.existingAward = existingAward;
		this.newAward = newAward;
	}

	public boolean awardsAreDuplicates() {
		return step1();
	}

	/*
	 * Step 1 - Consolidation Categorization
	 * 
	 * If both AwardTypes are in the Consolidation category (FFELConsl,
	 * FFELPConsolidation, FFELPConsolidationSubsidized,
	 * FFELPConsolidationUnsubsidized, FFELPConsolidationHEAL,
	 * FFELPConsolidationOther, DLConsl, DLConsolidation,
	 * DLConsolidationSubsidized, DLConsolidationUnsubsidized, or
	 * DLConsolidationOther), proceed to Step 4 � Award Type Matching.
	 * 
	 * If not, proceed to Step 2.
	 */
	private boolean step1() {
		LoanTypeEnum existingAwardType = LoanTypeEnum.getNameIgnoreCase(existingAward.getAwardType());
		LoanTypeEnum newAwardType = LoanTypeEnum.getNameIgnoreCase(newAward.getAwardType());

		if (existingAwardType != null && newAwardType != null && existingAwardType.isConsolidation() && newAwardType.isConsolidation()) {
			return step4();
		}

		return step2();
	}

	/*
	 * Step 2 - Data Provider Type Matching
	 * 
	 * Compare the �DataProviderType� within the Award block.
	 * 
	 * If the �DataProviderType� matches then proceed to Step 3 � Data Provider
	 * Entity ID Matching.
	 * 
	 * If the �DataProviderType� does not match, then proceed to Step 4 � Award
	 * Type Matching.
	 */
	private boolean step2() {
		if (existingAward.getDataProviderType() != null && existingAward.getDataProviderType().equalsIgnoreCase(newAward.getDataProviderType())) {
			return step3();
		}

		return step4();
	}

	/*
	 * Step 3 - Data Provider Entity ID Matching
	 * 
	 * If the data provider�s �EntityId� matches, then these are not duplicates,
	 * and the process continues by comparing the next Award with the Duplicate
	 * Award Logic.
	 * 
	 * If the data provider�s �EntityId� does not match, then proceed to Step 4
	 * - Award Type Matching.
	 */
	private boolean step3() {
		assert existingAward.getDataProviderType() != null && existingAward.getDataProviderType().equalsIgnoreCase(newAward.getDataProviderType()) : "Data Provider Types are not equal";

		String existingAwardEntityId = getDataProviderEntityId(existingAward);
		String newAwardEntityId = getDataProviderEntityId(newAward);

		return existingAwardEntityId != null && existingAwardEntityId.equals(newAwardEntityId) ? false : step4();
	}

	private String getDataProviderEntityId(Award award) {
		// TODO add other elements?...
		switch (DataProviderTypeEnum.valueOf(award.getDataProviderType())) {
		case G:
			return award.getGuarantor() == null ? null : award.getGuarantor().getEntityID();
		case LRS:
			return award.getServicer() == null ? null : award.getServicer().getEntityID();
		case LO:
			return award.getLender() == null ? null : award.getLender().getEntityID();
		case S:
			return award.getSchool() == null ? null : award.getSchool().getEntityID();
		default:
			return null;
		}
	}

	/*
	 * Step 4 - Award Type Matching
	 * 
	 * If �AwardType� does not match, and both are not FFELConsl,
	 * FFELPConsolidation, FFELPConsolidationSubsidized,
	 * FFELPConsolidationUnsubsidized, FFELPConsolidationHEAL,
	 * FFELPConsolidationOther, DLConsl, DLConsolidationSubsidized,
	 * DLConsolidationUnsubsidized, or DLConsolidationOther then these are not
	 * duplicates, and the logic continues by comparing the next Award with the
	 * Duplicate Award Logic.
	 * 
	 * If �AwardType� does not match but both are FFELConsl, FFELPConsolidation,
	 * FFELPConsolidationSubsidized, FFELPConsolidationUnsubsidized,
	 * FFELPConsolidationHEAL, FFELPConsolidationOther, DLConsl,
	 * DLConsolidation, DLConsolidationSubsidized, DLConsolidationUnsubsidized,
	 * or DLConsolidationOther, then proceed to Step 5 � Disbursement Date
	 * Check.
	 * 
	 * If �AwardType� does match, then proceed to Step 6 - Award ID Matching.
	 */
	private boolean step4() {
		LoanTypeEnum existingAwardType = LoanTypeEnum.getNameIgnoreCase(existingAward.getAwardType());
		LoanTypeEnum newAwardType = LoanTypeEnum.getNameIgnoreCase(newAward.getAwardType());

		if (existingAwardType != null && existingAwardType.equals(newAwardType)) {
			return step6();
		}

		return existingAwardType != null && newAwardType != null && existingAwardType.isConsolidation() && newAwardType.isConsolidation() ? step5() : false;
	}

	/*
	 * Step 5 � Disbursement Date Check
	 * 
	 * If the �ActualDisbDt� of the awards are equal then these awards are
	 * considered to be duplicates and they are passed to the Best Source Logic.
	 * 
	 * If they are not equal, then they are not duplicates and the process
	 * continues by comparing the next Award with the Duplicate Award Logic.
	 */
	private boolean step5() {
		Date existingActualDisbDt = getActualFirstDisbDt(existingAward);
		Date newActualDisbDt = getActualFirstDisbDt(newAward);

		return existingActualDisbDt != null && existingActualDisbDt.equals(newActualDisbDt);
	}

	private Date getActualFirstDisbDt(Award award) {
		Date actualDisbDt = award.getDisbursementCount() <= 0 ? null : award.getDisbursement(0).getActualDisbDt();

		if (award.getDisbursementCount() > 0) {
			for (Disbursement disbursement : award.getDisbursement()) {
				if (disbursement.getDisbSeqNum() != null && disbursement.getDisbSeqNum().equals(1)) {
					actualDisbDt = disbursement.getActualDisbDt();
					break;
				}
			}
		}

		return actualDisbDt;
	}
	
	/*
	 * Step 6 � Award ID Matching
	 * 
	 * When �AwardType� does match, compare the characters of the �AwardId�
	 * (this is the 17 character CommonLine Unique ID for FFELP and the 21
	 * character Direct Loan Award ID for Direct Loans) for a match.
	 * 
	 * If �AwardId� is not blank or missing for both Awards and the characters
	 * do match, then these are duplicates and they are sent to Best Source
	 * Logic.
	 * 
	 * If �AwardId� is not blank or missing for both Awards and the characters
	 * do not match, then these are not duplicates and the process continues by
	 * comparing the next Award with the Duplicate Award Logic.
	 * 
	 * If �AwardId� is not present for one or both Awards, then proceed to Step
	 * 7 � Guaranty Date Matching.
	 */
	private boolean step6() {
		if (existingAward.getAwardId() == null || newAward.getAwardId() == null) {
			return step7();
		}

		return existingAward.getAwardId().equals(newAward.getAwardId());
	}

	/*
	 * Step 7 � Guarantee/LoanDate Matching
	 * 
	 * When one or more of the "AwardId�s" are missing / blank, compare the
	 * �LoanDate�for a match.
	 * 
	 * If �LoanDate� is available for both and does match, then proceed to Step
	 * 8 � School Entity ID Matching.
	 * 
	 * If �LoanDate� is available for both and does not match, then these are
	 * not duplicates and the process continues by comparing the next Award with
	 * the Duplicate Award Logic.
	 * 
	 * If one or more �LoanDate�s are missing / blank, then proceed to Step 9 �
	 * Actual First Disbursement Date Matching.
	 */
	private boolean step7() {
		if (existingAward.getLoanDt() == null || newAward.getLoanDt() == null) {
			return step9();
		}

		if (existingAward.getLoanDt().equals(newAward.getLoanDt())) {
			return step8();
		} else {
			return false;
		}
	}

	/*
	 * Step 8 � School Entity ID Matching
	 * 
	 * If �EntityID� is not blank or missing for both and matches, then these
	 * Awards should be moved to Step 12 � PLUS Award Matching.
	 * 
	 * If �EntityId� is not blank or missing for both, and does not match, then
	 * these are not duplicates and the process continues by comparing the next
	 * Award with the Duplicate Award Logic.
	 * 
	 * If �EntityId� is not present for one or both Awards, then proceed to Step
	 * 9 � Actual First Disbursement Date Matching.
	 */
	private boolean step8() {
		String existingEntityId = existingAward.getSchool() == null ? null : existingAward.getSchool().getEntityID();
		String newEntityId = newAward.getSchool() == null ? null : newAward.getSchool().getEntityID();

		if (existingEntityId == null || newEntityId == null) {
			return step9();
		}

		return existingEntityId.equals(newEntityId) ? step12() : false;
	}

	/*
	 * Step 9 � Actual First Disbursement Date Matching
	 * 
	 * If the �ActualDisbDt� is available for both (disbursement with
	 * �DisbSeqNum� equal to �01�) and does match or is within 10 days of each
	 * other, then proceed to Step 10 � School Entity ID Matching.
	 * 
	 * If the �ActualDisbDt� is available for both and does not match or is not
	 * within 10 days of each other, then these are not duplicates and the
	 * process continues by comparing the next Award with the Duplicate Award
	 * Logic.
	 * 
	 * If the �ActualDisbDt� is not present for one or both Awards then proceed
	 * to Step 11 � Scheduled First Disbursement Date Matching.
	 */
	private boolean step9() {
		Date existingActualDisbDt = getActualFirstDisbDt(existingAward);
		Date newActualDisbDt = getActualFirstDisbDt(newAward);

		if (existingActualDisbDt == null || newActualDisbDt == null) {
			return step11();
		}

		return within10Days(existingActualDisbDt, newActualDisbDt) ? step10() : false;
	}

	private boolean within10Days(Date date1, Date date2) {
		Calendar date1Cal = date1.toCalendar();
		Calendar date2Cal = date2.toCalendar();

		date1Cal.add(Calendar.DAY_OF_YEAR, 10);
		if (date2Cal.after(date1Cal)) {
			return false;
		} else {
			date1Cal.add(Calendar.DAY_OF_YEAR, -20);
			return !date2Cal.before(date1Cal);
		}
	}

	/*
	 * Step 10 � School Entity ID Matching
	 * 
	 * If �EntityId� is available for both and does match, then proceed to Step
	 * 12 � PLUS Award Matching.
	 * 
	 * If �EntityId� is available for both and does not match, then these are
	 * not duplicates and the process continues by comparing the next Award with
	 * the Duplicate Award Logic.
	 * 
	 * If �EntityId� is not present for one or both Awards, then proceed to Step
	 * 12 � PLUS Award Matching.
	 */
	private boolean step10() {
		String existingEntityId = existingAward.getSchool() == null ? null : existingAward.getSchool().getEntityID();
		String newEntityId = newAward.getSchool() == null ? null : newAward.getSchool().getEntityID();

		if (existingEntityId == null || newEntityId == null) {
			return step12();
		}

		return existingEntityId.equals(newEntityId) ? step12() : false;
	}

	/*
	 * Step 11 � Scheduled First Disbursement Date Matching
	 * 
	 * If �SchedDisbDt� is available for both and does match or is within 10
	 * days of each other, then go back to Step 10 � School Entity ID Matching.
	 * 
	 * If �SchedDisbDt� is available for both and does not match or is not
	 * within 10 days of each other, then these are not duplicates and the
	 * process continues by comparing the next Award with the Duplicate Award
	 * Logic.
	 * 
	 * If �SchedDisbDt� is not present for one or both Awards, then consider
	 * both as non-duplicates and the process continues by comparing the next
	 * Award with the Duplicate Award Logic.
	 */
	private boolean step11() {
		Date existingSchedDisbDt = existingAward.getDisbursementCount() <= 0 ? null : existingAward.getDisbursement(0).getSchedDisbDt();
		Date newSchedDisbDt = newAward.getDisbursementCount() <= 0 ? null : newAward.getDisbursement(0).getSchedDisbDt();

		if (existingSchedDisbDt == null || newSchedDisbDt == null) {
			return false;
		}

		return within10Days(existingSchedDisbDt, newSchedDisbDt) ? step10() : false;
	}

	/*
	 * Step 12 � PLUS Award Matching
	 * 
	 * If the �AwardType� for both Awards is not equal to �FFELPLUS� or
	 * �DLPLUS�, then assume the Awards are duplicates and move them to the Best
	 * Source Logic.
	 * 
	 * If the �AwardType� is equal to �FFELPLUS� or �DLPLUS�, then proceed to
	 * Step 13 � Student SSN Matching.
	 */
	private boolean step12() {
		LoanTypeEnum existingAwardType = LoanTypeEnum.getNameIgnoreCase(existingAward.getAwardType());
		LoanTypeEnum newAwardType = LoanTypeEnum.getNameIgnoreCase(newAward.getAwardType());

		return isPlusAward(existingAwardType) || isPlusAward(newAwardType) ? step13() : true;
	}

	private boolean isPlusAward(LoanTypeEnum awardType) {
		if (awardType == null) {
			return false;
		}

		switch (awardType) {
		// TODO what other awards are PLUS awards?
		case FFELPLUS:
		case DLPLUS:
			return true;
		default:
			return false;
		}
	}

	/*
	 * Step 13 � Student SSN Matching
	 * 
	 * If the student�s �SSNum� is available for both Awards and does match,
	 * then proceed to Step 14 � Borrower SSN Matching.
	 * 
	 * If the student�s �SSNum� is available for both Awards and does not match,
	 * then these are not duplicates and the process continues by comparing the
	 * next Award with the Duplicate Award Logic.
	 * 
	 * If the student�s �SSNum� is not present for one or both Awards, then
	 * assume the Awards are not duplicates and continue the process by
	 * comparing the next Award with the Duplicate Award Logic.
	 */
	private boolean step13() {
		String existingSSNum = existingAward.getStudent() == null ? null : existingAward.getStudent().getSSNum();
		String newSSNum = newAward.getStudent() == null ? null : newAward.getStudent().getSSNum();

		return existingSSNum != null && existingSSNum.equals(newSSNum) && step14();
	}

	/*
	 * Step 14 � Borrower SSN Matching
	 * 
	 * If the borrower�s �SSNum� is available for both Awards and does match,
	 * then assume the Awards are duplicates and move them to the Best Source
	 * Logic.
	 * 
	 * If the borrower�s �SSNum� is available for both and does not match, then
	 * these are not duplicates and the process continues by comparing the next
	 * Award with the Duplicate Award Logic.
	 * 
	 * If the borrower�s �SSNum� is not present for one or both Awards, then
	 * assume the Awards are not duplicates and continue the process by
	 * comparing the next Award with the Duplicate Award Logic.
	 */
	private boolean step14() {
		String existingSSNum = existingAward.getBorrower() == null ? null : existingAward.getBorrower().getSSNum();
		String newSSNum = newAward.getBorrower() == null ? null : newAward.getBorrower().getSSNum();

		return existingSSNum != null && existingSSNum.equals(newSSNum);
	}

	public Award getExistingAward() {
		return existingAward;
	}

	public void setExistingAward(Award existingAward) {
		this.existingAward = existingAward;
	}

	public Award getNewAward() {
		return newAward;
	}

	public void setNewAward(Award newAward) {
		this.newAward = newAward;
	}

}
