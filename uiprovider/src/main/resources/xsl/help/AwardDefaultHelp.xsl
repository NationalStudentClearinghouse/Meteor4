<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
	<xsl:template name="award-default-help">
		<div class="helpContent">
			<h3>Help</h3>
			<p>This document provides an English description of each of the data elements on the screen that you are viewing.  If you require additional information regarding the loan that you are viewing, please contact the source of the data.</p>
			<table cellpadding="0" cellspacing="0" class="tblHelp">
				<tbody>
					<tr>
						<td class="tdHelp1">Borrower Name</td>
						<td class="tdHelp2">First, middle initial, last name and Social Security Number of the borrower.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Default</td>
						<td class="tdHelp2">Indicates if the loan is in default.  Valid values are yes / no.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Satisfactory Payment Arrangements</td>
						<td class="tdHelp2">Indicates whether the borrower has made satisfactory payment arrangements.  Valid values are yes / no.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Eligibility Reinstated</td>
						<td class="tdHelp2">Indicates whether the borrower's Title IV aid has been reinstated.  Valid values are yes / no.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Reinstated Date</td>
						<td class="tdHelp2">The date the borrower's Title IV aid was reinstated.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Default Aversion Requested</td>
						<td class="tdHelp2">Indicates whether default aversion has been requested.  Valid values are yes / no.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Requested Date</td>
						<td class="tdHelp2">The date default aversion was requested.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Repayment Begin Date</td>
						<td class="tdHelp2">The date the borrower began paying for student loan monies borrowed.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Claim Filed</td>
						<td class="tdHelp2">Indicates whether a claim had been filed with the guarantor.  Valid values are yes / no.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Claim Filed Date</td>
						<td class="tdHelp2">The date the claim was filed with the guarantor.</td>
					</tr>
					<tr>
						<td class="tdHelp1">Claim Paid</td>
						<td class="tdHelp2">Indicates whether the guarantor has paid the claim.  Valid values are yes / no.</td>
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