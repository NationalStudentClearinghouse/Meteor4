<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   
   <xsl:template name="consolidated-help">
   	  <div class="helpContent">
	      <h3>Help</h3>
	      <p>This document provides an English description of each of the data elements on the screen that you are viewing.  If you require additional information regarding the loan that you are viewing, please contact the source of the data.</p>
	      <table cellpadding="0" cellspacing="0" class="tblHelp">
	         <tbody>
	            <tr>
	               <td class="tdHelp1">Student Name</td>
	               <td class="tdHelp2">First, middle initial, last name and Social Security Number of the student.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Award Summary</b>
	               </td>
	               <td class="tdHelp2"/>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Type</td>
	               <td class="tdHelp2">The type of financial aid being reported.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Status</td>
	               <td class="tdHelp2">The current status of the award on the data provider's system.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Status Date</td>
	               <td class="tdHelp2">The date of the award status.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Amount</td>
	               <td class="tdHelp2">The amount of aid scheduled to be received or has been received.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Award Detail</b>
	               </td>
	               <td class="tdHelp2"></td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Address</td>
	               <td class="tdHelp2">The address, city, state and zip code of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Phone</td>
	               <td class="tdHelp2">The phone number of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award ID</td>
	               <td class="tdHelp2">The award identifier.  For FFELP, this is the CommonLine Unique ID plus the CommonLine Loan Sequence Number.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Grad Date</td>
	               <td class="tdHelp2">The anticipated or actual date the student will complete the program at the school.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Type</td>
	               <td class="tdHelp2">The type of financial aid being reported.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Amount</td>
	               <td class="tdHelp2">The amount of aid scheduled to be received or has been received.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Cancelled Amount</td>
	               <td class="tdHelp2">The sum of the gross amount of the cancellation amounts of the previously reported disbursements that were cancelled or reduced.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Repaid Principal</td>
	               <td class="tdHelp2">The amount of principal that has been repaid by the borrower.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Capitalized Int.</td>
	               <td class="tdHelp2">The total amount of the accumulated capitalized interest for the life of the loan.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award Begin Date</td>
	               <td class="tdHelp2">The beginning date of the award.  For FFELP loans, this is the loan period begin date.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award End Date</td>
	               <td class="tdHelp2">The end date of the award.  For FFELP loans, this is the loan period end date.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Grade Level</td>
	               <td class="tdHelp2">The student's grade level at the time the award was certified by the school.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">MPN</td>
	               <td class="tdHelp2">MPN on file indicator.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">E-Signature</td>
	               <td class="tdHelp2">Indicates whether the promissory note was e-signed.  Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Loan Date</td>
	               <td class="tdHelp2">The date the guarantor or insurer issued the 'guarantee' for a loan.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Disbursement Detail</b>
	               </td>
	               <td class="tdHelp2"></td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Address</td>
	               <td class="tdHelp2">The address, city, state and zip code of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Phone</td>
	               <td class="tdHelp2">The phone number of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award ID</td>
	               <td class="tdHelp2">The award identifier.  For FFELP, this is the CommonLine Unique ID plus the CommonLine Loan Sequence Number.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Disbursement Sequence Number</td>
	               <td class="tdHelp2">An award may have multiple disbursements.  The sequence number is the chronological occurrence of the disbursement or scheduled disbursement.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Scheduled Date</td>
	               <td class="tdHelp2">The date the monies are scheduled to be funded.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Actual Date</td>
	               <td class="tdHelp2">The true date the loan monies were funded.  This date can differ from the scheduled disbursement date.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Net Amount</td>
	               <td class="tdHelp2">The amount (net fees) disbursed.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Hold Release</td>
	               <td class="tdHelp2">Indicates whether the school requested the disbursement be held or released.  Valid values: Hold / Release.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Repayment Detail</b>
	               </td>
	               <td class="tdHelp2"></td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Address</td>
	               <td class="tdHelp2">The address, city, state and zip code of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Phone</td>
	               <td class="tdHelp2">The phone number of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award ID</td>
	               <td class="tdHelp2">The award identifier.  For FFELP, this is the CommonLine Unique ID plus the CommonLine Loan Sequence Number.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Original Account Balance</td>
	               <td class="tdHelp2">The sum of the gross amount of the disbursements that the borrower is scheduled to receive and/or has received.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Capitalized Interest</td>
	               <td class="tdHelp2">The total amount of the accumulated capitalized interest for the life of the loan.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Accrued Interest</td>
	               <td class="tdHelp2">The outstanding interest since the last time interest was satisfied either through a borrower payment, capitalization or government subsidy.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Most Recent Payments</td>
	               <td class="tdHelp2">The amount of the borrower's last payment.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Other Fees Outstanding</td>
	               <td class="tdHelp2">The grand total of all fees for this loan that are outstanding from the point that the last payment was made.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Outstanding Balance</td>
	               <td class="tdHelp2">The loan balance which includes principal and accrued interest.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Current Interest Rate</td>
	               <td class="tdHelp2">The current rate at which the borrower's loan accrues interest.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Payment Plan</td>
	               <td class="tdHelp2">The loan payment plan.  Payment Plan values are: Standard, Graduated, Income-sensitive and Extended.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Payment Begin Date</td>
	               <td class="tdHelp2">The expected date the borrower must begin paying for student loan monies borrowed.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Next Payment Due</td>
	               <td class="tdHelp2">The minimum payment amount due with the next payment.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Next Payment Due Date</td>
	               <td class="tdHelp2">The actual date when the next expected payment is due.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Account Balance</td>
	               <td class="tdHelp2">The loan balance which includes principal and accrued interest.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Reference Detail</b>
	               </td>
	               <td class="tdHelp2"></td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Address</td>
	               <td class="tdHelp2">The address, city, state and zip code of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Phone</td>
	               <td class="tdHelp2">The phone number of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award ID</td>
	               <td class="tdHelp2">The award identifier.  For FFELP, this is the CommonLine Unique ID plus the CommonLine Loan Sequence Number.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Reference Name</td>
	               <td class="tdHelp2">First, middle initial and last name of the reference.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Address</td>
	               <td class="tdHelp2">The address of the reference.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Phone</td>
	               <td class="tdHelp2">The phone number of the reference.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Most Recent Deferment/Forbearance</b>
	               </td>
	               <td class="tdHelp2"></td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Address</td>
	               <td class="tdHelp2">The address, city, state and zip code of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Phone</td>
	               <td class="tdHelp2">The phone number of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award ID</td>
	               <td class="tdHelp2">The award identifier.  For FFELP, this is the CommonLine Unique ID plus the CommonLine Loan Sequence Number.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Type</td>
	               <td class="tdHelp2">The type of deferement, waiver or forbearance granted to the borrower.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Begin Date</td>
	               <td class="tdHelp2">The date the deferment, waiver or forbearance began.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">End Date</td>
	               <td class="tdHelp2">The date the deferment, waiver or forbearance ended.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">
	                  <b>Default Details</b>
	               </td>
	               <td class="tdHelp2"></td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Address</td>
	               <td class="tdHelp2">The address, city, state and zip code of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Entity Phone</td>
	               <td class="tdHelp2">The phone number of the data provider.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Award ID</td>
	               <td class="tdHelp2">The award identifier.  For FFELP, this is the CommonLine Unique ID plus the CommonLine Loan Sequence Number.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Default</td>
	               <td class="tdHelp2">Indicates if the loan is in default. Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Satisfactory Payment Arrangements</td>
	               <td class="tdHelp2">Indicates whether the borrower has made satisfactory payment arrangements. Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Eligibility Reinstated</td>
	               <td class="tdHelp2">Indicates whether the borrower's Title IV aid has been reinstated. Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Reinstatement Date</td>
	               <td class="tdHelp2">The date the borrower's Title IV aid was reinstated.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Default Aversion Requested</td>
	               <td class="tdHelp2">Indicates whether default aversion has been requested. Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Requested Date</td>
	               <td class="tdHelp2">The date default aversion was requested.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Claim Filed</td>
	               <td class="tdHelp2">Indicates whether a claim had been filed with the guarantor. Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Claim Filed Date</td>
	               <td class="tdHelp2">The date the claim was filed with the guarantor.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Claim Paid</td>
	               <td class="tdHelp2">Indicates whether the guarantor has paid the claim. Valid values are yes / no.</td>
	            </tr>
	            <tr>
	               <td class="tdHelp1">Claim Paid Date</td>
	               <td class="tdHelp2">The date the claim was paid by the guarantor.</td>
	            </tr>
	         </tbody>
	      </table>
      </div>
   </xsl:template>
</xsl:stylesheet>