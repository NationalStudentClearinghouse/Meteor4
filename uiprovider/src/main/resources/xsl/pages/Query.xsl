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
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-master.xsl"/>
	
	<xsl:param name="header-message" select="'Please enter a Social Security number in the form above'"/>
	
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<xsl:choose><xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">
			<title>Meteor Network</title>
		</xsl:when>
		<xsl:otherwise>
			<title>Enter new SSN</title>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="subnavigation" />
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<xsl:if test="$role != 'BORROWER'">
		<h1>Enter new SSN</h1>
		<p>
			<strong><xsl:value-of select="$header-message"/></strong>
		</p>
		</xsl:if>
		<p>By submitting this inquiry to the Meteor Network, you are certifying that you are either (1) an individual 
		requesting information only on student loans for which you are the borrower, (2) a financial aid professional (or 
		are acting on behalf of a financial aid professional) with authority from your institution to view information on 
		the Meteor Network, and that the information you access relates only to a student applicant, a current student, or 
		a former student of the educational institution on whose behalf you are working; (3) an authorized employee of a 
		lender requesting information only on student loans owned by the lender; (4) an authorized employee of a guaranty 
		agency requesting information only on student loans guaranteed by the guaranty agency; or (5) an authorized 
		employee of a loan servicer requesting information only on student loans which are serviced by the loan servicer. 
		Use of the Meteor Network in violation of this certification constitutes misrepresentation and will be considered a 
		fraudulent act. In all cases, by submitting this inquiry to the Meteor Network, you are acknowledging that the 
		information displayed to you relates to student loans provided by participant(s) in the Meteor Network that is 
		associated with the social security number you provided and that other student loans may exist which are held by 
		other entities and that timeliness and accuracy of the information is the responsibility of the individual data 
		provider. </p>
		<xsl:if test="$role = 'BORROWER'">
		<p><a href="{$docroot}/meteor/summary.do">View eligibility and disbursement data for your student loans and other financial awards.</a></p>
		<p><a href="{$docroot}/meteor/repaymentSummary.do">View repayment and billing data for your student loans.</a></p>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
