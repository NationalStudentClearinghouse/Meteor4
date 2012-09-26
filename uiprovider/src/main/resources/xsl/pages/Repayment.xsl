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
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-master.xsl"/>
	<xsl:include href="../help/RepaymentHelp.xsl"/>
	
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:key name="awards-by-servicer" match="//Award" use="Servicer/EntityID"/>
	<xsl:key name="awards-by-type" match="//Award" use="AwardType"/>
	
	<xsl:template name="htmlhead">
		<title>Repayment Summary</title>
		
		<script type="text/javascript">		
			$(document).ready( function() {
				$('.hideShow').hide();
			
				$('a.msgtrigger').click(function(){
					var $str = '.';
					var $str2 = $(this).attr("id");
					var $showMe = $str+$str2;
					$($showMe).fadeToggle("fast");
					
					event.preventDefault ? event.preventDefault() : event.returnValue = false;
				});
			});	
		</script>
		
		<style type="text/css">
			.tFooter .msgtrigger,
			.tFooter .msgtrigger:link,
			.tFooter .msgtrigger:visited,
			.tFooter .msgtrigger:hover,
			.tFooter .msgtrigger:active {
				font-weight: bold;
				font-style: normal;
			}
		</style>
	</xsl:template>
	
	<xsl:template name="subnavigation" />
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Repayment Summary<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<xsl:if test="$role != 'BORROWER' and $inquiryRole != 'BORROWER'">
		<p class="intro">Only awards where the borrower's SSN matches the SSN entered on the Meteor Query screen are displayed. For example, PLUS loans where the SSN entered is the student's SSN are not included in this display, but will appear on the Award Summary screen.</p>
		</xsl:if>

	    <div class="actionButtons">
		    <div class="button-group">
		        <a href="#" class="button primary"><xsl:attribute name="onclick">javascript:showModal('showLoanTypeTotals', { minHeight: 170 })</xsl:attribute>Loan Type Totals</a>
                <xsl:if test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">
                <!-- <a href="#" class="button">Import MyData</a> -->
		        <a href="{$docroot}/meteor/mydata.do" class="button">Save MyData</a>
		        </xsl:if>
		    </div>
		</div>
	    
		<p class="tableTitle">Award Information</p>
		<table cellpadding="0" cellspacing="0" class="tblPayment">
			<thead>
				<tr>
					<th class="thPaymentNew1" nowrap="nowrap">View Details</th>
					<th class="thPaymentNew2" nowrap="nowrap">Borrower's Name</th>
					<th class="thPaymentNew3" nowrap="nowrap">Award Type</th>
					<th class="thPaymentNew4" nowrap="nowrap">Loan Status</th>
					<th class="thPaymentNew5" nowrap="nowrap">Interest<br/>Rate</th>
					<th class="thPaymentNew6" nowrap="nowrap">Award Amount</th>
					<th class="thPaymentNew7" nowrap="nowrap">Outstanding<br/>Balance</th>
					<th class="thPaymentNew8" nowrap="nowrap">Outstanding<br/>Balance Date</th>
					<th class="thPaymentNew9" nowrap="nowrap">Begin Date</th>
					<th class="thPaymentNew10" nowrap="nowrap">End Date</th>
					<th class="thPaymentNew11" nowrap="nowrap">Data Provider</th>
					<th class="thPaymentNew12" nowrap="nowrap">Data Provider Type</th>
				</tr>
			</thead>
			<tbody>
			<xsl:apply-templates select="//Award[count(. | key('awards-by-servicer',Servicer/EntityID)[1]) = 1]" mode="servicer-group">
				<xsl:sort select="Servicer/EntityID" data-type="text" order="ascending"/>
			</xsl:apply-templates>
			</tbody>
		</table>
		
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="repayment-help"/>
		</div>
		<div id="modal_showLoanTypeTotals" class="showOptions" style="width:500px;">
		    <xsl:call-template name="loan-type-totals">
		        <xsl:with-param name="awards" select="//Award"/>
		    </xsl:call-template>
		</div>
		
		<xsl:if test="$role != 'APCSR'">
			<xsl:call-template name="loan-locator"/>
		</xsl:if>
		
		<xsl:if test="count(//MeteorDataProviderMsg[RsMsgLevel='E']) > 0">
		<table cellpadding="0" cellspacing="0" class="tblMsg">
			<thead>
				<tr>
					<th class="thMsg" colspan="2">Error Messages</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="//MeteorDataProviderMsg[RsMsgLevel='E' and ../MeteorDataProviderDetailInfo/DataProviderType = 'IDX']" mode="error-messages"/>
				<xsl:apply-templates select="//MeteorDataProviderMsg[RsMsgLevel='E' and ../MeteorDataProviderDetailInfo/DataProviderType != 'IDX']" mode="error-messages"/>
			</tbody>
		</table>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Award" mode="servicer-group">
		<xsl:if test="key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked=$ssn]">
			<xsl:apply-templates select="key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked=$ssn]">
				<xsl:sort select="Disbursement[last()]/ActualDisbDt | Disbursement[last() and (not(ActualDisbDt))]/SchedDisbDt" order="descending" data-type="text" />
				<xsl:sort select="AwardType" order="ascending" data-type="text"/>
			</xsl:apply-templates>
			<xsl:variable name="atLeast1NonGrantScholarship"><xsl:apply-templates select="key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked=$ssn]" mode="count"/></xsl:variable>
			<xsl:if test="string-length($atLeast1NonGrantScholarship) > 0">
			<tr>
				<td class="tFooter" colspan="12" style="text-align: right"><a href="#" class="msgtrigger" id="triggerRepay{position()}">Repayment Info <img src="{$docroot}/imgs/repayment-info.gif" border="0" /></a> | <a href="#" class="msgtrigger" id="triggerTotals{position()}">Grand Totals <img src="{$docroot}/imgs/totals.gif" border="0" /></a><xsl:if test="count(key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked=$ssn]/../../MeteorDataProviderMsg/RsMsg) > 0"> | <a href="#" class="msgtrigger" id="triggerMsg{position()}">Messages <img src="{$docroot}/imgs/messages.gif" border="0" /></a></xsl:if></td>
			</tr>
			<tr class="altRow">
				<td class="triggerRepay{position()} hideShow" colspan="12">
					<table cellpadding="0" cellspacing="0" class="tblMsg">
						<thead>
							<tr>
								<th class="thMsg" colspan="2">Repayment Info</th>
							</tr>
						</thead>
						<tbody>
							<xsl:if test="count(key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked = $ssn]/Repayment/DaysPastDue) > 0">
							<tr>
								<td class="tdBorrowerPastDue" colspan="4"><strong>You are <xsl:apply-templates select="." mode="max-days-past-due"/> Days Past Due!</strong></td>
							</tr>
							</xsl:if>
							<xsl:variable name="repayment-due-date">
								<xsl:apply-templates select="." mode="earliest-due-date"/>
							</xsl:variable>
							<tr>
								<td>Next Payment Due Date:</td>
								<td><xsl:value-of select="$repayment-due-date"/></td>
							</tr>
							<tr>
								<td>Amount Due:</td>
								<td><xsl:call-template name="SumAmount">
										<xsl:with-param name="DUEDATE" select="$repayment-due-date"/>
										<xsl:with-param name="AWARD" select="."/>
									</xsl:call-template></td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
			<tr class="altRow">
				<td class="triggerTotals{position()} hideShow" colspan="12">
					<table cellpadding="0" cellspacing="0" class="tblMsg">
						<thead>
							<tr>
								<th class="thMsg" colspan="2">Grand Totals</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>Grand Total Original Account Balance:</td>
								<td>
									<xsl:call-template name="format-number-if-exists">
										<xsl:with-param name="number" select="//MeteorDataAggregates/OriginalBalanceGrandTotal[@ServicerID = current()/Servicer/EntityID]"/>
										<xsl:with-param name="format" select="'$###,##0.00'"/>
									</xsl:call-template>
								</td>
							</tr>
							<tr>
								<td>Grand Total Outstanding Account Balance:</td>
								<td>
									<xsl:call-template name="format-number-if-exists">
										<xsl:with-param name="number" select="//MeteorDataAggregates/OutstandingBalanceGrandTotal[@ServicerID = current()/Servicer/EntityID]"/>
										<xsl:with-param name="format" select="'$###,##0.00'"/>
									</xsl:call-template>
								</td>
							</tr>
							<tr>
								<td>Grand Total Other Fees Currently Outstanding:</td>
								<td>
									<xsl:call-template name="format-number-if-exists">
										<xsl:with-param name="number" select="//MeteorDataAggregates/OtherFeesOutstandingGrandTotal[@ServicerID = current()/Servicer/EntityID]"/>
										<xsl:with-param name="format" select="'$###,##0.00'"/>
									</xsl:call-template>
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
			<tr class="altRow">
				<td class="triggerMsg{position()} hideShow" colspan="12">
					<table cellpadding="0" cellspacing="0" class="tblMsg">
						<thead>
							<tr>
								<th class="thMsg" colspan="2">Messages</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked=$ssn]/../../MeteorDataProviderMsg"/>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td class="tFooter" colspan="9"><p>If you require additional information or feel that any of the data displayed for this award is incorrect or invalid, please contact the source of the data (For contact information, click on the provider&#39;s name above)</p></td>
			</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<!-- Award count excludes grants and scholarships -->
	<xsl:template match="Award" mode="count"><xsl:variable name="isGrantScholarship"><xsl:apply-templates select="AwardType" mode="is-grant-scholarship"/></xsl:variable><xsl:if test="$isGrantScholarship != 'true'">1</xsl:if></xsl:template>
	
	<xsl:template match="Award">
		<xsl:variable name="isGrantScholarship"><xsl:apply-templates select="AwardType" mode="is-grant-scholarship"/></xsl:variable>
		<xsl:if test="$isGrantScholarship != 'true'">
			<tr>
				<xsl:attribute name="class"><xsl:choose>
					<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
					<xsl:otherwise>altRow</xsl:otherwise>
				</xsl:choose></xsl:attribute>
				<td class="tdPaymentNew1" nowrap="nowrap" valign="middle"><a href="{$docroot}/meteor/repaymentDetail.do?apsUniqAwardId={APSUniqueAwardID}"><img src="{$docroot}/imgs/view-details.jpg" border="0" /></a></td>
				<td class="tdPaymentNew2" nowrap="nowrap">
					<xsl:choose>
					<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">
						<xsl:variable name="isConsolidation"><xsl:apply-templates select="AwardType" mode="is-consolidation"/></xsl:variable>
						<xsl:if test="$isConsolidation = 'false'">
							<xsl:apply-templates select="Student" mode="fullname"/>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="Borrower" mode="fullname"/>
					</xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="tdPaymentNew3" nowrap="nowrap">
					<xsl:apply-templates select="AwardType"/>
				</td>
				<td class="tdPaymentNew4" nowrap="nowrap">
					<xsl:apply-templates select="LoanStat"/>
				</td>
				<td class="tdPaymentNew5" nowrap="nowrap">
				    <xsl:apply-templates select="Repayment/CurrIntRate"/>%
				</td>
				<td class="tdPaymentNew6" nowrap="nowrap">
					<xsl:choose>
						<xsl:when test="string(number(GrossLoanAmount)) != 'NaN'">
							<xsl:value-of select="format-number(GrossLoanAmount, '$###,##0.00')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="format-number-if-exists">
								<xsl:with-param name="number" select="AwardAmt"/>
								<xsl:with-param name="format" select="'$###,##0.00'"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="tdPaymentNew7" nowrap="nowrap">
				    <xsl:choose>
                        <xsl:when test="../../MeteorDataProviderDetailInfo/DataProviderType != 'GSP'">
						    <xsl:call-template name="format-number-if-exists">
						        <xsl:with-param name="number" select="Repayment/AcctBal"/>
						        <xsl:with-param name="format" select="'$###,##0.00'"/>
						    </xsl:call-template>
					    </xsl:when>
				    </xsl:choose>
				</td>
				<td class="tdPaymentNew8" nowrap="nowrap">
				    <xsl:choose>
                        <xsl:when test="../../MeteorDataProviderDetailInfo/DataProviderType != 'GSP'">
				            <xsl:value-of select="Repayment/AcctBalDt"/>
				        </xsl:when>
				    </xsl:choose>
				</td>
				<td class="tdPaymentNew9" nowrap="nowrap">
					<xsl:choose>
						<xsl:when test="AwardBeginDt">
							<xsl:value-of select="AwardBeginDt"/>
						</xsl:when>
					</xsl:choose>
				</td>
				<td class="tdPaymentNew10" nowrap="nowrap">
					<xsl:choose>
						<xsl:when test="AwardEndDt">
							<xsl:value-of select="AwardEndDt"/>
						</xsl:when>
					</xsl:choose>
				</td>
				<td class="tdPaymentNew11" nowrap="nowrap">
					<xsl:apply-templates select="." mode="select-best-source"/>
				</td>
				<td class="tdPaymentNew12" nowrap="nowrap">
					<xsl:apply-templates select="DataProviderType" />
				</td>
			</tr>
			<xsl:if test="($role = 'BORROWER' or $inquiryRole = 'BORROWER') and (OnlinePaymentProcessURL or OnlineDeferForbProcessURL)">
			<tr>
				<xsl:attribute name="class"><xsl:choose>
					<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
					<xsl:otherwise>altRow</xsl:otherwise>
				</xsl:choose></xsl:attribute>
				<td colspan="12" style="text-align: right">
					<xsl:if test="OnlinePaymentProcessURL"><xsl:choose>
						<xsl:when test="$role = 'APCSR'"><a href="#">Make a Payment</a></xsl:when>
						<xsl:otherwise><a href="{OnlinePaymentProcessURL}">Make a Payment</a></xsl:otherwise>
					</xsl:choose></xsl:if> 
					<xsl:if test="OnlinePaymentProcessURL and OnlineDeferForbProcessURL"> | </xsl:if> 
					<xsl:if test="OnlineDeferForbProcessURL"><xsl:choose>
						<xsl:when test="$role = 'APCSR'"><a href="#">Apply for a Deferment/Forbearance</a></xsl:when>
						<xsl:otherwise><a href="{OnlineDeferForbProcessURL}">Apply for a Deferment/Forbearance</a></xsl:otherwise>
					</xsl:choose></xsl:if>
				</td>
			</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Award" mode="max-days-past-due">
		<xsl:for-each select="key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked = $ssn]/Repayment/DaysPastDue">
			<xsl:sort select="." order="descending"/>
			<xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="Award" mode="earliest-due-date">
		<xsl:for-each select="key('awards-by-servicer',Servicer/EntityID)[Borrower/SSNum/@unmasked = $ssn]/Repayment/NextDueDt">
			<xsl:sort select="." order="ascending"/>
			<xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="SumAmount">
		<xsl:param name="DUEDATE"/>
		<xsl:param name="AWARD"/>
		<xsl:call-template name="format-number-if-exists">
			<xsl:with-param name="number" select="sum(key('awards-by-servicer',$AWARD/Servicer/EntityID)[Borrower/SSNum/@unmasked = $ssn and Repayment/NextDueDt=$DUEDATE]/Repayment/NextPmtAmt)"/>
			<xsl:with-param name="format" select="'$###,##0.00'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="MeteorDataProviderMsg">
		<xsl:for-each select="RsMsg">
			<tr>
				<td>
					<a href="{../../MeteorDataProviderDetailInfo/DataProviderData/EntityURL}" target="_blank">
						<xsl:value-of select="../../MeteorDataProviderDetailInfo/DataProviderData/EntityName"/>
					</a>
				</td>
				<td>
					<xsl:value-of select="." disable-output-escaping="yes"/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="loan-locator">
		<xsl:if test="count(//MeteorDataProviderInfo/MeteorDataProviderDetailInfo[DataProviderType = 'UNK']) > 0 and //MeteorIndexProviderData[1]/EntityName">
		<table cellpadding="0" cellspacing="0" class="tblMsg" style="margin-bottom: 1em">
			<thead>
				<tr>
					<th class="thMsg" colspan="2">Loan Locator</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="2"><xsl:value-of select="//MeteorIndexProviderData[1]/EntityName"/> indicates that the following providers also have financial aid award information available. Please visit the provider's website for additional details.</td>
				</tr>
				<xsl:apply-templates select="//MeteorDataProviderInfo/MeteorDataProviderDetailInfo[DataProviderType = 'UNK']" mode="loan-locator"/>
			</tbody>
		</table>
		</xsl:if>
		<xsl:if test="count(//MeteorDataProviderInfo[LoanLocatorActivationIndicator = '1' or LoanLocatorActivationIndicator = 'true']) > 0">
		<table cellpadding="0" cellspacing="0" class="tblMsg" style="margin-bottom: 1em">
			<thead>
				<tr>
					<th class="thMsg" colspan="2">Loan Locator</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="2">The following providers have indicated they have additional information available. Please visit the provider's website for additional details.</td>
				</tr>
				<xsl:apply-templates select="//MeteorDataProviderInfo[LoanLocatorActivationIndicator = '1' or LoanLocatorActivationIndicator = 'true']/MeteorDataProviderDetailInfo" mode="loan-locator"/>
			</tbody>
		</table>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="MeteorDataProviderDetailInfo" mode="loan-locator">
		<tr><td style="padding-top: 1em;">&#32;</td></tr>
		<tr>
			<td><xsl:value-of select="DataProviderData/EntityName"/></td>
			<td><a href="{DataProviderData/EntityURL}"><xsl:value-of select="DataProviderData/EntityURL"/></a></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="MeteorDataProviderMsg" mode="error-messages">
		<xsl:for-each select="RsMsg">
			<tr><td style="padding-top: 1em;">&#32;</td></tr>
			<tr>
				<td>
				<xsl:choose>
				<xsl:when test="../../MeteorDataProviderDetailInfo/DataProviderData/EntityURL">
					<a href="{../../MeteorDataProviderDetailInfo/DataProviderData/EntityURL}" target="_blank">
						<xsl:value-of select="../../MeteorDataProviderDetailInfo/DataProviderData/EntityName"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../../MeteorDataProviderDetailInfo/DataProviderData/EntityName"/>
				</xsl:otherwise>
				</xsl:choose> 
				</td>
				<td>
					<xsl:value-of select="." disable-output-escaping="yes"/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
