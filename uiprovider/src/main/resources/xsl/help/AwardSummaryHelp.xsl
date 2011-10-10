<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
	<xsl:template name="award-summary-help">
		<h3>Help</h3>
		<p>This document provides an English description of each of the data elements on the screen that you are viewing.  If you require additional information regarding the loan that you are viewing, please contact the source of the data.</p>
		<table cellpadding="0" cellspacing="0" class="tblHelp">
			<tbody>
				<tr>
					<td class="tdHelp1">Student's Name</td>
					<td class="tdHelp2">First, middle initial and last name of the student attending school including student SSN.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Borrower's Name</td>
					<td class="tdHelp2">First, middle initial and last name of the borrower including borrower SSN.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Loan Status</td>
					<td class="tdHelp2">The Loan Status depicts the current award phase.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Type</td>
					<td class="tdHelp2">The type of financial aid being reported.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Amount</td>
					<td class="tdHelp2">The amount of aid awarded to the recipient.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Begin Date</td>
					<td class="tdHelp2">The beginning date of the award. For FFELP loans, this is the loan period begin date. </td>
				</tr>
				<tr>
					<td class="tdHelp1">End Date</td>
					<td class="tdHelp2">The ending date of the award. For FFELP loans, this is the loan period end date. </td>
				</tr>
				<tr>
					<td class="tdHelp1">Entity Information</td>
					<td class="tdHelp2">
						<p>A star(*) denotes the source of the data displayed. Click on each link to gain demographic information on:</p>
						<p>S - Schools</p>
						<p>LO - Lenders</p>
						<p>G - Guarantors</p>
						<p>LRS - Lender Servicers</p>
						<p>GSP - Grant and Scholarship Providers</p>
						<p>FAT - Financial Aid Trascript Providers</p>
					</td>
				</tr>
				<tr>
					<td class="tdHelp1">Messages</td>
					<td class="tdHelp2">Messages display a text sent by an entity that pertains to a loan or account. </td>
				</tr>
			</tbody>				
		</table>
	</xsl:template>
	                
</xsl:stylesheet>