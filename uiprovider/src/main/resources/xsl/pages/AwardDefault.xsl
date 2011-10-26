<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-master.xsl"/>
	<xsl:include href="../help/AwardDefaultHelp.xsl"/>
	
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>Claim and Default Details</title>
	</xsl:template>
	
	<xsl:template name="subnavigation" />
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Default Aversion Request, Claim and Default Details<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<p class="tableTitle">Claim and Default Details</p>
		<xsl:choose>
		<xsl:when test="count(//Award[Borrower/SSNum/@unmasked=$ssn]/Default) > 0">
			<xsl:apply-templates select="//Award[Borrower/SSNum/@unmasked=$ssn]/Default"/>
		</xsl:when>
		<xsl:otherwise>
			<p>No Default Information Available</p>
		</xsl:otherwise>
		</xsl:choose>
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="award-default-help"/>
		</div>
	</xsl:template>
	
	<xsl:template match="Default">
		<table cellpadding="0" cellspacing="0" class="tblPayment">
			<thead>
				<tr>
					<th class="thPayment1" nowrap="nowrap">View Details</th>
					<th class="thPayment3" nowrap="nowrap">Award Type</th>
					<th class="thPayment4" nowrap="nowrap">Loan Status</th>
					<th class="thPayment6" nowrap="nowrap">Repayment Begin Date</th>
					<th class="thPayment7" nowrap="nowrap">Award Amount</th>
					<th class="thPayment8" nowrap="nowrap">Data Provider</th>
					<th class="thPayment9" nowrap="nowrap">Data Provider Type</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select=".."/>
				<tr>
					<td colspan="7" align="center">
						<table cellpadding="0" cellspacing="0" class="tblBorrower">
							<tbody>
								<tr>
									<td class="tdBorrower1">Default Aversion Requested</td>
									<td class="tdBorrower2">
										<xsl:choose>
											<xsl:when test="DefAvertRq = 'false' or DefAvertRq = '0'"> No </xsl:when>
											<xsl:when test="DefAvertRq = 'true' or DefAvertRq = '1'"> Yes </xsl:when>
										</xsl:choose>
									</td>
									<td class="tdBorrower3">Claim Filed</td>
									<td class="tdBorrower4">
										<xsl:choose>
											<xsl:when test="ClaimFil = 'false' or ClaimFil = '0'"> No </xsl:when>
											<xsl:when test="ClaimFil = 'true' or ClaimFil = 'true'"> Yes </xsl:when>
										</xsl:choose>
									</td>
									<td class="tdBorrower3">Default</td>				
									<td class="tdBorrower4">
										<xsl:choose>
											<xsl:when test="Def = 'false' or Def = '0'"> No </xsl:when>
											<xsl:when test="Def = 'true' or Def = '0'"> Yes </xsl:when>
										</xsl:choose>
									</td>
								</tr>
								<tr>
									<td class="tdBorrower1">Requested Date</td>				
									<td class="tdBorrower2">
										<xsl:value-of select="DefAvertRqDt"/>
									</td>
									<td class="tdBorrower3">Claim Filed Date</td>
									<td class="tdBorrower4">
										<xsl:value-of select="ClaimFilDt"/>
									</td>
									<td class="tdBorrower3">Satisfactory Payment Arrangements</td>
									<td class="tdBorrower4">
										<xsl:choose>
											<xsl:when test="SatisPmtArr = 'false' or SatisPmtArr = '0'"> No </xsl:when>
					
											<xsl:when test="SatisPmtArr = 'true' or SatisPmtArr = '1'"> Yes </xsl:when>
										</xsl:choose>
									</td>
								</tr>
								<tr>
									<td></td>
									<td></td>
									<td class="tdBorrower3">Claim Paid</td>
									<td class="tdBorrower4">
										<xsl:choose>
											<xsl:when test="ClaimPd = 'false' or ClaimPd = '0'"> No </xsl:when>
											<xsl:when test="ClaimPd = 'true' or ClaimPd = '1'"> Yes </xsl:when>
										</xsl:choose>
									</td>
									<td class="tdBorrower3">Eligibility Reinstated</td>
									<td class="tdBorrower4">
										<xsl:choose>
											<xsl:when test="EligibilityReinstatementIndicator = 'false' or EligibilityReinstatementIndicator = '0'"> No </xsl:when>
					
											<xsl:when test="EligibilityReinstatementIndicator = 'true' or EligibilityReinstatementIndicator = '1'"> Yes </xsl:when>
										</xsl:choose>
									</td>
								</tr>
								<tr>
									<td></td>
									<td></td>
									<td class="tdBorrower3">Claim Paid Date</td>
									<td class="tdBorrower4">
										<xsl:value-of select="ClaimPdDt"/>
									</td>				
									<td class="tdBorrower3">Reinstated Date</td>
									<td class="tdBorrower4">
										<xsl:value-of select="EligibilityReinstatementDate"/>
									</td>
								</tr>
								<xsl:if test="../Guarantor">
								<tr>
									<td class="tdBorrower1">Send Payments To:</td>
									<td class="tdBorrower2"><xsl:apply-templates select="../Guarantor" mode="contact"/></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								</xsl:if>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td class="tFooter" colspan="12"><p>If you require additional information or feel that any of the data displayed for this award is incorrect or invalid, please contact the source of the data - <xsl:apply-templates select=".." mode="select-best-source"/></p></td>
				</tr>
			</tfoot>
		</table>
	</xsl:template>
	
	<xsl:template match="Award">
		<tr class="defRow">
			<td class="tdPayment1" nowrap="nowrap" valign="middle"><a href="{$docroot}/meteor/awardDetail.do?apsUniqAwardId={APSUniqueAwardID}"><img src="{$docroot}/imgs/view-details.jpg" border="0" /></a></td>
			<td class="tdPayment3" nowrap="nowrap">
				<xsl:apply-templates select="AwardType"/>
			</td>
			<td class="tdPayment4" nowrap="nowrap">
				<xsl:apply-templates select="LoanStat"/>
			</td>
			<td class="tdPayment6" nowrap="nowrap">
				<xsl:value-of select="Repayment/PmtBeginDt"/>
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
			<td class="tdPayment8" nowrap="nowrap">
				<xsl:apply-templates select="." mode="select-best-source"/>
			</td>
			<td class="tdPayment9" nowrap="nowrap">
				<xsl:apply-templates select="DataProviderType" />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>