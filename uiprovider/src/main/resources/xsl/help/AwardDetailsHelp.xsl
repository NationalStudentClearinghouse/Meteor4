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
                
	<xsl:template name="award-details-help">
	<div class="helpContent">
		<h3>Help</h3>
		<p>This document provides an English description of each of the data elements on the screen that you are viewing.  If you require additional information regarding the loan that you are viewing, please contact the source of the data.</p>
		<table cellpadding="0" cellspacing="0" class="tblHelp">
			<tbody>
				<xsl:if test="$role != 'BORROWER' and $inquiryRole != 'BORROWER'">
				<tr>
					<td class="tdHelp1">Student Name</td>
					<td class="tdHelp2">First, middle initial and last name of the student attending school including student SSN.</td>
				</tr>
				</xsl:if>
				<tr>
			      <td class="tdHelp1">Borrower Name</td>
			      <td class="tdHelp2">First, middle initial and last name of the borrower.</td>
			    </tr>
			    <tr>
			      <td class="tdHelp1">Student Name</td>
			      <td class="tdHelp2">First, middle initial and last name of the student attending school.</td>
			    </tr>
				<tr>
					<td class="tdHelp1">Borrower Information</td>
					<td class="tdHelp2">
						<p>Permanent and or temporary address of the borrower</p>
						<p>Primary borrower phone number - Includes 3-digit area code + 7-digit telephone number</p>
						<p>Borrower primary email address</p>
						<p>Drivers License number</p>
						<p>License state of issuance. The 2-character abbreviation for the state that issued the borrower&#39;s driver&#39;s license.</p>
					</td>
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
					<td class="tdHelp1">Award Type</td>
					<td class="tdHelp2">The type of financial aid being reported.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Amount</td>
					<td class="tdHelp2">The amount of aid awarded.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Cancellation Amount</td>
					<td class="tdHelp2">Gross amount of all award cancellations.</td>
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
					<td class="tdHelp1">Guaranteed Date</td>
					<td class="tdHelp2">The date the Guarantor or insurer issued the "guarantee" for specific individual or combination loan.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Graduation Date</td>
					<td class="tdHelp2">The date the student is expected to complete / exit school studies.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Repayment Begin Date</td>
					<td class="tdHelp2">Expected date that the borrower must begin paying for student loan monies borrowed.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Grade Level</td>
					<td class="tdHelp2">Student's grade level at the time the award was certified by the school.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Status</td>
					<td class="tdHelp2">The Award Status depicts the current award phase.</td>
				</tr>
				<tr>
					<td class="tdHelp1">Award Status Date</td>
					<td class="tdHelp2">The effective date of the loan status.</td>
				</tr>
				<tr>
					<td class="tdHelp1">E-Signature</td>
					<td class="tdHelp2">Indicates whether the promissory note was e-signed. Valid values are yes / no.</td>
				</tr>
			</tbody>				
		</table>
	</div>
	</xsl:template>
	                
</xsl:stylesheet>
