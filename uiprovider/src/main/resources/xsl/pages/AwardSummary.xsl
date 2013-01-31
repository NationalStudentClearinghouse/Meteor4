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
	<xsl:include href="../help/AwardSummaryHelp.xsl"/>
	
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>Award Summary</title>
		
		<script type="text/javascript">		
			$(document).ready( function() {
				$('.hideShow').hide();
			
				$('a.msgtrigger').click(function(){
					var $str = '.';
					var $str2 = $(this).attr("id");
					var $showMe = $str+$str2;
					$($showMe).fadeToggle("fast");
					
					e.preventDefault ? e.preventDefault() : e.returnValue = false;
				});
			});	
		</script>
	</xsl:template>
	
	<xsl:template name="subnavigation" />
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Award Summary<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		
		<xsl:if test="$role != 'BORROWER' and $inquiryRole != 'BORROWER'">
			<p class="intro">Only awards where the student&#39;s SSN matches the SSN entered on the Meteor Query screen are displayed. For example, PLUS loans where the SSN entered is the borrower&#39;s SSN are not included in this display, but will appear on the Repayment Summary screen.</p>
		</xsl:if>
		
		<p class="tableTitle">Award Information</p>
		<table cellpadding="0" cellspacing="0" class="tblAwardInfo">
			<thead>
				<tr>
					<th class="thAwardInfo1" nowrap="nowrap">View Details</th>
					<th class="thAwardInfo2" nowrap="nowrap">Borrower&#39;s Name</th>
					<th class="thAwardInfo3" nowrap="nowrap">Award Type</th>
					<th class="thAwardInfo4" nowrap="nowrap">Loan Status</th>
					<th class="thAwardInfo5" nowrap="nowrap">Award Amount</th>
					<th class="thAwardInfo6" nowrap="nowrap">Begin Date</th>
					<th class="thAwardInfo7" nowrap="nowrap">End Date</th>
					<th class="thAwardInfo8" nowrap="nowrap">School</th>
					<th class="thAwardInfo9" nowrap="nowrap">Lender / Servicer</th>
					<th class="thAwardInfo10">Grant/Scholarship<br/> Provider</th>
					<th class="thAwardInfo11" nowrap="nowrap">Guarantor</th>
					<th class="thAwardInfo12" nowrap="nowrap">Messages</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td class="tFooter" colspan="12"><p>* Denotes source of data. If you require additional information or feel that any of the data displayed for this award is incorrect or invalid, please contact the source of the data (For contact information, click on the provider&#39;s name above)</p></td>
				</tr>
			</tfoot>
			<tbody>
				<xsl:choose>
				<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">
					<xsl:apply-templates select="//Award[Borrower/SSNum/@unmasked=$ssn]">
						<xsl:sort select="Disbursement[last()]/ActualDisbDt | Disbursement[last() and (not(ActualDisbDt))]/SchedDisbDt" order="descending" data-type="text" />
						<xsl:sort select="AwardType" order="ascending" data-type="text"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="//Award[Student/SSNum/@unmasked=$ssn]">
						<xsl:sort select="Disbursement[last()]/ActualDisbDt | Disbursement[last() and (not(ActualDisbDt))]/SchedDisbDt" order="descending" data-type="text" />
						<xsl:sort select="AwardType" order="ascending" data-type="text"/>
					</xsl:apply-templates>
				</xsl:otherwise>
				</xsl:choose>
				
			</tbody>
		</table>
		
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
				<xsl:apply-templates select="//MeteorDataProviderMsg[RsMsgLevel='E' and ../MeteorDataProviderDetailInfo/DataProviderType = 'IDX']"/>
				<xsl:apply-templates select="//MeteorDataProviderMsg[RsMsgLevel='E' and ../MeteorDataProviderDetailInfo/DataProviderType != 'IDX']"/>
			</tbody>
		</table>
		</xsl:if>
		
		<!-- modal content -->
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="award-summary-help"/>
		</div>	
	</xsl:template>
	
	<xsl:template match="Award">
		<tr>
			<xsl:attribute name="class"><xsl:choose>
				<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
				<xsl:otherwise>altRow</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<td class="tdAwardInfo1" nowrap="nowrap" valign="middle"><a href="{$docroot}/meteor/awardDetail.do?apsUniqAwardId={APSUniqueAwardID}"><img src="{$docroot}/imgs/view-details.jpg" border="0" /></a></td>
			<td class="tdAwardInfo2" nowrap="nowrap">
				<xsl:choose>
				<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">
					<xsl:variable name="isConsolidation"><xsl:apply-templates select="AwardType" mode="is-consolidation"/></xsl:variable>
					<xsl:choose>
					<xsl:when test="$isConsolidation = 'false'">
						<xsl:apply-templates select="Student" mode="fullname"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="Borrower" mode="fullname"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="Borrower" mode="fullname"/>
				</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="tdAwardInfo3" nowrap="nowrap">
				<xsl:apply-templates select="AwardType"/>
			</td>
			<td class="tdAwardInfo4" nowrap="nowrap">
				<xsl:apply-templates select="LoanStat"/>
			</td>
			<td class="tdAwardInfo5" nowrap="nowrap">
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
			<td class="tdAwardInfo6" nowrap="nowrap">
				<xsl:value-of select="AwardBeginDt"/>
			</td>
			<td class="tdAwardInfo7" nowrap="nowrap">
				<xsl:value-of select="AwardEndDt"/>
			</td>
			<td class="tdAwardInfo8" nowrap="nowrap">
				<xsl:apply-templates select="School" mode="link-to-contact"/>
				<xsl:if test="DataProviderType = 'S'"> *</xsl:if>
			</td>
			<td class="tdAwardInfo9" nowrap="nowrap">
				<xsl:if test="Lender">
					<xsl:apply-templates select="Lender" mode="link-to-contact"/>
					<xsl:if test="DataProviderType = 'LO'"> *</xsl:if>
				</xsl:if>
				<xsl:if test="Servicer">
					<xsl:if test="Lender"><br/></xsl:if>
					<xsl:apply-templates select="Servicer"  mode="link-to-contact"/>
					<xsl:if test="DataProviderType = 'LRS'"> *</xsl:if>
				</xsl:if>
			</td>
			<td class="tdAwardInfo10" nowrap="nowrap">
				<xsl:apply-templates select="GrantScholarshipProvider"  mode="link-to-contact"/>
				<xsl:if test="DataProviderType = 'GSP'"> *</xsl:if>
			</td>
			<td class="tdAwardInfo11" nowrap="nowrap">
				<xsl:apply-templates select="Guarantor"  mode="link-to-contact"/>
				<xsl:if test="DataProviderType = 'G'"> *</xsl:if>
			</td>
			<td class="tdAwardInfo12" nowrap="nowrap">
				<xsl:if test="../../MeteorDataProviderMsg/RsMsg">
				<a href="#" class="msgtrigger" id="trigger{APSUniqueAwardID}"><img src="{$docroot}/imgs/messages.gif" border="0" /></a>
				</xsl:if>
			</td>
		</tr>
		<xsl:if test="DataProviderType = 'FAT'">
		<tr>
			<xsl:attribute name="class"><xsl:choose>
				<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
				<xsl:otherwise>altRow</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<td colspan="8"></td>
			<td colspan="2" style="text-align: right">
				<strong>Financial Aid Transcript Provider:</strong>
			</td>
			<td class="tdAwardInfo11" colspan="2">
				<xsl:apply-templates select="FinAidTranscript"  mode="link-to-contact"/> *
			</td>
		</tr>
		</xsl:if>
		<tr>
			<xsl:attribute name="class"><xsl:choose>
				<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
				<xsl:otherwise>altRow</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<td class="trigger{APSUniqueAwardID} hideShow" colspan="12">
				<table cellpadding="0" cellspacing="0" class="tblMsg">
					<thead>
						<tr>
							<th class="thMsg" colspan="2">Messages</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="../../MeteorDataProviderMsg"/>
					</tbody>
				</table>
			</td>
		</tr>
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
	
	<xsl:template match="MeteorDataProviderMsg">
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
