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
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
    	            
	<xsl:include href="../layout-master.xsl"/>
	<xsl:include href="../help/ConsolidatedViewHelp.xsl"/>
	
	<xsl:param name="apsuniqueawardid"/>

	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>Consolidated View</title>
		
		<script type="text/javascript">		
			$(document).ready( function() {
				$('.hideShow').hide();
			
				$('a.triggerA').click(function(e){
					var $str = '.';
					var $str2 = $(this).attr("id");
					var $showMe = $str+$str2;
					$($showMe).fadeToggle("fast");
					if ($(this).hasClass("show")) {
						$(this).removeClass("show").addClass("close");
					} else {
						$(this).removeClass("close").addClass("show");
					}
					
					e.preventDefault ? e.preventDefault() : e.returnValue = false;
				});
				
				$('a#expandAll').click( function(e) {
					$('.hideShow').toggle(true);
					$('a.triggerA').removeClass("show").addClass("close");
					
					e.preventDefault ? e.preventDefault() : e.returnValue = false;
				});
				
				$('a#collapseAll').click( function(e) {
					$('.hideShow').toggle(false);
					$('a.triggerA').removeClass("close").addClass("show");
					
					e.preventDefault ? e.preventDefault() : e.returnValue = false;
				});
			});	
		</script>
	</xsl:template>
	
	<xsl:template name="subnavigation">
		<xsl:call-template name="subnavigation-award-detail">
			<xsl:with-param name="awardId" select="$apsuniqueawardid"/>
		</xsl:call-template>
	</xsl:template>
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:variable name="borrower-awards" select="//Award[Borrower/SSNum/@unmasked = $ssn]"/>
	<xsl:variable name="student-awards" select="//Award[Student/SSNum/@unmasked = $ssn]"/>
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Consolidated<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="consolidated-help"/>
		</div>
		
		<p class="intro">The Award summary, Award Detail and Disbursement Detail blocks only display awards where the student's SSN matches the SSN entered on the Meteor Query screen. For example, PLUS loans where the SSN queried is not the student's SSN will show no information in the Award or Disbursement blocks, however information on these loans may be available within the other blocks on this screen.</p>
		
		<div class="toggleControls">
			<a href="#" id="expandAll"><img src="{$docroot}/imgs/expandall.png" border="0"/> <span class="toggleControlText">Expand All</span></a><a href="#" id="collapseAll"><img src="{$docroot}/imgs/expandall-close.png"/> <span class="toggleControlText">Collapse All</span></a>
		</div>
		
		<p class="tableTitle">Award Summary <span class="expander"><a href="#" class="triggerA show" id="triggerA1">&#32;</a></span></p> 
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA1">
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$student-awards"/>
			</xsl:call-template>
			<tbody>
				<tr class="defRow">
					<td class="rowHead">Award Type </td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="AwardType"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Award Status</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="LoanStat"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award Status Date</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="LoanStatDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Award Amount </td>
					<xsl:for-each select="$student-awards">
					<td><xsl:choose>
						<xsl:when test="string(number(GrossLoanAmount)) != 'NaN'">
							<xsl:value-of select="format-number(GrossLoanAmount, '$###,##0.00')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="format-number-if-exists">
								<xsl:with-param name="number" select="AwardAmt"/>
								<xsl:with-param name="format" select="'$###,##0.00'"/>
							</xsl:call-template>
						</xsl:otherwise>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Best Source of Data</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:choose>
						<xsl:when test="APSUniqueAwardID = $apsuniqueawardid">Yes</xsl:when>
						<xsl:otherwise>No</xsl:otherwise>
					</xsl:choose></td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>
		<p class="tableTitle clr">Award Detail <span class="expander"><a href="#" class="triggerA show" id="triggerA2">&#32;</a></span></p>
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA2">
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$student-awards"/>
			</xsl:call-template>
			<tbody>
				<tr class="defRow">
					<td class="rowHead">Entity Address</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="." mode="best-source-address"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Entity Phone</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="." mode="best-source-phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award ID</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="AwardId"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Grad Date</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="Student/GradDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award Type</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="AwardType"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Award Amount</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:choose>
						<xsl:when test="string(number(GrossLoanAmount)) != 'NaN'">
							<xsl:value-of select="format-number(GrossLoanAmount, '$###,##0.00')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="format-number-if-exists">
								<xsl:with-param name="number" select="AwardAmt"/>
								<xsl:with-param name="format" select="'$###,##0.00'"/>
							</xsl:call-template>
						</xsl:otherwise>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Cancelled Amount</td>
					<xsl:for-each select="$student-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="sum(Disbursement/CancellationAmount)"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Repaid Principal</td>
					<xsl:for-each select="$student-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/RepaidPrincipalAmt"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Capitalized Int.</td>
					<xsl:for-each select="$student-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/CapitalizedIntAmt"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Award Begin Date</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="AwardBeginDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award End Date</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="AwardEndDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Grade Level</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="GradeLevelInd"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">MPN</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:choose>
						<xsl:when test="MPNInd = 'N'">No</xsl:when>
						<xsl:when test="MPNInd = 'Y'">Yes</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="MPNInd"/>
						</xsl:otherwise>
					</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">E-Signature</td>
					<xsl:for-each select="$student-awards">
					<td>
						<xsl:choose>
							<xsl:when test="Esign = 'false'"> No </xsl:when>
							<xsl:when test="Esign = 'true'"> Yes </xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="Esign"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Loan Date</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="LoanDt"/></td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>
		<p class="tableTitle clr">Disbursement Detail <span class="expander"><a href="#" class="triggerA show" id="triggerA3">&#32;</a></span></p>
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA3">
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$student-awards"/>
			</xsl:call-template>
			<tbody>	
				<tr class="defRow">
					<td class="rowHead">Entity Address</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="." mode="best-source-address"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Entity Phone</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:apply-templates select="." mode="best-source-phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award ID</td>
					<xsl:for-each select="$student-awards">
					<td><xsl:value-of select="AwardId"/></td>
					</xsl:for-each>
				</tr>
				<xsl:call-template name="Disbursement">
					<xsl:with-param name="i" select="0"/>
				</xsl:call-template>
			</tbody>
		</table>
		<p class="tableTitle clr">Repayment Detail <span class="expander"><a href="#" class="triggerA show" id="triggerA4">&#32;</a></span></p>
		
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA4">
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$borrower-awards"/>
			</xsl:call-template>
			<tbody> 
				<tr class="defRow">
					<td class="rowHead">Entity Address</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-address"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Entity Phone</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award ID</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="AwardId"/></td>
					</xsl:for-each>
				</tr> 
				<tr class="altRow">
					<td class="rowHead">Original Account Balance</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
						<xsl:when test="string(number(GrossLoanAmount)) != 'NaN'">
							<xsl:value-of select="format-number(GrossLoanAmount, '$###,##0.00')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="format-number-if-exists">
								<xsl:with-param name="number" select="AwardAmt"/>
								<xsl:with-param name="format" select="'$###,##0.00'"/>
							</xsl:call-template>
						</xsl:otherwise>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>  
				<tr class="defRow">
					<td class="rowHead">Capitalized Interest</td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/CapitalizedIntAmt"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>  
				<tr class="altRow">
					<td class="rowHead">Accrued Interest</td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/AccruedInterest"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr> 
				<tr class="defRow">
					<td class="rowHead">Most Recent Payments </td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/LastPmt/PaymentAmt"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>  
				<tr class="altRow">
					<td class="rowHead">Other Fees Outstanding</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:call-template name="fees" /></td>
					</xsl:for-each>
				</tr>  
				<tr class="defRow">
					<td class="rowHead">Outstanding Balance</td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/AcctBal"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>  
				<tr class="altRow">
					<td class="rowHead">Current Interest Rate</td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:value-of select="Repayment/CurrIntRate"/>%
					</td>
					</xsl:for-each>
				</tr> 
				<tr class="defRow">
					<td class="rowHead">Payment Plan Standard</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Repayment/PmtPlan"/></td>
					</xsl:for-each>
				</tr>  
				<tr class="altRow">
					<td class="rowHead">Payment Begin Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Repayment/PmtBeginDt"/></td>
					</xsl:for-each>
				</tr>  
				<tr class="defRow">
					<td class="rowHead">Next Payment Due</td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/NextPmtAmt"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>  
				<tr class="altRow">
					<td class="rowHead">Next Payment Due Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Repayment/NextDueDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Account Balance</td>
					<xsl:for-each select="$borrower-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Repayment/AcctBal"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>

		<p class="tableTitle clr">Borrower Demographics Detail <span class="expander"><a href="#" class="triggerA show" id="triggerA5">&#32;</a></span></p>
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA5">
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$borrower-awards"/>
			</xsl:call-template>
			<tbody>
				<tr class="defRow">
					<td class="rowHead">Borrower Address</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="Borrower/Contacts/AddressInfo"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Phone Number</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="Borrower/Contacts/Phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Email Address </td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="Borrower/Contacts/Email"/></td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>
		
		<p class="tableTitle clr">Reference Detail <span class="expander"><a href="#" class="triggerA show" id="triggerA6">&#32;</a></span></p>
		
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA6"> 
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$borrower-awards"/>
			</xsl:call-template>
			<tbody> 
				<tr class="defRow">
					<td class="rowHead">Entity Address</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-address"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Entity Phone</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award ID</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="AwardId"/></td>
					</xsl:for-each>
				</tr> 
				
				<xsl:apply-templates select="Reference" mode="consol"/>
			</tbody>
		</table>
		
		<p class="tableTitle clr">Most Recent Deferment and Forbearance Detail <span class="expander"><a href="#" class="triggerA show" id="triggerA7">&#32;</a></span></p>
		
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA7"> 
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$borrower-awards"/>
			</xsl:call-template>
			<tbody>
				<tr class="defRow">
					<td class="rowHead">Entity Address</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-address"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Entity Phone</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award ID</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="AwardId"/></td>
					</xsl:for-each>
				</tr> 
				<tr class="altRow">
					<td class="rowHead">Type</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:for-each select="Repayment/DefermentForbearance">
						<xsl:sort select="DefermentForbearanceEndDate" data-type="text" order="descending"/>
						<xsl:if test="position() = 1">
							<xsl:value-of select="DefermentForbearanceName"/>
						</xsl:if>
					</xsl:for-each></td>
					</xsl:for-each>
				</tr>  
				<tr class="defRow">
					<td class="rowHead">Begin Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:for-each select="Repayment/DefermentForbearance">
							<xsl:sort select="DefermentForbearanceEndDate" data-type="text" order="descending"/>
							<xsl:if test="position() = 1">
								<xsl:value-of select="DefermentForbearanceBeginDate"/>
							</xsl:if>
						</xsl:for-each></td>
					</xsl:for-each>
				</tr>  

				<tr class="altRow">
					<td class="rowHead">End Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:for-each select="Repayment/DefermentForbearance">
							<xsl:sort select="DefermentForbearanceEndDate" data-type="text" order="descending"/>
							<xsl:if test="position() = 1">
								<xsl:value-of select="DefermentForbearanceEndDate"/>
							</xsl:if>
						</xsl:for-each></td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>
		
		<p class="tableTitle clr">Default Aversion Request, Claim and Default Details <span class="expander"><a href="#" class="triggerA show" id="triggerA8">&#32;</a></span></p>
		
		<table cellpadding="0" cellspacing="0" class="consol hideShow triggerA8"> 
			<xsl:call-template name="provider-table-header">
				<xsl:with-param name="awards" select="$borrower-awards"/>
			</xsl:call-template>
			<tbody>
				<tr class="defRow">
					<td class="rowHead">Entity Address</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-address"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Entity Phone</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:apply-templates select="." mode="best-source-phone"/></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Award ID</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="AwardId"/></td>
					</xsl:for-each>
				</tr> 
				<tr class="altRow">
					<td class="rowHead">Default</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
							<xsl:when test="Default/Def = 'true' or Default/Def = '1'">Yes</xsl:when>
							<xsl:when test="Default/Def = 'false' or Default/Def='0'">No</xsl:when>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Satisfactory Payment Arrangements</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
							<xsl:when test="Default/SatisPmtArr = 'false' or Default/SatisPmtArr = '0'">No</xsl:when>
							<xsl:when test="Default/SatisPmtArr = 'true' or Default/SatisPmtArr = '1'">Yes</xsl:when>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Eligibility Reinstated</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
							<xsl:when test="Default/EligibilityReinstatementIndicator = 'false' or Default/EligibilityReinstatementIndicator = '0'">No</xsl:when>
							<xsl:when test="Default/EligibilityReinstatementIndicator = 'true' or Default/EligibilityReinstatementIndicator = '1'">Yes</xsl:when>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Reinstatement Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Default/EligibilityReinstatementDate"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Default Aversion Requested</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
							<xsl:when test="Default/DefAvertRq = 'false' or Default/DefAvertRq = '0'">No</xsl:when>
							<xsl:when test="Default/DefAvertRq = 'true' or Default/DefAvertRq = '1'">Yes</xsl:when>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Requested Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Default/DefAvertRqDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Claim Filed</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
							<xsl:when test="Default/ClaimFil = 'false' or Default/ClaimFil = '0'">No</xsl:when>
							<xsl:when test="Default/ClaimFil = 'true' or Default/ClaimFil = '1'">Yes</xsl:when>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Claim Filed Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Default/ClaimFilDt"/></td>
					</xsl:for-each>
				</tr>
				<tr class="altRow">
					<td class="rowHead">Claim Paid</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:choose>
							<xsl:when test="Default/ClaimPd = 'false' or Default/ClaimPd = '0'">No</xsl:when>
							<xsl:when test="Default/ClaimPd = 'true' or Default/ClaimPd = '1'">Yes</xsl:when>
						</xsl:choose></td>
					</xsl:for-each>
				</tr>
				<tr class="defRow">
					<td class="rowHead">Claim Paid Date</td>
					<xsl:for-each select="$borrower-awards">
					<td><xsl:value-of select="Default/ClaimFilDt"/></td>
					</xsl:for-each>
				</tr>
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template name="provider-table-header">
		<xsl:param name="awards"/>
		<col width="250px"/>
		<col span="{count($awards)}" width="140px"/>
		
		<thead>
			<tr>
				<th></th>
				<xsl:for-each select="$awards">
				<th>
					<xsl:apply-templates select="DataProviderType"/><br/>
					<xsl:apply-templates select="." mode="dataprovidername"/>
				</th>
				</xsl:for-each>
			</tr>
		</thead>
	</xsl:template>
	
	<xsl:template name="Disbursement">
		<xsl:param name="i"/>
		<xsl:if test="count($student-awards/Disbursement[$i+1]) > 0">
			<tr class="altRow">
				<td></td>
				<td colspan="{count($student-awards)}"></td>
			</tr>
			<tr class="altRow">
				<td class="rowHead">Disbursement <xsl:value-of select="$i+1"/> </td>
				<xsl:for-each select="$student-awards">
				<td></td>
				</xsl:for-each>
			</tr>
			<tr class="defRow">
				<td class="rowHead">Scheduled Date</td>
				<xsl:for-each select="$student-awards">
					<td>
						<xsl:value-of select="Disbursement[$i+1]/SchedDisbDt"/>
					</td>
				</xsl:for-each>
			</tr>
			<tr class="altRow">
				<td class="rowHead">Actual Date</td>
				<xsl:for-each select="$student-awards">
					<td>
						<xsl:value-of select="Disbursement[$i+1]/ActualDisbDt"/>
					</td>
				</xsl:for-each>
			</tr>
			<tr class="defRow">
				<td class="rowHead">Net Amount</td>
				<xsl:for-each select="$student-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Disbursement[$i+1]/DisbNetAmt"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
				</xsl:for-each>
			</tr>
			<tr class="altRow">
				<td class="rowHead">Cancelled Amount</td>
				<xsl:for-each select="$student-awards">
					<td>
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Disbursement[$i+1]/CancellationAmount"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
				</xsl:for-each>
			</tr>
			<tr class="defRow">
				<td class="rowHead">Cancelled Date</td>
				<xsl:for-each select="$student-awards">
				<td><xsl:value-of select="Disbursement[$i+1]/CancellationDate"/></td>
				</xsl:for-each>
			</tr>
			<tr class="altRow">
				<td class="rowHead">Status</td>
				<xsl:for-each select="$student-awards">
				<td class="data"><xsl:apply-templates select="Disbursement[$i+1]/DisbStatCd"/></td>
				</xsl:for-each>
			</tr>
			<tr class="defRow">
				<td class="rowHead">Status Date</td>
				<xsl:for-each select="$student-awards">
				<td><xsl:value-of select="Disbursement[$i+1]/DisbStatDt"/></td>
				</xsl:for-each>
			</tr>
			<tr class="altRow">
				<td class="rowHead">Hold Release Indicator</td>
				<xsl:for-each select="$student-awards">
				<td><xsl:apply-templates select="Disbursement[$i+1]/DisbHold"/></td>
				</xsl:for-each>
			</tr>
			<xsl:call-template name="Disbursement">
				<xsl:with-param name="i" select="$i+1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="fees">
		<xsl:if 
			test="count(Repayment/LateFees/LateFeesAmount) + count(Repayment/CollectionCosts/CollectionCostsAmount) + count(Repayment/ServicingFees/ServicingFeesAmount) + count(Repayment/OtherFees/OtherFeesAmount) > 0">
			<xsl:value-of 
				select="format-number(sum(Repayment/LateFees/LateFeesAmount) + sum(Repayment/CollectionCosts/CollectionCostsAmount ) + sum(Repayment/ServicingFees/ServicingFeesAmount ) + sum(Repayment/OtherFees/OtherFeesAmount ), '$###,##0.00')"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Reference" mode="consol">
		<tr class="altRow">
			<td></td>
			<td colspan="{count($borrower-awards)}"></td>
		</tr>
		<tr class="altRow">
			<td class="rowHead">Reference Name</td>
			<xsl:for-each select="$borrower-awards">
			<td><xsl:apply-templates select="." mode="fullname"/></td>
			</xsl:for-each>
		</tr>
		<tr class="defRow">
			<td class="rowHead">Reference Address</td>
			<xsl:for-each select="$borrower-awards">
			<td><xsl:apply-templates select="Contacts/AddressInfo"/></td>
			</xsl:for-each>
		</tr>
		<tr class="altRow">
			<td class="rowHead">Phone</td>
			<xsl:for-each select="$borrower-awards">
			<td><xsl:apply-templates select="Contacts/Phone"/></td>
			</xsl:for-each>
		</tr>
	</xsl:template>
</xsl:stylesheet>
