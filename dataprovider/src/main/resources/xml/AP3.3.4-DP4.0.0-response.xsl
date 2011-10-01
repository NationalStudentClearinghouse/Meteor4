<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
		
	<xsl:template match="//@PESCXMLVersion">
		<xsl:attribute name="PESCXMLVersion">3.0.0</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="//Repayment/RepaymentTermRemaining | //Repayment/CurrentMonthlyPayment | //Repayment/DaysPastDue | //Phone/PhoneValidInd | //Phone/PhoneValidDt | //Contacts/EmailValidInd | //Contacts/EmailValidDt | //Repayment/Deferment | //Repayment/Forbearance | //Repayment/OnlinePaymentProcessURL | //Repayment/OnlineDeferForbProcessURL | //MeteorDataProviderInfo/LoanLocatorActivationIndicator"/>

	<xsl:template match="//Award/LoanDt">
		<xsl:element name="GuarDt"><xsl:value-of select="."/></xsl:element>
	</xsl:template>
	
	<xsl:template match="//Award/AwardId[string-length(.) > 19]">
		<xsl:copy><xsl:value-of select="substring(.,string-length(.) - 19 + 1)"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="//DataProviderType[. = 'S']">
		<xsl:copy>SBS</xsl:copy>
	</xsl:template>
	
	<xsl:template match="//Contacts/Email">
		<xsl:copy><xsl:value-of select="EmailAddress"/></xsl:copy>
	</xsl:template>
	
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
	
</xsl:stylesheet>