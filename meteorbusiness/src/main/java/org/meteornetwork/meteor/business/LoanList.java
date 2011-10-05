package org.meteornetwork.meteor.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.exolab.castor.types.Date;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.Disbursement;
import org.meteornetwork.meteor.common.xml.dataresponse.types.DataProviderTypeEnum;
import org.meteornetwork.meteor.common.xml.dataresponse.types.DisbStatCdEnum;

/**
 * Groups awards by duplicates and applies best source logic to each group
 * 
 * @author jlazos
 * 
 */
public class LoanList {

	private enum DisbursementStatusEnum {
		UNDISBURSED, PARTIALLY_DISBURSED, FULLY_DISBURSED, FULLY_CANCELLED
	}

	private List<LinkedList<Award>> duplicateGroups;
	private Map<Integer, LinkedList<Award>> awardDuplicateGroupMap;

	public LoanList() {
		duplicateGroups = new LinkedList<LinkedList<Award>>();
		awardDuplicateGroupMap = new HashMap<Integer, LinkedList<Award>>();
	}

	/**
	 * Adds award to its own duplicate group, assuming award is not a duplicate
	 * of a previously added award.
	 * 
	 * @param award
	 *            The award to add
	 */
	public void addUnique(Award award) {
		assert award.getAPSUniqueAwardID() != null : "APSUniqueAwardID is null";

		LinkedList<Award> newDuplicateList = new LinkedList<Award>();
		newDuplicateList.add(award);
		duplicateGroups.add(newDuplicateList);
		awardDuplicateGroupMap.put(award.getAPSUniqueAwardID(), newDuplicateList);
	}

	/**
	 * Adds a duplicate award to the duplicate group of the existing award it is
	 * a duplicate of. Inserts the new award into the duplicate list according
	 * to best source logic
	 * 
	 * @param existingAward
	 *            the award that already exists in this LoanList instance and
	 *            the newAward is a duplicate of
	 * @param newAward
	 *            the award to add
	 */
	public void addDuplicate(Award existingAward, Award newAward) {
		assert existingAward.getAPSUniqueAwardID() != null : "existingAward APSUniqueAwardID is null";
		assert newAward.getAPSUniqueAwardID() != null : "newAward APSUniqueAwardID is null";

		LinkedList<Award> duplicateList = awardDuplicateGroupMap.get(existingAward.getAPSUniqueAwardID());
		assert duplicateList != null : "Duplicate List does not exist for existingAward";

		addWithBestSourceLogic(duplicateList, newAward);
		awardDuplicateGroupMap.put(newAward.getAPSUniqueAwardID(), duplicateList);
	}

	private void addWithBestSourceLogic(LinkedList<Award> duplicateList, Award award) {

		if (LoanTypeEnum.getNameIgnoreCase(award.getAwardType()) == null || duplicateList.isEmpty()) {
			duplicateList.addLast(award);
			return;
		}

		Award bestAward = duplicateList.getFirst();

		Date loanDt = getLoanDate(bestAward, award);
		DisbursementStatusEnum disbursementStatus = getDisbursementStatus(bestAward, award);
		Date claimPdDt = getClaimPaidDate(bestAward, award);

		/*
		 * Pre-guarantee
		 * 
		 * Best source order is LO, S, G, LRS, FAT
		 */
		if (loanDt == null && DisbursementStatusEnum.UNDISBURSED.equals(disbursementStatus)) {
			addWithBestSource(duplicateList, bestAward, award, DataProviderTypeEnum.LO, DataProviderTypeEnum.S, DataProviderTypeEnum.G, DataProviderTypeEnum.LRS, DataProviderTypeEnum.FAT);
			return;
		}

		/*
		 * Pre-default
		 * 
		 * If fully disbursed, best source order is LRS, G, LO, S, FAT
		 * 
		 * If not fully disbursed, best source order is LO, LRS, G, S, FAT
		 */
		if (claimPdDt == null) {
			if (DisbursementStatusEnum.FULLY_DISBURSED.equals(disbursementStatus)) {
				addWithBestSource(duplicateList, bestAward, award, DataProviderTypeEnum.LRS, DataProviderTypeEnum.G, DataProviderTypeEnum.LO, DataProviderTypeEnum.S, DataProviderTypeEnum.FAT);
			} else {
				addWithBestSource(duplicateList, bestAward, award, DataProviderTypeEnum.LO, DataProviderTypeEnum.LRS, DataProviderTypeEnum.G, DataProviderTypeEnum.S, DataProviderTypeEnum.FAT);
			}
			return;
		}

		/*
		 * Default
		 * 
		 * Best source order is G, LRS, LO, FAT, S
		 */
		addWithBestSource(duplicateList, bestAward, award, DataProviderTypeEnum.G, DataProviderTypeEnum.LRS, DataProviderTypeEnum.LO, DataProviderTypeEnum.FAT, DataProviderTypeEnum.S);
	}

