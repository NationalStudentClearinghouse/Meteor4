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
	
	<!-- Global parameters set on every Meteor response -->
	<xsl:param name="ssn"/>
	<xsl:param name="role"/>
	<xsl:param name="docroot"/>
	<xsl:param name="inquiryRole" select="''"/>

	<!-- Format number if exists -->
	<xsl:template name="format-number-if-exists">
		<xsl:param name="number"/>
		<xsl:param name="format"/>
		<xsl:if test="string(number($number)) != 'NaN'">
			<xsl:value-of select="format-number($number, $format)"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Get contact link to best source provider -->
	<xsl:template match="Award" mode="select-best-source">
		<xsl:choose>
			<xsl:when test="DataProviderType = 'S'"><xsl:apply-templates select="School" mode="link-to-contact"/></xsl:when>
			<xsl:when test="DataProviderType = 'LO'"><xsl:apply-templates select="Lender" mode="link-to-contact"/></xsl:when>
			<xsl:when test="DataProviderType = 'LRS'"><xsl:apply-templates select="Servicer" mode="link-to-contact"/></xsl:when>
			<xsl:when test="DataProviderType = 'G'"><xsl:apply-templates select="Guarantor" mode="link-to-contact"/></xsl:when>
			<xsl:when test="DataProviderType = 'GSP'"><xsl:apply-templates select="GrantScholarshipProvider" mode="link-to-contact"/></xsl:when>
			<xsl:when test="DataProviderType = 'FAT'"><xsl:apply-templates select="FinAidTranscript" mode="link-to-contact"/></xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="Award" mode="select-best-source-no-link">
		<xsl:choose>
			<xsl:when test="DataProviderType = 'S'"><xsl:value-of select="School/EntityName"/></xsl:when>
			<xsl:when test="DataProviderType = 'LO'"><xsl:value-of select="Lender/EntityName"/></xsl:when>
			<xsl:when test="DataProviderType = 'LRS'"><xsl:value-of select="Servicer/EntityName"/></xsl:when>
			<xsl:when test="DataProviderType = 'G'"><xsl:value-of select="Guarantor/EntityName"/></xsl:when>
			<xsl:when test="DataProviderType = 'GSP'"><xsl:value-of select="GrantScholarshipProvider/EntityName"/></xsl:when>
			<xsl:when test="DataProviderType = 'FAT'"><xsl:value-of select="FinAidTranscript/EntityName"/></xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Display data provider name hyperlinked to contact modal -->
	<xsl:template match="School | Lender | Servicer | Guarantor | GrantScholarshipProvider | FinAidTranscript" mode="link-to-contact">
		<a href="#"><xsl:attribute name="onclick">javascript:showModal('DPContact<xsl:value-of select="EntityID"/>', { maxWidth: 574, minHeight: 170 })</xsl:attribute><xsl:value-of select="EntityName"/></a>
		<div id="modal_DPContact{EntityID}" class="showOptions" style="width:500px;">
			<h3>Entity Details</h3>
			<xsl:apply-templates select="." mode="contact"/>
		</div>
	</xsl:template>
	
	<xsl:template match="School | Lender | Servicer | Guarantor | GrantScholarshipProvider | FinAidTranscript" mode="contact">
		<p>
			<xsl:value-of select="EntityName"/> (ID: <xsl:value-of select="EntityID"/>)<br/> 
			<xsl:if test="string-length(Contacts/Phone) > 0"><xsl:apply-templates select="Contacts/Phone"/><br/></xsl:if>
			<xsl:apply-templates select="Contacts/AddressInfo"/>
			<xsl:if test="string-length(Contacts/Email/EmailAddress) > 0"><xsl:apply-templates select="Contacts/Email"/><br/></xsl:if>
			<xsl:if test="string-length(EntityURL) > 0"><a href="{EntityURL}" target="_blank"><xsl:value-of select="EntityURL"/></a><br/></xsl:if>
		</p>
	</xsl:template>

	<!-- Select award data provider name -->
	<xsl:template match="Award" mode="dataprovidername">
		<xsl:choose>
			<xsl:when test="DataProviderType = 'G'">
				<xsl:value-of select="Guarantor/EntityName"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'LRS'">
				<xsl:value-of select="Servicer/EntityName"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'LO'">
				<xsl:value-of select="Lender/EntityName"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'S'">
				<xsl:value-of select="School/EntityName"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'GSP'">
				<xsl:value-of select="GrantScholarshipProvider/EntityName"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'FAT'">
				<xsl:value-of select="FinAidTranscript/EntityName"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Award data provider address -->
	<xsl:template match="Award" mode="best-source-address">
		<xsl:choose>
			<xsl:when test="DataProviderType = 'G'">
				<xsl:apply-templates select="Guarantor/Contacts/AddressInfo"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'LRS'">
				<xsl:apply-templates select="Servicer/Contacts/AddressInfo"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'LO'">
				<xsl:apply-templates select="Lender/Contacts/AddressInfo"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'S'">
				<xsl:apply-templates select="School/Contacts/AddressInfo"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'GSP'">
				<xsl:apply-templates select="GrantScholarshipProvider/Contacts/AddressInfo"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'FAT'">
				<xsl:apply-templates select="FinAidTranscript/Contacts/AddressInfo"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Award data provider phone -->
	<xsl:template match="Award" mode="best-source-phone">
		<xsl:choose>
			<xsl:when test="DataProviderType = 'G'">
				<xsl:apply-templates select="Guarantor/Contacts/Phone/PhoneNum"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'LRS'">
				<xsl:apply-templates select="Servicer/Contacts/Phone/PhoneNum"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'LO'">
				<xsl:apply-templates select="Lender/Contacts/Phone/PhoneNum"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'S'">
				<xsl:apply-templates select="School/Contacts/Phone/PhoneNum"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'GSP'">
				<xsl:apply-templates select="GrantScholarshipProvider/Contacts/Phone/PhoneNum"/>
			</xsl:when>
			<xsl:when test="DataProviderType = 'FAT'">
				<xsl:apply-templates select="FinAidTranscript/Contacts/Phone/PhoneNum"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Data Provider Type -->
	<xsl:template match="Award/DataProviderType">
		<xsl:choose>
			<xsl:when test=". = 'G'">Guarantor</xsl:when>
			<xsl:when test=". = 'LRS'">Lender Repayment Servicer</xsl:when>
			<xsl:when test=". = 'LO'">Lender</xsl:when>
			<xsl:when test=". = 'S'">School</xsl:when>
			<xsl:when test=". = 'GSP'">Grant and Scholarship Provider</xsl:when>
			<xsl:when test=". = 'FAT'">Financial Aid Transcript Provider</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Default address formatting -->
	<xsl:template match="Contacts/AddressInfo">
		<div style="margin:0; padding:0">
		<xsl:for-each select="Addr">
		<xsl:if test="string-length(.) > 0"><xsl:value-of select="."/><br/></xsl:if> 
		</xsl:for-each><xsl:if test="string-length(City) > 0"><xsl:value-of select="City"/>, </xsl:if><xsl:value-of select="StateProv"/><xsl:text> </xsl:text><xsl:value-of select="PostalCd"/>
		</div>		
	</xsl:template>
	
	<!-- Address type -->
	<xsl:template match="Contacts/AddressInfo/AddressType">
		<xsl:choose>
			<xsl:when test=".='P'">Permanent Address: </xsl:when>
			<xsl:when test=".='T'">Temporary Address: </xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Default phone formatting -->
	<xsl:template match="Contacts/Phone">
		<div style="margin:0; padding:0">
		<xsl:apply-templates select="PhoneNumType"/>
		<xsl:apply-templates select="PhoneNum" />
		<xsl:if test="PhoneValidInd or PhoneValidDt"> (<xsl:choose>
			<xsl:when test="PhoneValidInd = 'true' or PhoneValidInd = '1'">Valid</xsl:when>
			<xsl:when test="PhoneValidInd = 'false' or PhoneValidInd = '0'">Invalid</xsl:when>
		</xsl:choose><xsl:if test="PhoneValidDt"> as of <xsl:value-of select="PhoneValidDt"/></xsl:if>)</xsl:if>
		</div>
	</xsl:template>
	
	<!-- Phone type -->
	<xsl:template match="Contacts/Phone/PhoneNumType">
		<xsl:choose>
			<xsl:when test=". = 'H'">Home Phone: </xsl:when>
			<xsl:when test=". = 'W'">Work Phone: </xsl:when>
			<xsl:when test=". = 'T'">Temporary Phone: </xsl:when>
			<xsl:when test=". = 'M'">Mobile Phone: </xsl:when>
			<xsl:when test=". = 'O'">Other Phone: </xsl:when>
			<xsl:when test=". = 'P'">Primary Phone: </xsl:when>
			<xsl:when test=". = 'S'">Secondary Phone: </xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Phone number -->
	<xsl:template match="Contacts/Phone/PhoneNum">
		<xsl:choose>
			<xsl:when test="string-length(normalize-space(.)) = 10"> (<xsl:value-of select="substring(normalize-space(.), 1, 3)" />) 
				<xsl:value-of select="substring(normalize-space(.), 4, 3)" />-<xsl:value-of select="substring(normalize-space(.), 7, 4)" /> 
				</xsl:when>
			<xsl:when test="string-length(normalize-space(.)) = 7"> <xsl:value-of select="substring(normalize-space(.), 1, 3)" 
				/>-<xsl:value-of select="substring(normalize-space(.), 4, 4)" /> </xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="normalize-space(.)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Default email formatting -->
	<xsl:template match="Contacts/Email">
		<a href="mailto:{EmailAddress}"><xsl:value-of select="EmailAddress"/></a>
		<xsl:if test="EmailValidInd or EmailValidDt"> (<xsl:choose>
			<xsl:when test="EmailValidInd = 'true' or EmailValidInd = '1'">Valid</xsl:when>
			<xsl:when test="EmailValidInd = 'false' or EmailValidInd = '0'">Invalid</xsl:when>
		</xsl:choose><xsl:if test="EmailValidDt"> as of <xsl:value-of select="EmailValidDt"/></xsl:if>)</xsl:if>
	</xsl:template>	
	
	<!-- Formats names -->
	<xsl:template match="Student | Borrower | Reference" mode="fullname">
		<xsl:value-of select="concat(FirstName, ' ', MiddleInitial, ' ', LastName)"/>
	</xsl:template>
	
	<!-- Award type descriptions -->
	<xsl:template match="Award/AwardType">
		<xsl:choose>
			<xsl:when test=". = 'FFELCONSL'">FFEL Consolidation</xsl:when>
			<xsl:when test=". = 'FFELCSUB'">FFEL Consolidation Stafford Subsidized</xsl:when>
			<xsl:when test=". = 'FFELCUSUB'">FFEL Consolidation Stafford Unsubsidized</xsl:when>
			<xsl:when test=". = 'FFELCHEAL'">FFEL Consolidation HEAL</xsl:when>
			<xsl:when test=". = 'FFELCOTHR'">FFEL Consolidation Other</xsl:when>
			<xsl:when test=". = 'FFELPLUS'">FFELP PLUS</xsl:when>
			<xsl:when test=". = 'FFELSUB'">FFELP Subsidized</xsl:when>
			<xsl:when test=". = 'FFELUNSUB'">FFELP Unsubsidized</xsl:when>
			<xsl:when test=". = 'FFELGB'">FFELP GradPLUS</xsl:when>
			<xsl:when test=". = 'ALTLOAN'">Alternative Loan</xsl:when>
			<xsl:when test=". = 'HEAL'">Health Education Assistance Loan</xsl:when>
			<xsl:when test=". = 'DLCONSL'">DL Consolidation</xsl:when>
			<xsl:when test=". = 'DLCSUB'">DL Consolidation Stafford Subsidized</xsl:when>
			<xsl:when test=". = 'DLCUSUB'">DL Consolidation Stafford Unsubsidized</xsl:when>
			<xsl:when test=". = 'DLCHEAL'">DL Consolidation HEAL</xsl:when>
			<xsl:when test=". = 'DLCOTHR'">DL Consolidation Other</xsl:when>
			<xsl:when test=". = 'DLSUB'">Subsidized Direct Loan</xsl:when>
			<xsl:when test=". = 'DLUNSUB'">Unsubsidized Direct Loan</xsl:when>
			<xsl:when test=". = 'DLUNSUB'">Unsubsidized Direct Loan</xsl:when>
			<xsl:when test=". = 'DLPLUS'">DL PLUS</xsl:when>
			<xsl:when test=". = 'DLGB'">DL GradPLUS</xsl:when>
			<xsl:when test=". = 'FWSP'">Federal Work Study Program</xsl:when>
			<xsl:when test=". = 'SEOG'">Supplemental Educational Opportunity Grant</xsl:when>
			<xsl:when test=". = 'PERKINS'">Perkins Loan</xsl:when>
			<xsl:when test=". = 'PELL'">Pell Grant</xsl:when>
			<xsl:when test=". = 'STATEGRNT'">State Grant</xsl:when>
			<xsl:when test=". = 'STATESCHL'">State Scholarship</xsl:when>
			<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="Award/AwardType" mode="is-consolidation">
		<xsl:choose>
			<xsl:when test=". = 'FFELCONSL'">true</xsl:when>
			<xsl:when test=". = 'FFELCSUB'">true</xsl:when>
			<xsl:when test=". = 'FFELCUSUB'">true</xsl:when>
			<xsl:when test=". = 'FFELCHEAL'">true</xsl:when>
			<xsl:when test=". = 'FFELCOTHR'">true</xsl:when>
			<xsl:when test=". = 'DLCONSL'">true</xsl:when>
			<xsl:when test=". = 'DLCSUB'">true</xsl:when>
			<xsl:when test=". = 'DLCUSUB'">true</xsl:when>
			<xsl:when test=". = 'DLCHEAL'">true</xsl:when>
			<xsl:when test=". = 'DLCOTHR'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="Award/AwardType" mode="is-grant-scholarship">
		<xsl:choose>
			<xsl:when test=". = 'FWSP'">true</xsl:when>
			<xsl:when test=". = 'SEOG'">true</xsl:when>
			<xsl:when test=". = 'CWC'">true</xsl:when>
			<xsl:when test=". = 'PELL'">true</xsl:when>
			<xsl:when test=". = 'STATEGRNT'">true</xsl:when>
			<xsl:when test=". = 'STATESCHL'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Loan status codes -->
	<xsl:template match="Award/LoanStat">
		<xsl:choose>
			<xsl:when test=".='AE'">Loan Transferred</xsl:when>
			<xsl:when test=".='AL'">Abandoned loan</xsl:when>
			<xsl:when test=".='BC'">Bankruptcy Claim, Discharged</xsl:when>
			<xsl:when test=".='BK'">Bankruptcy Claim, Active</xsl:when>
			<xsl:when test=".='CA'">Cancelled</xsl:when>
			<xsl:when test=".='CP'">Claim, Paid in Full</xsl:when>
			<xsl:when test=".='CS'">Closed School Discharge</xsl:when>
			<xsl:when test=".='D '">Denied/Rejected/Incomplete</xsl:when>
			<xsl:when test=".='DA'">Deferred</xsl:when>
			<xsl:when test=".='DB'">Defaulted, Then Bankrupt, Active, Chapter 13</xsl:when>
			<xsl:when test=".='DC'">Defaulted, Compromise</xsl:when>
			<xsl:when test=".='DD'">Defaulted, Then Died</xsl:when>
			<xsl:when test=".='DE'">Death</xsl:when>
			<xsl:when test=".='DF'">Defaulted, Unresolved</xsl:when>
			<xsl:when test=".='DI'">Disability</xsl:when>
			<xsl:when test=".='DK'">Defaulted, Then Bankrupt, Discharged, Chapter 13</xsl:when>
			<xsl:when test=".='DG'">Defaulted, Contact Guarantor for Default Details</xsl:when>
			<xsl:when test=".='DL'">Defaulted, In Litigation</xsl:when>
			<xsl:when test=".='DN'">Defaulted, Paid in Full Through Consolidation Loan</xsl:when>
			<xsl:when test=".='DO'">Defaulted, Then Bankrupt, Active, other</xsl:when>
			<xsl:when test=".='DP'">Defaulted, Paid in Full</xsl:when>
			<xsl:when test=".='DR'">Defaulted Loan in Roll-Up Loan</xsl:when>
			<xsl:when test=".='DS'">Defaulted, Then Disabled</xsl:when>
			<xsl:when test=".='DT'">Defaulted, Collection Terminated</xsl:when>
			<xsl:when test=".='DU'">Defaulted, Unresolved</xsl:when>
			<xsl:when test=".='DW'">Defaulted, Write-off</xsl:when>
			<xsl:when test=".='DX'">Defaulted, Six Consecutive Payments</xsl:when>
			<xsl:when test=".='DZ'">Defaulted, Six Consecutive Payments, then missed payment</xsl:when>
			<xsl:when test=".='FB'">Forbearance</xsl:when>
			<xsl:when test=".='FC'">False Certification Discharge</xsl:when>
			<xsl:when test=".='IA'">Loan Originated</xsl:when>
			<xsl:when test=".='ID'">In School or Grace Period</xsl:when>
			<xsl:when test=".='IG'">In Grace Period</xsl:when>
			<xsl:when test=".='IM'">In Military Grace</xsl:when>
			<xsl:when test=".='IP'">In post-deferment grace (Perkins only)</xsl:when>
			<xsl:when test=".='MO'">Military Operations</xsl:when>
			<xsl:when test=".='MR'">Military Reservist</xsl:when>
			<xsl:when test=".='OD'">Defaulted, Then Bankrupt, Discharged, Other</xsl:when>
			<xsl:when test=".='PC'">Paid in Full Through Consolidation Loan</xsl:when>
			<xsl:when test=".='PF'">Paid in Full</xsl:when>
			<xsl:when test=".='PM'">Presumed Paid in Full</xsl:when>
			<xsl:when test=".='PN'">Paid in Full Through Consolidation Loan</xsl:when>
			<xsl:when test=".='PZ'">PLUS Child Death</xsl:when>
			<xsl:when test=".='RF'">Refinanced</xsl:when>
			<xsl:when test=".='RP'">In Repayment</xsl:when>
			<xsl:when test=".='UA'">Temporarily Uninsured-No Default Claim Requested</xsl:when>
			<xsl:when test=".='UB'">Temporarily Uninsured-Default Claim Denied</xsl:when>
			<xsl:when test=".='UC'">Permanently Uninsured-No Default Claim Requested</xsl:when>
			<xsl:when test=".='UD'">Permanently Uninsured-Default Claim Denied</xsl:when>
			<xsl:when test=".='UI'">Unreinsured</xsl:when>
			<xsl:when test=".='XD'">Defaulted, six consecutive payments</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="Award/LoanStat" mode="is-paid">
		<xsl:choose>
			<xsl:when test=".='PC'">true</xsl:when>
			<xsl:when test=".='PN'">true</xsl:when>
			<xsl:when test=".='DP'">true</xsl:when>
			<xsl:when test=".='PF'">true</xsl:when>
			<xsl:when test=".='DN'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Disbursement codes -->
	<xsl:template match="DisbStatCd">
		<xsl:choose>
			<xsl:when test=".='D'">Disbursed</xsl:when>
			<xsl:when test=".='A'">Approved</xsl:when>
			<xsl:when test=".='C'">Cancelled</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="DisbHold">
		<xsl:choose>
			<xsl:when test=".='H'">Hold</xsl:when>
			<xsl:when test=".='R'">Release</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

