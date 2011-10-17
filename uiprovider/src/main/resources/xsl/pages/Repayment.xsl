<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-master.xsl"/>
	<xsl:include href="../help/RepaymentHelp.xsl"/>
	
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
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
					
					e.preventDefault();
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
		<xsl:if test="$role != 'BORROWER'">
		<p class="intro">Only awards where the borrower's SSN matches the SSN entered on the Meteor Query screen are displayed. For example, PLUS loans where the SSN entered is the student's SSN are not included in this display, but will appear on the Award Summary screen.</p>
		</xsl:if>
		
		<p class="tableTitle">Award Information</p>
		<xsl:apply-templates select="//MeteorDataProviderInfo">
			<xsl:sort select="MeteorDataProviderDetailInfo/DataProviderData/EntityName" data-type="text" order="ascending"/>
		</xsl:apply-templates>
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="repayment-help"/>
		</div>
	</xsl:template>
	
	<xsl:template match="MeteorDataProviderInfo">
		<table cellpadding="0" cellspacing="0" class="tblPayment">
			<thead>
				<tr>
					<th class="thPayment1" nowrap="nowrap">View Details</th>
					<th class="thPayment2" nowrap="nowrap">Borrower's Name</th>
					<th class="thPayment3" nowrap="nowrap">Award Type</th>
					<th class="thPayment4" nowrap="nowrap">Loan Status</th>
					<th class="thPayment5" nowrap="nowrap">Award Amount</th>
					<th class="thPayment6" nowrap="nowrap">Begin Date</th>
					<th class="thPayment7" nowrap="nowrap">End Date</th>
					<th class="thPayment8" nowrap="nowrap">Data Provider</th>
					<th class="thPayment9" nowrap="nowrap">Data Provider Type</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td class="tFooter" colspan="9" style="text-align: right"><a href="#" class="msgtrigger" id="triggerRepay{position()}">Repayment Info <img src="{$docroot}/imgs/repayment-info.gif" border="0" /></a> | <a href="#" class="msgtrigger" id="triggerTotals{position()}">Grand Totals <img src="{$docroot}/imgs/totals.gif" border="0" /></a><xsl:if test="count(MeteorDataProviderMsg/RsMsg) > 0"> | <a href="#" class="msgtrigger" id="triggerMsg{position()}">Messages <img src="{$docroot}/imgs/messages.gif" border="0" /></a></xsl:if></td>
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
								<xsl:if test="count(MeteorDataProviderAwardDetails/Award[Borrower/SSNum/@unmasked = $ssn]/Repayment/DaysPastDue) > 0">
								<tr>
									<td class="tdBorrowerPastDue" colspan="4"><strong>You are <xsl:apply-templates select="MeteorDataProviderAwardDetails" mode="max-days-past-due"/> Days Past Due!</strong></td>
								</tr>
								</xsl:if>
								<xsl:variable name="repayment-due-date">
									<xsl:apply-templates select="MeteorDataProviderAwardDetails" mode="earliest-due-date"/>
								</xsl:variable>
								<tr>
									<td>Next Payment Due Date:</td>
									<td><xsl:value-of select="$repayment-due-date"/></td>
								</tr>
								<tr>
									<td>Amount Due:</td>
									<td><xsl:call-template name="SumAmount"><xsl:with-param name="DUEDATE" select="$repayment-due-date"/></xsl:call-template></td>
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
											<xsl:with-param name="number" select="MeteorDataProviderDetailInfo/DataProviderAggregateTotal/OriginalBalanceGrandTotal"/>
											<xsl:with-param name="format" select="'$###,##0.00'"/>
										</xsl:call-template>
									</td>
								</tr>
								<tr>
									<td>Grand Total Outstanding Account Balance:</td>
									<td>
										<xsl:call-template name="format-number-if-exists">
											<xsl:with-param name="number" select="MeteorDataProviderDetailInfo/DataProviderAggregateTotal/OutstandingBalanceGrandTotal"/>
											<xsl:with-param name="format" select="'$###,##0.00'"/>
										</xsl:call-template>
									</td>
								</tr>
								<tr>
									<td>Grand Total Other Fees Currently Outstanding:</td>
									<td>
										<xsl:call-template name="format-number-if-exists">
											<xsl:with-param name="number" select="MeteorDataProviderDetailInfo/DataProviderAggregateTotal/OtherFeesOutstandingGrandTotal"/>
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
								<xsl:apply-templates select="MeteorDataProviderMsg"/>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td class="tFooter" colspan="9"><p>If you require additional information or feel that any of the data displayed for this award is incorrect or invalid, please contact the source of the data (For contact information, click on the provider&#39;s name above)</p></td>
				</tr>
			</tfoot>
			<tbody>
				<xsl:apply-templates select="MeteorDataProviderAwardDetails/Award[Borrower/SSNum/@unmasked=$ssn]">
					<xsl:sort select="Disbursement[last()]/ActualDisbDt | Disbursement[last() and (not(ActualDisbDt))]/SchedDisbDt" order="descending" data-type="text" />
					<xsl:sort select="AwardType" order="ascending" data-type="text"/>
				</xsl:apply-templates>
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template match="Award">
		<tr>
			<xsl:attribute name="class"><xsl:choose>
				<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
				<xsl:otherwise>altRow</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<td class="tdPayment1" nowrap="nowrap" valign="middle"><a href="{$docroot}/meteor/repaymentDetail.do?apsUniqAwardId={APSUniqueAwardID}"><img src="{$docroot}/imgs/view-details.jpg" border="0" /></a></td>
			<td class="tdPayment2" nowrap="nowrap">
				<xsl:choose>
				<xsl:when test="$role = 'BORROWER'">
					<xsl:if test="ConsolidationLoan = '0'">
					<xsl:apply-templates select="Student" mode="fullname"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="Borrower" mode="fullname"/>
				</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="tdPayment3" nowrap="nowrap">
				<xsl:apply-templates select="AwardType"/>
			</td>
			<td class="tdPayment4" nowrap="nowrap">
				<xsl:apply-templates select="LoanStat"/>
			</td>
			<td class="tdPayment5" nowrap="nowrap">
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
	
	<xsl:template match="MeteorDataProviderAwardDetails" mode="max-days-past-due">
		<xsl:for-each select="Award[Borrower/SSNum/@unmasked = $ssn]/Repayment/DaysPastDue">
			<xsl:sort select="." order="descending"/>
			<xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="MeteorDataProviderAwardDetails" mode="earliest-due-date">
		<xsl:for-each select="Award[Borrower/SSNum/@unmasked = $ssn]/Repayment/NextDueDt">
			<xsl:sort select="." order="ascending"/>
			<xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="SumAmount">
		<xsl:param name="DUEDATE"/>
		<xsl:call-template name="format-number-if-exists">
			<xsl:with-param name="number" select="sum(MeteorDataProviderAwardDetails/Award[Borrower/SSNum/@unmasked = $ssn][Repayment/NextDueDt=$DUEDATE]/Repayment/NextPmtAmt)"/>
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
</xsl:stylesheet>