	private Date getLoanDate(Award bestAward, Award award) {
		return bestAward.getLoanDt() == null ? award.getLoanDt() : bestAward.getLoanDt();
	}

	private Date getClaimPaidDate(Award bestAward, Award award) {
		if (bestAward.getDefault() != null && bestAward.getDefault().getClaimPdDt() != null) {
			return bestAward.getDefault().getClaimPdDt();
		}

		if (award.getDefault() != null && award.getDefault().getClaimPdDt() != null) {
			return award.getDefault().getClaimPdDt();
		}

		return null;
	}

	private DisbursementStatusEnum getDisbursementStatus(Award bestAward, Award award) {
		DisbursementStatusEnum bestAwardDisbStatus = determineDisbursementStatus(bestAward);
		DisbursementStatusEnum awardDisbStatus = determineDisbursementStatus(award);

		if (DisbursementStatusEnum.UNDISBURSED.equals(bestAwardDisbStatus) || DisbursementStatusEnum.UNDISBURSED.equals(awardDisbStatus)) {
			return DisbursementStatusEnum.UNDISBURSED;
		}

		if (DisbursementStatusEnum.PARTIALLY_DISBURSED.equals(bestAwardDisbStatus) || DisbursementStatusEnum.PARTIALLY_DISBURSED.equals(awardDisbStatus)) {
			return DisbursementStatusEnum.PARTIALLY_DISBURSED;
		}

		if (DisbursementStatusEnum.FULLY_DISBURSED.equals(bestAwardDisbStatus) || DisbursementStatusEnum.FULLY_DISBURSED.equals(awardDisbStatus)) {
			return DisbursementStatusEnum.FULLY_DISBURSED;
		}

		return DisbursementStatusEnum.FULLY_CANCELLED;
	}

	private DisbursementStatusEnum determineDisbursementStatus(Award award) {
		int disbCancelled = 0;
		int disbNotDisbursed = 0;
		int disbDisbursed = 0;

		if (award.getDisbursementCount() == 0) {
			return DisbursementStatusEnum.FULLY_CANCELLED;
		}

		for (Disbursement disbursement : award.getDisbursement()) {
			{
				if (disbursement.getDisbStatCd() == null) {
					continue;
				}

				if (DisbStatCdEnum.D.name().equals(disbursement.getDisbStatCd())) {
					++disbDisbursed;
					continue;
				}

				if (DisbStatCdEnum.C.name().equals(disbursement.getDisbStatCd())) {
					++disbCancelled;
					continue;
				}

				if (DisbStatCdEnum.A.name().equals(disbursement.getDisbStatCd())) {
					++disbNotDisbursed;
				}
			}
		}

		if (disbCancelled == award.getDisbursementCount()) {
			return DisbursementStatusEnum.FULLY_CANCELLED;
		}

		if (disbDisbursed + disbCancelled == award.getDisbursementCount()) {
			return DisbursementStatusEnum.FULLY_DISBURSED;
		}

		if (disbNotDisbursed == award.getDisbursementCount()) {
			return DisbursementStatusEnum.UNDISBURSED;
		}

		return DisbursementStatusEnum.PARTIALLY_DISBURSED;
	}

