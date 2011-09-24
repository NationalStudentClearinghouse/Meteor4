<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:template match="//@PESCXMLVersion">
		<xsl:attribute name="PESCXMLVersion">4.0.0</xsl:attribute>
	</xsl:template>
	
	<!-- 
		Remove any 0000-00-00 dates generated by 3.3.4 data providers for empty xsd:date elements
	 -->
	<xsl:template match="//AddressInfo/AddrValidDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Award/AwardBeginDt[. = '0000-00-00'] | //Award/AwardEndDt[. = '0000-00-00'] | //Award/LoanStatDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//CollectionCosts/CollectionCostsDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Contacts/EmailValidDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Default/DefAvertRqDt[. = '0000-00-00'] | //Default/DefAvertRqCureDt[. = '0000-00-00'] | //Default/ClaimFilDt[. = '0000-00-00'] | //Default/ClaimPdDt[. = '0000-00-00'] | //Default/EligibilityReinstatementDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//DefermentForbearance/DefermentForbearanceBeginDate[. = '0000-00-00'] | //DefermentForbearance/DefermentForbearanceEndDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Disbursement/SchedDisbDt[. = '0000-00-00'] | //Disbursement/ActualDisbDt[. = '0000-00-00'] | //Disbursement/DisbStatDt[. = '0000-00-00'] | //Disbursement/CancellationDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//LateFees/LateFeesDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//LastPmt/PaymentDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//LoanTransfer/TransferDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//OtherFees/OtherFeesDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Phone//PhoneValidDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Reference/DtOfBirth[. = '0000-00-00'] | //Borrower/DtOfBirth[. = '0000-00-00'] | //Student/DtOfBirth[. = '0000-00-00'] | //Student/GradDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//Repayment/NextDueDt[. = '0000-00-00'] | //Repayment/AcctBalDt[. = '0000-00-00'] | //Repayment/PmtBeginDt[. = '0000-00-00']"><xsl:copy /></xsl:template>
	<xsl:template match="//ServicingFees/ServicingFeesDate[. = '0000-00-00']"><xsl:copy /></xsl:template>
	 
	<xsl:template match="//Award/GuarDt">
		<LoanDt><xsl:if test=". != '0000-00-00'"><xsl:value-of select="."/></xsl:if></LoanDt>
	</xsl:template>
	
	<xsl:template match="//DefermentForbearance">
		<!-- TODO: translate into Meteor 4.0 Deferment/Forbearance tags -->
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>