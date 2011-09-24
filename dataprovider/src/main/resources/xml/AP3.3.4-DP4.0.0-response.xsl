<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
	
	<xsl:template match="//Repayment/RepaymentTermRemaining | //Repayment/CurrentMonthlyPayment | //Repayment/DaysPastDue | //Phone/PhoneValidInd | //Phone/PhoneValidDt | //Contacts/EmailValidInd | //Contacts/EmailValidDt"/>

	<xsl:template match="//Award/LoanDt">
		<xsl:element name="GuarDt"><xsl:value-of select="."/></xsl:element>
	</xsl:template>
	
	<xsl:template match="//Award/AwardId">
		<xsl:copy><xsl:value-of select="substring(.,1,19)"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="//Repayment/Deferment | //Repayment/Forbearance">
		<xsl:element name="DefermentForbearance">
			<xsl:element name="DefermentForbearanceName"><xsl:value-of select="TypeCode"/></xsl:element>
			<!-- TODO: calculate Deferment/Forbearance BeginDate and Deferment/Forbearance EndDate? -->
		</xsl:element>
	</xsl:template>
	
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
	
</xsl:stylesheet>