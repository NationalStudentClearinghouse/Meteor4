<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="MeteorRsMsg">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="//Repayment/RepaymentTermRemaining | //Repayment/CurrentMonthlyPayment | //Repayment/DaysPastDue | //Repayment/ForbearanceTimeUsed | //Repayment/DefermentTimeUsed"/>
	<xsl:template match="//Phone/PhoneValidInd | //Phone/PhoneValidDt | //Contacts/EmailValidInd | //Contacts/EmailValidDt"/>
	
	<xsl:template match="//*" priority="-100">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>