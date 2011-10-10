<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-master.xsl"/>
	<xsl:include href="../help/AwardDetailsHelp.xsl"/>
	
	<xsl:param name="apsuniqueawardid"/>
	
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>Disbursements</title>
	</xsl:template>
	
	<xsl:template name="subnavigation">
		<xsl:call-template name="subnavigation-award-detail">
			<xsl:with-param name="awardId" select="$apsuniqueawardid"/>
		</xsl:call-template>
	</xsl:template>
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Disbursements<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<p class="tableTitle">Award Information</p>
		<table cellpadding="0" cellspacing="0" class="tblAwardInfo">
			<thead>
				<tr>
					<th class="thAwardInfo3" nowrap="nowrap">Award Type</th>
					<th class="thAwardInfo5" nowrap="nowrap">Award Amount</th>
					<th class="thAwardInfo6" nowrap="nowrap">Begin Date</th>
					<th class="thAwardInfo7" nowrap="nowrap">End Date</th>
					<th class="thAwardInfo8" nowrap="nowrap">School</th>
					<th class="thAwardInfo9" nowrap="nowrap">Lender</th>
					<th class="thAwardInfo10" nowrap="nowrap">Servicer</th>
					<th class="thAwardInfo11" nowrap="nowrap">Guarantor</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td class="tFooter" colspan="12"><p>* Denotes source of data. If you require additional information or feel that any of the data displayed for this award is incorrect or invalid, please contact the source of the data - <xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid]" mode="select-best-source"/></p></td>
				</tr>
			</tfoot>
			<tbody>
				<xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid]"/>
			</tbody>
		</table>				
		
		<p class="tableTitle clr">Disbursements</p>
		<table cellpadding="0" cellspacing="0" class="tblBorrower">
			<tbody>
				<xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid]/Disbursement"/>
				<xsl:if test="$role != 'BORROWER' and count(//Award[APSUniqueAwardID=$apsuniqueawardid]/Disbursement) = 0 or $role = 'BORROWER' and count(//Award[APSUniqueAwardID=$apsuniqueawardid]) = 0 ">
				<tr>
					<td class="tdBorrower2">No Disbursements Found</td>
				</tr>
				</xsl:if>
			</tbody>
		</table>
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="award-details-help"/>
		</div>
	</xsl:template>
	
	<xsl:template match="Award">
		<tr>
			<xsl:attribute name="class"><xsl:choose>
				<xsl:when test="position() mod 2 = 1">defRow</xsl:when>
				<xsl:otherwise>altRow</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<td class="tdAwardInfo3" nowrap="nowrap">
				<xsl:apply-templates select="AwardType"/>
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
				<xsl:apply-templates select="Lender" mode="link-to-contact"/>
				<xsl:if test="DataProviderType = 'LO'"> *</xsl:if>
			</td>
			<td class="tdAwardInfo10" nowrap="nowrap">
				<xsl:apply-templates select="Servicer" mode="link-to-contact"/>
				<xsl:if test="DataProviderType = 'LRS'"> *</xsl:if>
			</td>
			<td class="tdAwardInfo11" nowrap="nowrap">
				<xsl:apply-templates select="Guarantor" mode="link-to-contact"/>
				<xsl:if test="DataProviderType = 'G'"> *</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="Disbursement">
		<tr>
			<td class="tdBorrower1">Sequence Number:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:value-of select="DisbSeqNum"/>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Scheduled Disbursement Date:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:value-of select="SchedDisbDt"/>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Actual Disbursement Date:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:value-of select="ActualDisbDt"/>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Net Amount:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:call-template name="format-number-if-exists">
					<xsl:with-param name="number" select="DisbNetAmt"/>
					<xsl:with-param name="format" select="'$###,##0.00'"/>
				</xsl:call-template>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Cancelled Amount:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:call-template name="format-number-if-exists">
					<xsl:with-param name="number" select="CancellationAmount"/>
					<xsl:with-param name="format" select="'$###,##0.00'"/>
				</xsl:call-template>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Cancelled Date:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:value-of select="CancellationDate"/>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Status:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:apply-templates select="DisbStatCd"/>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Status Date:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:value-of select="DisbStatDt"/>
			</td>
		</tr>
		<tr>
			<td class="tdBorrower1">Hold Release Indicator:</td>
			<td class="tdBorrower2" colspan="3">
				<xsl:apply-templates select="DisbHold"/>
			</td>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>