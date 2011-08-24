<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
	
	<xsl:template match="//Repayment/RepaymentTermRemaining | //Repayment/CurrentMonthlyPayment | //Repayment/DaysPastDue | //Repayment/ForbearanceTimeUsed | //Repayment/DefermentTimeUsed"/>
	<xsl:template match="//Phone/PhoneValidInd | //Phone/PhoneValidDt | //Contacts/EmailValidInd | //Contacts/EmailValidDt"/>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
	
</xsl:stylesheet>