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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exolab.castor.types.Date;
import org.meteornetwork.meteor.common.xml.dataresponse.Award;
import org.meteornetwork.meteor.common.xml.dataresponse.CollectionCosts;
import org.meteornetwork.meteor.common.xml.dataresponse.LateFees;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataAggregates;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.dataresponse.OriginalBalanceGrandTotal;
import org.meteornetwork.meteor.common.xml.dataresponse.OtherFees;
import org.meteornetwork.meteor.common.xml.dataresponse.OtherFeesOutstandingGrandTotal;
import org.meteornetwork.meteor.common.xml.dataresponse.OutstandingBalanceGrandTotal;
import org.meteornetwork.meteor.common.xml.dataresponse.ServicingFees;

/**
 * Calculates Original Balance, Outstanding Balance, and Total Other fees for
 * the Repayment summary screen.
 * 
 * @author jlazos
 * 
 */
public class GrandTotalCalculator {

	private final MeteorRsMsg responseData;
	private final String borrowerSsn;

	private transient List<Date> consolidationLoanDates;
	private transient List<Award> awards = new ArrayList<Award>();
	private transient Map<String, OriginalBalanceGrandTotal> originalBalanceMap = new HashMap<String, OriginalBalanceGrandTotal>();
	private transient Map<String, OutstandingBalanceGrandTotal> outstandingBalanceMap = new HashMap<String, OutstandingBalanceGrandTotal>();
	private transient Map<String, OtherFeesOutstandingGrandTotal> otherFeesMap = new HashMap<String, OtherFeesOutstandingGrandTotal>();

	private static final long TWO_HUNDRED_TEN_DAYS_MILLIS = 18144000000L;

	private enum PaidLoanStatus {
		PC("PC"), PN("PN"), DP("DP"), PF("PF"), DN("DN");

		private final String code;

		private PaidLoanStatus(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public static PaidLoanStatus getByCodeIgnoreCase(String name) {
			for (PaidLoanStatus value : PaidLoanStatus.values()) {
				if (value.getCode().equalsIgnoreCase(name)) {
					return value;
				}
			}

			return null;
		}
	}

	public GrandTotalCalculator(MeteorRsMsg responseData, String borrowerSsn) {
		this.responseData = responseData;
		this.borrowerSsn = borrowerSsn;
	}
	
	public void calculate() {
		prepare();
		calculateTotals();
	}

	private void prepare() {
		if (responseData.getMeteorDataAggregates() == null) {
			responseData.setMeteorDataAggregates(new MeteorDataAggregates());
		}

		Set<String> servicerIds = new HashSet<String>();
		for (MeteorDataProviderInfo info : responseData.getMeteorDataProviderInfo()) {
			if (info.getMeteorDataProviderAwardDetails() != null) {
				for (Award award : info.getMeteorDataProviderAwardDetails().getAward()) {
					awards.add(award);
					if (award.getServicer() != null && award.getServicer().getEntityID() != null) {
						servicerIds.add(award.getServicer().getEntityID());
					}
				}
			}
		}

		consolidationLoanDates = new ArrayList<Date>();
		for (Award award : awards) {
			LoanTypeEnum awardType = LoanTypeEnum.getNameIgnoreCase(award.getAwardType());
			if (awardType != null && LoanTypeEnum.isConsolidation(awardType)) {
				Date date = getConsolidationLoanDate(award);
				if (date != null) {
					consolidationLoanDates.add(date);
				}
			}
		}

		for (String id : servicerIds) {
			OriginalBalanceGrandTotal originalBalance = new OriginalBalanceGrandTotal("0");
			originalBalance.setServicerID(id);

			OutstandingBalanceGrandTotal outstandingBalance = new OutstandingBalanceGrandTotal("0");
			outstandingBalance.setServicerID(id);

			OtherFeesOutstandingGrandTotal otherFeesBalance = new OtherFeesOutstandingGrandTotal("0");
			otherFeesBalance.setServicerID(id);

			responseData.getMeteorDataAggregates().addOriginalBalanceGrandTotal(originalBalance);
			responseData.getMeteorDataAggregates().addOutstandingBalanceGrandTotal(outstandingBalance);
			responseData.getMeteorDataAggregates().addOtherFeesOutstandingGrandTotal(otherFeesBalance);
			originalBalanceMap.put(id, originalBalance);
			outstandingBalanceMap.put(id, outstandingBalance);
			otherFeesMap.put(id, otherFeesBalance);
		}
	}

	private Date getConsolidationLoanDate(Award consolidationAward) {
		Date date = consolidationAward.getAwardBeginDt();
		if (date == null) {
			date = consolidationAward.getLoanDt();
		}
		if (date == null) {
			date = consolidationAward.getLoanStatDt();
		}
		return date;
	}

	private void calculateTotals() {
		for (Award award : awards) {
			addToOriginalBalance(award);
			addToOutstandingBalance(award);
			addToOtherFees(award);
		}
	}

