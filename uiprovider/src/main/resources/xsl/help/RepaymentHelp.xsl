<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
	<xsl:template name="repayment-help">
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
					<td class="tdHelp1">Repayment Plan</td>
					<td class="tdHelp2">The repayment plan to which the loan is currently subscribed</td>
				</tr>
				<tr>
					<td class="tdHelp1">Repayment Starting Date</td>
					<td class="tdHelp2">The date when the borrower must begin paying for student loan monies borrowed.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Next Payment Due Date</td>
					<td class="tdHelp2">The Actual Date when the next expected payment is due for payment to the servicer or lender.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Amount Due</td>
					<td class="tdHelp2">The minimum expected Payment Amount due with the next payment.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Grand Total Original Account Balance</td>
					<td class="tdHelp2">The original amount total for all loans in servicing. </td>
				</tr>
				<tr>
					<td class="tdHelp1">Grand Total Outstanding Account Balance</td>
					<td class="tdHelp2">The outstanding amount total for all loans in servicing.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Grand Total Other Fees Currently Outstanding</td>
					<td class="tdHelp2">The outstanding fees total for all loans in servicing.</td>
				</tr>
			</tbody>				
		</table>
	</xsl:template>
	                
</xsl:stylesheet>