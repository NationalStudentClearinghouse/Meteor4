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
		<xsl:attribute name="PESCXMLVersion">4.0.0</xsl:attribute>
	</xsl:template>
	
	<!-- 
		Remove any 0000-00-00 dates generated by 3.3.4 data providers for empty xsd:date elements
	 -->
	<xsl:template match="//AddressInfo/AddrValidDt[. = '0000-00-00']"/>
	<xsl:template match="//Award/AwardBeginDt[. = '0000-00-00'] | //Award/AwardEndDt[. = '0000-00-00'] | //Award/LoanStatDt[. = '0000-00-00']"/>
	<xsl:template match="//CollectionCosts/CollectionCostsDate[. = '0000-00-00']"/>
	<xsl:template match="//Default/DefAvertRqDt[. = '0000-00-00'] | //Default/DefAvertRqCureDt[. = '0000-00-00'] | //Default/ClaimFilDt[. = '0000-00-00'] | //Default/ClaimPdDt[. = '0000-00-00'] | //Default/EligibilityReinstatementDate[. = '0000-00-00']"/>
	<xsl:template match="//DefermentForbearance/DefermentForbearanceBeginDate[. = '0000-00-00'] | //DefermentForbearance/DefermentForbearanceEndDate[. = '0000-00-00']"/>
	<xsl:template match="//Disbursement/SchedDisbDt[. = '0000-00-00'] | //Disbursement/ActualDisbDt[. = '0000-00-00'] | //Disbursement/DisbStatDt[. = '0000-00-00'] | //Disbursement/CancellationDate[. = '0000-00-00']"/>
	<xsl:template match="//LateFees/LateFeesDate[. = '0000-00-00']"/>
	<!-- PaymentDt is required... xsl:template match="//LastPmt/PaymentDt[. = '0000-00-00']"><xsl:copy /></xsl:template -->
	<xsl:template match="//LoanTransfer/TransferDt[. = '0000-00-00']"/>
	<xsl:template match="//OtherFees/OtherFeesDate[. = '0000-00-00']"/>
	<xsl:template match="//Phone//PhoneValidDt[. = '0000-00-00']"/>
	<xsl:template match="//Reference/DtOfBirth[. = '0000-00-00'] | //Borrower/DtOfBirth[. = '0000-00-00'] | //Student/DtOfBirth[. = '0000-00-00'] | //Student/GradDt[. = '0000-00-00']"/>
	<xsl:template match="//Repayment/NextDueDt[. = '0000-00-00'] | //Repayment/AcctBalDt[. = '0000-00-00'] | //Repayment/PmtBeginDt[. = '0000-00-00']"/>
	<xsl:template match="//ServicingFees/ServicingFeesDate[. = '0000-00-00']"/>
	 
	<xsl:template match="//Award/GuarDt"><xsl:if test=". != '0000-00-00'">
		<LoanDt><xsl:value-of select="."/></LoanDt>
	</xsl:if></xsl:template>
	
	<xsl:template match="//Contacts/Email">
		<xsl:copy><EmailAddress><xsl:value-of select="."/></EmailAddress></xsl:copy>
	</xsl:template>
	
	<xsl:template match="//Award[Student/SSNum = Borrower/SSNum]/AwardType[. = 'FFELPLUS']">
		<AwardType>FFELGB</AwardType>
	</xsl:template>
	
	<xsl:template match="//Award[Student/SSNum = Borrower/SSNum]/AwardType[. = 'DLPLUS']">
		<AwardType>DLGB</AwardType>
	</xsl:template>
	
	<xsl:template match="//DataProviderType[. = 'SBS']">
		<xsl:copy>S</xsl:copy>
	</xsl:template>
	
	<xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
