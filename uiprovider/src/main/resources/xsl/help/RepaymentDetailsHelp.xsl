<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
	<xsl:template name="repayment-details-help">
	<div class="helpContent">
		<h3>Help</h3>
		<p>This document provides an English description of each of the data elements on the screen that you are viewing.  If you require additional information regarding the loan that you are viewing, please contact the source of the data.</p>
		<table cellpadding="0" cellspacing="0" class="tblHelp">
			<tbody>
				<tr>
					<td class="tdHelp1">Loan Status</td>
					<td class="tdHelp2">The Loan Status depicts the current loan phase.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Type</td>
					<td class="tdHelp2">The type of financial aid being reported.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Begin Date</td>
					<td class="tdHelp2">The beginning date of the award. For FFELP loans, this is the loan period begin date.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award End Date</td>
					<td class="tdHelp2">The ending date of the award. For FFELP loans, this is the loan period end date.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Data Provider</td>
					<td class="tdHelp2">The entity that provided the best source of the information displayed. Click on the name for more details.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Data Provider Type</td>
					<td class="tdHelp2">Providers of Meteor Data can include:
						<p>S - Schools</p>
						<p>LO - Lenders</p>
						<p>G - Guarantors</p>
						<p>LRS - Lender Repayment Servicers</p>
						<p>GSP - Grant and Scholarship Providers</p>
						<p>FAT - Financial Aid Trascript Providers</p>
					</td>
				</tr>
				<tr>
					<td class="tdHelp1">Repayment Term Remaining</td>
					<td class="tdHelp2">The number of months remaining in the repayment term.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Current Monthly Payment</td>
					<td class="tdHelp2">The amount the borrower is currently expected to pay on a monthly basis as determined by their current repayment plan.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Original Account Balance</td>
					<td class="tdHelp2">The gross amount of aid initially awarded to the borrower.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Capitalized Interest</td>
					<td class="tdHelp2">The total amount of the accumulated capitalized interest for the life of the loan.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Accrued Interest</td>
					<td class="tdHelp2">The outstanding interest since the last time interest was satisfied (either through a borrower payment, capitalization or government subsidy).</td>
				</tr>
				<tr>
					<td class="tdHelp1">Most Recent Payment</td>
					<td class="tdHelp2">Amount of the Last Payment</td>
				</tr>
				<tr> 
					<td class="tdHelp1">Other Fees Outstanding</td>
					<td class="tdHelp2">Grand total of all fees for this loan that are outstanding from the point that the last payment was made.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Outstanding Account Balance</td>
					<td class="tdHelp2">Total current balance of the loan. This amount includes principal and accrued interest less payments received</td>
				</tr>
				<tr> 
					<td class="tdHelp1">Current Interest Rate</td>
					<td class="tdHelp2">The interest rate at which a borrower's loan accrues interest.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Payment Plan</td>
					<td class="tdHelp2">Payment Plan Type Indictor</td>
				</tr>
				<tr>
					<td class="tdHelp1">Payment Begin Date</td>
					<td class="tdHelp2">The date when the borrower must begin paying for student loan monies borrowed.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Next Payment Due</td>
					<td class="tdHelp2">The minimum expected Payment Amount due with the next payment.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Next Payment Due Date</td>
					<td class="tdHelp2">The Actual Date when the next expected payment is due for payment to the servicer or lender.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Account Balance Date</td>
					<td class="tdHelp2">The date used to calculate the Outstanding Loan Balance</td>
				</tr>
				<tr>
					<td class="tdHelp1">Most Recent deferment/waiver/forbearance</td>
					<td class="tdHelp2">Displays the borrower's most recent approved instance of deferment, waiver, or forbearance.</td>
				</tr>
			</tbody>				
		</table>
	</div>
	</xsl:template>
	                
</xsl:stylesheet>