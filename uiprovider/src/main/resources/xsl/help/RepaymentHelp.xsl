<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 2002 National Student Clearinghouse
  
  This code is part of the Meteor system as defined and specified 
  by the National Student Clearinghouse and the Meteor Sponsors.
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.
  
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
	<xsl:template name="repayment-help">
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
	</div>
	</xsl:template>
	                
</xsl:stylesheet>
