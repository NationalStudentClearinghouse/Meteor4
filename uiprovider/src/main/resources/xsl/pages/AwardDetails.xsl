<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
    	            
	<xsl:include href="../layout-master.xsl"/>
	<xsl:include href="../help/AwardDetailsHelp.xsl"/>
	
	<xsl:param name="apsuniqueawardid"/>
		
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>Award Details</title>
	</xsl:template>
	
	<xsl:template name="subnavigation">
		<xsl:call-template name="subnavigation-award-detail">
			<xsl:with-param name="awardId" select="$apsuniqueawardid"/>
		</xsl:call-template>
	</xsl:template>
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>Award Details<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<xsl:apply-templates select="//Award[APSUniqueAwardID = $apsuniqueawardid]"/>
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="award-details-help"/>
		</div>
	</xsl:template>
	
	<xsl:template match="Award">
		<xsl:if test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">
			<p class="tableTitle">Student Name: <xsl:apply-templates select="Student" mode="fullname"/> (<xsl:value-of select="Student/SSNum"/>)</p>
		</xsl:if>
		<xsl:for-each select="Borrower">
		<p class="tableTitle">Borrower Information: <xsl:apply-templates select="." mode="fullname"/> (<xsl:value-of select="SSNum"/>)</p>
		<table cellpadding="0" cellspacing="0" class="tblBorrower">
			<tbody>
				<xsl:for-each select="Contacts/AddressInfo">
				<tr>
					<td class="tdBorrower1">
						<xsl:apply-templates select="AddressType" />
					</td>
					<td class="tdBorrower2">
						<xsl:for-each select="Addr">
							<xsl:value-of select="."/>
							<br/>
						</xsl:for-each>
						<xsl:value-of select="concat(City, ', ', StateProv, ' ', PostalCd)"/>
					</td>
					<xsl:if test="$role != 'BORROWER' and $inquiryRole != 'BORROWER' and (AddressValidInd or AddressValidDt)">
					<td class="tdBorrower3">Address Validated:</td>
					<td class="tdBorrower4"><xsl:choose>
						<xsl:when test="AddressValidInd = 'true' or AddressValidInd = '1'">Yes</xsl:when>
						<xsl:when test="AddressValidInd = 'false' or AddressValidInd = '0'">No</xsl:when>
					</xsl:choose><xsl:if test="AddressValidDt"> - <xsl:value-of select="AddressValidDt"/></xsl:if></td>
					</xsl:if>
				</tr>
				</xsl:for-each>
				<xsl:for-each select="Contacts/Phone">
				<tr>
					<td class="tdBorrower1"><xsl:apply-templates select="PhoneNumType" /></td>
					<td class="tdBorrower2"><xsl:apply-templates select="PhoneNum" /></td>
					<xsl:if test="$role != 'BORROWER' and $inquiryRole != 'BORROWER'and (PhoneValidInd or PhoneValidDt)">
					<td class="tdBorrower3">Phone Validated:</td>
					<td class="tdBorrower4"><xsl:choose>
						<xsl:when test="PhoneValidInd = 'true' or PhoneValidInd = '1'">Yes</xsl:when>
						<xsl:when test="PhoneValidInd = 'false' or PhoneValidInd = '0'">No</xsl:when>
					</xsl:choose><xsl:if test="PhoneValidDt"> - <xsl:value-of select="PhoneValidDt"/></xsl:if></td>
					</xsl:if>
				</tr>
				</xsl:for-each>
				<xsl:for-each select="Contacts/Email">
				<tr>
					<td class="tdBorrower1">Email Address:</td>
					<td class="tdBorrower2"><xsl:value-of select="EmailAddress"/></td>
					<xsl:if test="$role != 'BORROWER' and $inquiryRole != 'BORROWER' and (EmailValidInd or EmailValidDt)">
					<td class="tdBorrower3">Email Validated:</td>
					<td class="tdBorrower4"><xsl:choose>
						<xsl:when test="EmailValidInd = 'true' or EmailValidInd = '1'">Yes</xsl:when>
						<xsl:when test="EmailValidInd = 'false' or EmailValidInd = '0'">No</xsl:when>
					</xsl:choose><xsl:if test="EmailValidDt"> - <xsl:value-of select="EmailValidDt"/></xsl:if></td>
					</xsl:if>
				</tr>
				</xsl:for-each>
				<xsl:if test="DriversLicense">
				<tr>
					<td class="tdBorrower1">Drivers License:</td>
					<td class="tdBorrower2"><xsl:value-of select="DriversLicense"/></td>
					<td class="tdBorrower3">State:</td>	
					<td class="tdBorrower4"><xsl:value-of select="DriversLicenseState"/></td>								
				</tr>
				</xsl:if>
			</tbody>
		</table>
		</xsl:for-each>
		
		<p class="tableTitle clr">Award Details</p>
		<table cellpadding="0" cellspacing="0" class="tblBorrower">
			<tbody>
				<xsl:if test="DataProviderType">
				<tr>
					<td class="tdBorrower1">Data Provider:</td>
					<td class="tdBorrower2" colspan="3"><xsl:apply-templates select="." mode="dataprovidername"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Data Provider Type:</td>
					<td class="tdBorrower2" colspan="3"><xsl:apply-templates select="DataProviderType"/></td>
				</tr>
				</xsl:if>
				<tr>
					<td class="tdBorrower1">Award Type:</td>
					<td class="tdBorrower2" colspan="3"><xsl:apply-templates select="AwardType"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Award ID:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="AwardId"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Award Amount:</td>
					<td class="tdBorrower2" colspan="3"><xsl:choose>
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
				</tr>
				<tr>
					<td class="tdBorrower1">Cancelled Amount:</td>
					<td class="tdBorrower2" colspan="3">
						<xsl:call-template name="format-number-if-exists">
							<xsl:with-param name="number" select="Disbursement/CancellationAmount"/>
							<xsl:with-param name="format" select="'$###,##0.00'"/>
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td class="tdBorrower1">Award Begin Date:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="AwardBeginDt"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Award End Date:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="AwardEndDt"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Loan Date:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="LoanDt"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Graduation Date:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="Student/GradDt"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Repayment Begin Date:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="Repayment/PmtBeginDt"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Grade Level:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="GradeLevelInd"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Award Status:</td>
					<td class="tdBorrower2" colspan="3"><xsl:apply-templates select="LoanStat"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">Award Status Date:</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="LoanStatDt"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">E-Signature:</td>
					<td class="tdBorrower2" colspan="3"><xsl:choose>
						<xsl:when test="Esign = 'false' or Esign = '0'">No</xsl:when>
						<xsl:when test="Esign = 'true' or Esign = '1'">Yes</xsl:when>
					</xsl:choose></td>
				</tr>
				<tr><td class="tdBorrower1">&#32;</td></tr>
				<tr>
					<td class="tdBorrower1">Current Interest Rate</td>
					<td class="tdBorrower2" colspan="3"><xsl:value-of select="Repayment/CurrIntRate"/></td>
				</tr>
				<tr>
					<td class="tdBorrower1">
						<xsl:attribute name="style"><xsl:if test="Repayment/DaysPastDue">color:#F00;</xsl:if></xsl:attribute>
						Days Past Due
					</td>
					<td class="tdBorrower2" colspan="3">
						<xsl:attribute name="style"><xsl:if test="Repayment/DaysPastDue">color:#F00;</xsl:if></xsl:attribute>
						<xsl:value-of select="Repayment/DaysPastDue"/>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>
</xsl:stylesheet>