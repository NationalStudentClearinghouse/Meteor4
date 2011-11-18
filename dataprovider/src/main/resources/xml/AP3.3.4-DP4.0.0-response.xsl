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
