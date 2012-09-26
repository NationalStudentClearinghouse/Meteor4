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
	<xsl:include href="../help/RepaymentDetailsHelp.xsl"/>
	
	<xsl:param name="apsuniqueawardid"/>
		
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person">borrower</xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>Repayment Details</title>
	</xsl:template>
	
	<xsl:template name="subnavigation">
		<xsl:call-template name="subnavigation-award-detail">
			<xsl:with-param name="awardId" select="$apsuniqueawardid"/>
		</xsl:call-template>
	</xsl:template>
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Repayment Details<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<p class="tableTitle">Repayment Details</p>
		<table cellpadding="0" cellspacing="0" class="tblPayment">
			<thead>
				<tr>
					<th class="thPayment3" nowrap="nowrap">Award Type</th>
					<th class="thPayment4" nowrap="nowrap">Loan Status</th>
					<th class="thPayment6" nowrap="nowrap">Begin Date</th>
					<th class="thPayment7" nowrap="nowrap">End Date</th>
					<th class="thPayment8" nowrap="nowrap">Data Provider</th>
					<th class="thPayment9" nowrap="nowrap">Data Provider Type</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td class="tFooter" colspan="12">
						<p>* Please note: This is not a Payoff Amount. In order to obtain a Payoff Amount, please contact your Loan Servicer identified at the bottom of this screen. </p>
						<p>If you require additional information or feel that any of the data displayed for this award is incorrect or invalid, please contact the source of the data - <xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid]" mode="select-best-source"/></p>
					</td>
				</tr>
			</tfoot>
			<tbody>
				<xsl:apply-templates select="//Award[APSUniqueAwardID = $apsuniqueawardid and Borrower/SSNum/@unmasked = $ssn]"/>
				<xsl:if test="count(//Award[APSUniqueAwardID=$apsuniqueawardid and Borrower/SSNum/@unmasked=$ssn]) = 0">
				<tr>
					<td>No repayment information found</td>
				</tr>
				</xsl:if>
				<xsl:apply-templates select="//Repayment [../APSUniqueAwardID=$apsuniqueawardid and ../Borrower/SSNum/@unmasked=$ssn]"/>
			</tbody>
		</table>	
		<p class="tableTitle clr">Most Recent Deferment/Forbearance</p>
		<xsl:apply-templates select="//Repayment[../APSUniqueAwardID=$apsuniqueawardid and ../Borrower/SSNum/@unmasked=$ssn]/DefermentForbearance">
			<xsl:sort select="DefermentForbearanceEndDate" data-type="text" order="descending"/>
		</xsl:apply-templates>
		<xsl:if test="count(//Repayment[../APSUniqueAwardID=$apsuniqueawardid and ../Borrower/SSNum/@unmasked=$ssn]/DefermentForbearance) = 0">
			<p>No Deferment/Forbearance information found</p>
		</xsl:if>
		<br/>
		<br/>
		<br/>
		<strong>Send Payments To:</strong><br />
		<xsl:choose>
			<xsl:when test="//Award[APSUniqueAwardID=$apsuniqueawardid and Borrower/SSNum/@unmasked=$ssn]/Default/Def = 'true'">
				<xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid and Borrower/SSNum/@unmasked=$ssn]/Guarantor" mode="contact"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid and Borrower/SSNum/@unmasked=$ssn]/Servicer" mode="contact"/>
			</xsl:otherwise>
		</xsl:choose>
		
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="repayment-details-help"/>
		</div>
	</xsl:template>
	
	<xsl:template match="Award">
		<tr>
			<xsl:attribute name="class"><xsl:choose>
				<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
				<xsl:otherwise>altRow</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<td class="tdPayment3" nowrap="nowrap">
				<xsl:apply-templates select="AwardType"/>
			</td>
			<td class="tdPayment4" nowrap="nowrap">
				<xsl:apply-templates select="LoanStat"/>
			</td>
			<td class="tdPayment6" nowrap="nowrap">
				<xsl:choose>
					<xsl:when test="AwardBeginDt">
						<xsl:value-of select="AwardBeginDt"/>
					</xsl:when>
				</xsl:choose>
			</td>
			<td class="tdPayment7" nowrap="nowrap">
				<xsl:choose>
					<xsl:when test="AwardEndDt">
						<xsl:value-of select="AwardEndDt"/>
					</xsl:when>
				</xsl:choose>
			</td>
			<td class="tdPayment8" nowrap="nowrap">
				<xsl:apply-templates select="." mode="select-best-source"/>
			</td>
			<td class="tdPayment9" nowrap="nowrap">
				<xsl:apply-templates select="DataProviderType" />
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="Repayment">
		<tr>
			<td colspan="6" align="center">
				<table cellpadding="0" cellspacing="0" class="tblBorrower">
					<tbody>
						<xsl:if test="DaysPastDue">
						<tr>
							<td class="tdBorrowerPastDue" colspan="4"><strong>You are <xsl:value-of select="DaysPastDue"/> Days Past Due!</strong></td>
						</tr>
						</xsl:if>
						<tr>
							<td class="tdBorrower1">Repayment Term Remaining:</td>
							<td class="tdBorrower2"><xsl:value-of select="RepaymentTermRemaining"/></td>
							<td class="tdBorrower3">Current Monthly Payment:</td>
							<td class="tdBorrower4"><xsl:call-template name="format-number-if-exists">
								<xsl:with-param name="number" select="CurrentMonthlyPayment"/>
								<xsl:with-param name="format" select="'$###,##0.00'"/>
							</xsl:call-template></td>
						</tr>
						<tr><td class="tdBorrower1">&#32;</td></tr>
						<tr>
							<td class="tdBorrower1">Original Account Balance:</td>
							<td class="tdBorrower2"><xsl:choose>
								<xsl:when test="string(number(../GrossLoanAmount)) != 'NaN'">
									<xsl:value-of select="format-number(../GrossLoanAmount, '$###,##0.00')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="format-number-if-exists">
										<xsl:with-param name="number" select="../AwardAmt"/>
										<xsl:with-param name="format" select="'$###,##0.00'"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose></td>
							<td class="tdBorrower3">Current Interest Rate:</td>
							<td class="tdBorrower4">
								<xsl:value-of select="CurrIntRate"/>%
							</td>
						</tr>
						<tr>
							<td class="tdBorrower1">Capitalized Interest:</td>
							<td class="tdBorrower2">
								<xsl:call-template name="format-number-if-exists">
									<xsl:with-param name="number" select="CapitalizedIntAmt"/>
									<xsl:with-param name="format" select="'$###,##0.00'"/>
								</xsl:call-template>
							</td>
							<td class="tdBorrower3">Payment Plan:</td>
							<td class="tdBorrower4"><xsl:value-of select="PmtPlan"/></td>
						</tr>
						<tr>
							<td class="tdBorrower1">Accrued Interest:</td>
							<td class="tdBorrower2">
								<xsl:call-template name="format-number-if-exists">
									<xsl:with-param name="number" select="AccruedInterest"/>
									<xsl:with-param name="format" select="'$###,##0.00'"/>
								</xsl:call-template>
							</td>
							<td class="tdBorrower3">Payment Begin Date:</td>
							<td class="tdBorrower4"><xsl:value-of select="PmtBeginDt"/></td>
						</tr>
						<tr>
							<td class="tdBorrower1">Most Recent Payment:</td>
							<td class="tdBorrower2">
								<xsl:call-template name="format-number-if-exists">
									<xsl:with-param name="number" select="LastPmt/PaymentAmt"/>
									<xsl:with-param name="format" select="'$###,##0.00'"/>
								</xsl:call-template>
							</td>
							<td class="tdBorrower3">Next Payment Due:</td>
							<td class="tdBorrower4">
								<xsl:call-template name="format-number-if-exists">
									<xsl:with-param name="number" select="NextPmtAmt"/>
									<xsl:with-param name="format" select="'$###,##0.00'"/>
								</xsl:call-template>
							</td>
						</tr>
						<tr>
							<td class="tdBorrower1">Other Fees Outstanding:</td>
							<td class="tdBorrower2"><xsl:call-template name="fees"/></td>
							<td class="tdBorrower3">Next Payment Due Date:</td>
							<td class="tdBorrower4"><xsl:value-of select="NextDueDt"/></td>   
						</tr>
						<tr>
							<td class="tdBorrower1">Outstanding Account Balance:</td>
							<td class="tdBorrower2">* <xsl:call-template name="format-number-if-exists">
									<xsl:with-param name="number" select="AcctBal"/>
									<xsl:with-param name="format" select="'$###,##0.00'"/>
								</xsl:call-template>
							</td>
							<td class="tdBorrower3">Account Balance Date:</td>
							<td class="tdBorrower4"><xsl:value-of select="AcctBalDt"/></td> 
						</tr>
						<tr>
							<td class="tdBorrowerLinks" colspan="4">
							<a href="#" class="PaymentHistory"><xsl:attribute name="onclick">javascript:showModal('showHistory' + <xsl:value-of select="position()"/>, { minHeight: 170 })</xsl:attribute>View Payment History</a> | 
							<xsl:if test="count(//Repayment/Deferment) + count(//Repayment/Forbearance) > 0"><a href="#" class="CumulativeDeferment"><xsl:attribute name="onclick">javascript:showModal('showCumulativeDefForb' + <xsl:value-of select="position()"/>, { minHeight: 170 })</xsl:attribute>View Cumulative Deferement/Forbearance Time Used</a> | </xsl:if> 
							<a href="#" class="OutstandingFees"><xsl:attribute name="onclick">javascript:showModal('showOutstanding' + <xsl:value-of select="position()"/>, { minHeight: 170 })</xsl:attribute>View Outstanding Fees</a>
							</td>
						</tr>
					</tbody>
				</table>
				<div id="modal_showHistory{position()}" class="showOptions" style="width:726px;">
					<h3>Payment History</h3>
					<xsl:choose>
					<xsl:when test="count(LastPmt) + count(DefermentForbearance) = 0">
					<p>No Information Reported by this Data Provider</p>
					</xsl:when>
					<xsl:otherwise>
					<table cellpadding="0" cellspacing="0" class="simple" style="width:710px;">
						<col width="20%"/>
						<col width="20%"/>
						<col width="12%"/>
						<col width="12%"/>
						<col width="36%"/>
						
						<thead>
							<tr>
								<th style="border-bottom: none"></th>
								<th style="border-bottom: none"></th>
								<th style="border-bottom: none" colspan="3">Deferment/Forbearance</th>
							</tr>
							<tr>
								<th>Payment Amount</th>
								<th>Payment Date</th>
								<th>Begin Date</th>
								<th>End Date</th>
								<th>Deferment/Forbearance Type</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="LastPmt | DefermentForbearance">
							<xsl:sort select="PaymentDt | DefermentForbearanceBeginDate" data-type="text" order="ascending"/>
							<tr>
								<td style="text-align: center"><xsl:if test="name() = 'LastPmt'"><xsl:call-template name="format-number-if-exists">
									<xsl:with-param name="number" select="PaymentAmt"/>
									<xsl:with-param name="format" select="'$###,##0.00'"/>
								</xsl:call-template></xsl:if></td>
								<td style="text-align: center"><xsl:if test="name() = 'LastPmt'"><xsl:value-of select="PaymentDt"/></xsl:if></td>
								<td style="text-align: center"><xsl:if test="name() = 'DefermentForbearance'"><xsl:value-of select="DefermentForbearanceBeginDate"/></xsl:if></td>
								<td style="text-align: center"><xsl:if test="name() = 'DefermentForbearance'"><xsl:value-of select="DefermentForbearanceEndDate"/></xsl:if></td>
								<td style="text-align: center"><xsl:if test="name() = 'DefermentForbearance'"><xsl:value-of select="DefermentForbearanceName"/></xsl:if></td>
							</tr>
							</xsl:for-each>
						</tbody>
					</table>
					</xsl:otherwise>
					</xsl:choose>
				</div>
				<div id="modal_showCumulativeDefForb{position()}" class="showOptions" style="width:726px;">
					<h3>Cumulative Deferment/Forbearance Details</h3>
					<table cellpadding="0" cellspacing="0" class="tblCumulative">
						<thead>
							<tr>
								<th class="col1">Deferment/Forbearance Type</th>
								<th class="col2">Data Providers</th>
								<th class="col3">Cumulative Months Used</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="//Repayment/Deferment | //Repayment/Forbearance">
								<xsl:sort select="DefermentForbearanceTypeCode" order="ascending"/>
								<xsl:sort select="../../DataProviderType" order="ascending"/>
							</xsl:apply-templates>
							<tr>
								<td class="colspan" colspan="3"><strong>Note:</strong> Deferments that do not have any time limitations are not displayed</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div id="modal_showOutstanding{position()}" class="showOptions" style="width:500px;">
					<h3>Fee Information</h3>
					<xsl:choose>
					<xsl:when test="count(LateFees | CollectionCosts | ServicingFees | OtherFees) = 0">
						<p>Information regarding fees has not been reported by this Data Provider.</p>
					</xsl:when>
					<xsl:otherwise>
						<table>
							<col width="200px"/>
							<col width="100px"/>
							<col width="100px"/>
							<xsl:apply-templates select="." mode="outstanding-fees"/>
						</table>
					</xsl:otherwise>
					</xsl:choose>
				</div>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template name="fees">
		<xsl:if 
			test="count(LateFees/LateFeesAmount) + count(CollectionCosts/CollectionCostsAmount) + count(ServicingFees/ServicingFeesAmount) + count(OtherFees/OtherFeesAmount) > 0">
			<xsl:call-template name="format-number-if-exists">
				<xsl:with-param name="number" select="sum(LateFees/LateFeesAmount) + sum(CollectionCosts/CollectionCostsAmount ) + sum(ServicingFees/ServicingFeesAmount ) + sum(OtherFees/OtherFeesAmount )"/>
				<xsl:with-param name="format" select="'$###,##0.00'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Repayment/Deferment | Repayment/Forbearance">
		<tr>
			<td class="col1"><xsl:value-of select="DefermentForbearanceTypeCode"/><xsl:if test="name() = 'Forbearance'"> Forbearance</xsl:if></td>
			<td class="col2"><xsl:apply-templates select="../.." mode="select-best-source-no-link"/></td>
			<td class="col3"><xsl:value-of select="DefermentForbearanceTimeUsed"/></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="Repayment" mode="outstanding-fees">
		<xsl:for-each select="LateFees | CollectionCosts | ServicingFees | OtherFees">
			<xsl:sort select="LateFeesDate | CollectionCostsDate | ServicingFeesDate | OtherFeesDate" data-type="text" 
				order="descending"/>
			<xsl:apply-templates select="." />
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="LateFees">
		<tr>
			<td><strong>Late Fee:</strong></td>
			<td>
				<xsl:call-template name="format-number-if-exists">
					<xsl:with-param name="number" select="LateFeesAmount"/>
					<xsl:with-param name="format" select="'$###,##0.00'"/>
				</xsl:call-template>
			</td>
			<td><xsl:value-of select="LateFeesDate"/></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="CollectionCosts">
		<tr>
			<td><strong>Collection Costs:</strong></td>
			<td>
				<xsl:call-template name="format-number-if-exists">
					<xsl:with-param name="number" select="CollectionCostsAmount"/>
					<xsl:with-param name="format" select="'$###,##0.00'"/>
				</xsl:call-template>
			</td>
			<td><xsl:value-of select="CollectionCostsDate"/></td>
		</tr>
	</xsl:template>
	<xsl:template match="ServicingFees">
		<tr>
			<td><strong>Servicing Fees:</strong></td>
			<td>
				<xsl:call-template name="format-number-if-exists">
					<xsl:with-param name="number" select="ServicingFeesAmount"/>
					<xsl:with-param name="format" select="'$###,##0.00'"/>
				</xsl:call-template>
			</td>
			<td><xsl:value-of select="ServicingFeesDate"/></td>
		</tr>
	</xsl:template>
	<xsl:template match="OtherFees">
		<tr>
			<td><strong>Other Fees:</strong></td>
			<td>
				<xsl:call-template name="format-number-if-exists">
					<xsl:with-param name="number" select="OtherFeesAmount"/>
					<xsl:with-param name="format" select="'$###,##0.00'"/>
				</xsl:call-template>
			</td>
			<td><xsl:value-of select="OtherFeesDate"/></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="DefermentForbearance">
		<xsl:if test="position() = 1">
		<table>
			<col width="100px"/>
			<tr>
				<td><strong>Type:</strong></td>
				<td><xsl:value-of select="DefermentForbearanceName"/></td>
			</tr>
			<tr>
				<td><strong>Begin Date:</strong></td>
				<td><xsl:value-of select="DefermentForbearanceBeginDate"/></td>
			</tr>
			<tr>
				<td><strong>End Date:</strong></td>
				<td><xsl:value-of select="DefermentForbearanceEndDate"/></td>
			</tr>
		</table>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
