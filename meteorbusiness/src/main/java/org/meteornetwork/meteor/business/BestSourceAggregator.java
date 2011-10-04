package org.meteornetwork.meteor.business;

import java.util.List;

import org.meteornetwork.meteor.common.xml.dataresponse.Award;

public class BestSourceAggregator {

	private LoanList loanList = new LoanList();

	/**
	 * Adds award to this instance, checking if it is a duplicate of a
	 * previously added award. If it is, best source logic is applied.
	 * 
	 * @param award
	 *            the award to add
	 */
	public void add(Award award) {
		for (Award existingAward : loanList.getAll()) {
			DuplicateChecker dupeChecker = new DuplicateChecker(existingAward, award);
			if (dupeChecker.awardsAreDuplicates()) {
				loanList.addDuplicate(existingAward, award);
				return;
			}
		}

		loanList.addUnique(award);
	}

	/**
	 * Gets the best sources of the awards that have been added to this instance
	 * 
	 * @return the best sources of the awards that have been added to this
	 *         instance
	 */
	public List<Award> getBest() {
		return loanList.getBestSource();
	}

	/**
	 * Gets the best source of the awards that are duplicates of the specified
	 * award
	 * 
	 * @param apsUniqueAwardId
	 *            the APSUniqueAwardID of the award to get the best source
	 *            duplicate of
	 * @return the best source of the awards that are duplicates of the
	 *         specified award
	 */
	public Award getBest(Integer apsUniqueAwardId) {
		List<Award> duplicates = getDuplicates(apsUniqueAwardId);
		return duplicates == null ? null : duplicates.get(0);
	}

	/**
	 * Gets the duplicates of the specified award
	 * 
	 * @param apsUniqueAwardId
	 *            the APSUniqueAwardID of the award to get the duplicates of
	 * @return the duplicates of the specified award
	 */
	public List<Award> getDuplicates(Integer apsUniqueAwardId) {
		return loanList.getDuplicates(apsUniqueAwardId);
	}

	/**
	 * Gets all awards that were added to this instance.
	 * 
	 * @return All awards that have been added to this instance
	 */
	public List<Award> getAll() {
		return loanList.getAll();
	}
}