	private void addWithBestSource(LinkedList<Award> duplicateList, Award bestAward, Award award, DataProviderTypeEnum... dataProviderTypes) {
		DataProviderTypeEnum awardDpType = award.getDataProviderType() == null ? null : DataProviderTypeEnum.fromValue(award.getDataProviderType());
		DataProviderTypeEnum bestAwardDpType = bestAward.getDataProviderType() == null ? null : DataProviderTypeEnum.fromValue(bestAward.getDataProviderType());

		boolean isBestSource = false;
		for (DataProviderTypeEnum dataProviderType : dataProviderTypes) {
			if (dataProviderType.equals(awardDpType)) {
				isBestSource = true;
				break;
			}

			if (dataProviderType.equals(bestAwardDpType)) {
				break;
			}
		}

		if (isBestSource) {
			duplicateList.addFirst(award);
		} else {
			duplicateList.addLast(award);
		}
	}

	/**
	 * @return the best source awards from each group of duplicates
	 */
	public List<Award> getBestSource() {
		List<Award> bestSourceAwards = new ArrayList<Award>();
		for (List<Award> duplicateGroup : duplicateGroups) {
			Award bestSource = duplicateGroup.get(0);
			LoanTypeEnum awardType = LoanTypeEnum.getNameIgnoreCase(bestSource.getAwardType());

			/*
			 * If award type is consolidation, get other consolidation loans
			 * from same data provider
			 */
			if (awardType != null && LoanTypeEnum.isConsolidation(awardType)) {
				bestSourceAwards.addAll(getConsolidationLoansWithSameDataProvider(bestSource, duplicateGroup));
			} else {
				bestSourceAwards.add(bestSource);
			}
		}
		return bestSourceAwards;
	}

	/*
	 * The best source rules are used to select a single loan to be displayed on
	 * the Award Summary amongst the duplicate loans when comparing award types
	 * with the exception of Consolidation award types. For Consolidation award
	 * types, because Data Providers may respond with a single “rolled-up”
	 * Consolidation Award type (FFELCONSOL) loan or multiple individual loan
	 * groups (e.g. FFELCONSOLSUB,
	 * FFELCONSOLUNSUB,FFELCONSOLHEAL,FFELCONSOLOTHER) in a single response, the
	 * best source rules should select all loans from the best source data
	 * provider. E.g., Data Provider ONE responds with a single FFELCONSOL loan,
	 * and DATA Provider TWO responds with 2 loans (FFELCONSOLSUB and
	 * FFELCONSOLUNSUB); all three having been identified as duplicates because
	 * they have the same disbursement date; either the single loan from Data
	 * Provider ONE, or, both loans from Data Provider TWO will be displayed as
	 * determined by the best source logic.
	 */
	private List<Award> getConsolidationLoansWithSameDataProvider(Award bestSource, List<Award> duplicateGroup) {
		List<Award> consolidationLoans = new ArrayList<Award>();
		consolidationLoans.add(bestSource);

		for (Award award : duplicateGroup) {
			if (award.equals(bestSource)) {
				continue;
			}

			LoanTypeEnum awardType = LoanTypeEnum.getNameIgnoreCase(award.getAwardType());
			if (awardType != null && LoanTypeEnum.isConsolidation(awardType) && award.getDataProviderType() != null && award.getDataProviderType().equals(bestSource.getDataProviderType())) {
				consolidationLoans.add(award);
			}
		}

		return consolidationLoans;
	}

	/**
	 * @param apsUniqueAwardId
	 *            the APSUniqueAwardID of the award to get duplicates of
	 * @return the group of duplicates of the specified award id
	 */
	public List<Award> getDuplicates(Integer apsUniqueAwardId) {
		return awardDuplicateGroupMap.get(apsUniqueAwardId);
	}

	/**
	 * @return all awards that have been added to this LoanList instance
	 */
	public List<Award> getAll() {
		List<Award> allAwards = new ArrayList<Award>();

		for (List<Award> duplicateGroup : duplicateGroups) {
			for (Award award : duplicateGroup) {
				allAwards.add(award);
			}
		}

		return allAwards;
	}
}