	/*
	 * Calculates the grand total original balance and stores it in the meteor
	 * response data. The calculation is performed as follows:
	 * 
	 * Sums all awards where all of the following conditions are met:
	 * 
	 * 1) Borrower ssn = queried ssn
	 * 
	 * 2) If the loan status is paid (PC, PN, DP, PF, DN), no consolidation loan
	 * in the ENTIRE RESPONSE has an award begin date less than 210 days away
	 * from the award's loan status date. (ENTIRE RESPONSE means all
	 * consolidation loans returned from all data providers are checked.) If a
	 * consolidation loan has no award begin date, loan date is checked. If no
	 * loan date, loan status date.
	 */
	private void addToOriginalBalance(Award award) {
		if (award.getBorrower() != null && borrowerSsn.equals(award.getBorrower().getSSNum().getContent()) && !consolLoanWithinDaysOfPaidLoanStatDt(award) && !isGrantScholarship(award) && award.getServicer() != null && award.getServicer().getEntityID() != null) {
			BigDecimal toAdd = award.getGrossLoanAmount() == null ? (award.getAwardAmt() == null ? BigDecimal.ZERO : award.getAwardAmt()) : award.getGrossLoanAmount();
			OriginalBalanceGrandTotal total = originalBalanceMap.get(award.getServicer().getEntityID());
			total.setContent(toAdd.add(total.getContent()));
		}
	}

	private boolean isGrantScholarship(Award award) {
		LoanTypeEnum loanType = LoanTypeEnum.getNameIgnoreCase(award.getAwardType());
		return loanType != null && LoanTypeEnum.isGrantScholarship(loanType);
	}

	private boolean consolLoanWithinDaysOfPaidLoanStatDt(Award award) {
		if (PaidLoanStatus.getByCodeIgnoreCase(award.getLoanStat()) == null) {
			return false;
		}

		Date loanStatDt = award.getLoanStatDt();

		if (consolidationLoanDates != null && loanStatDt != null) {
			long loanStatDtLong = loanStatDt.toLong();
			for (Date date : consolidationLoanDates) {
				if (Math.abs(date.toLong() - loanStatDtLong) < TWO_HUNDRED_TEN_DAYS_MILLIS) {
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * Calculates the grand total outstanding balance and stores it in the
	 * meteor data provider info set in this object. The calculation sums up the
	 * Repyament/AcctBal elements of all awards where borrower ssn = queried ssn
	 */
	private void addToOutstandingBalance(Award award) {
		if (award.getBorrower() != null && borrowerSsn.equals(award.getBorrower().getSSNum().getContent()) && !isGrantScholarship(award) && award.getServicer() != null && award.getServicer().getEntityID() != null) {
			if (award.getRepayment() != null && award.getRepayment().getAcctBal() != null) {
				OutstandingBalanceGrandTotal total = outstandingBalanceMap.get(award.getServicer().getEntityID());
				total.setContent(total.getContent().add(award.getRepayment().getAcctBal()));
			}
		}
	}

	/*
	 * Calculates the grand total other fees and stores it in the meteor data
	 * provider info set in this object. The calculation sums up all of the
	 * following fees:
	 * 
	 * 1) Repayment/LateFees/LateFeesAmount
	 * 
	 * 2) Repayment/CollectionCosts/CollectionCostsAmount
	 * 
	 * 3) Repayment/ServicingFees/ServicingFeesAmount
	 * 
	 * 4) Repayment/OtherFees
	 */
	private void addToOtherFees(Award award) {

		if (award.getBorrower() != null && borrowerSsn.equals(award.getBorrower().getSSNum().getContent()) && award.getRepayment() != null && !isGrantScholarship(award) && award.getServicer() != null && award.getServicer().getEntityID() != null) {
			OtherFeesOutstandingGrandTotal total = otherFeesMap.get(award.getServicer().getEntityID());

			if (award.getRepayment().getLateFeesCount() > 0) {
				for (LateFees fees : award.getRepayment().getLateFees()) {
					if(null != fees.getLateFeesAmount()) {
						total.setContent(total.getContent().add(fees.getLateFeesAmount()));
					}
				}
			}

			if (award.getRepayment().getCollectionCostsCount() > 0) {
				for (CollectionCosts fees : award.getRepayment().getCollectionCosts()) {
					if(null != fees.getCollectionCostsAmount()) {
						total.setContent(total.getContent().add(fees.getCollectionCostsAmount()));
					}
				}
			}

			if (award.getRepayment().getServicingFeesCount() > 0) {
				for (ServicingFees fees : award.getRepayment().getServicingFees()) {
					if(null != fees.getServicingFeesAmount()) {
						total.setContent(total.getContent().add(fees.getServicingFeesAmount()));
					}
				}
			}

			if (award.getRepayment().getOtherFeesCount() > 0) {
				for (OtherFees fees : award.getRepayment().getOtherFees()) {
					if(null != fees.getOtherFeesAmount()) {
						total.setContent(total.getContent().add(fees.getOtherFeesAmount()));
					}
				}
			}
		}
	}

	public MeteorRsMsg getResponseData() {
		return responseData;
	}

	public String getBorrowerSsn() {
		return borrowerSsn;
	}
}
