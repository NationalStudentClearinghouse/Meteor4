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
	<xsl:include href="../help/ReferencesHelp.xsl"/>
	
	<xsl:param name="apsuniqueawardid"/>
		
	<!-- Templates / Variables for layout-master -->
	<xsl:variable name="person"><xsl:choose>
		<xsl:when test="$role = 'BORROWER' or $inquiryRole = 'BORROWER'">borrower</xsl:when>
		<xsl:otherwise>student</xsl:otherwise>
	</xsl:choose></xsl:variable>
	
	<xsl:template name="htmlhead">
		<title>References</title>
	</xsl:template>
	
	<xsl:template name="subnavigation">
		<xsl:call-template name="subnavigation-award-detail">
			<xsl:with-param name="awardId" select="$apsuniqueawardid"/>
		</xsl:call-template>
	</xsl:template>
	<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	<xsl:template match="pescxml:MeteorRsMsg">
		<h1>References<span class="help"><a href="#" class="helpSection"><xsl:attribute name="onclick">javascript:showModal('showHelp', { minHeight: 170 })</xsl:attribute><img src="{$docroot}/imgs/help.jpg" border="0" /></a></span></h1>
		<p class="tableTitle">References</p>
		<table cellpadding="0" cellspacing="0" class="tblBorrower">
			<tbody>
				<xsl:apply-templates select="//Award[APSUniqueAwardID=$apsuniqueawardid]" mode="reference"/>
			</tbody>
		</table>
		<div id="modal_showHelp" class="showOptions" style="width:800px;">
			<xsl:call-template name="references-help"/>
		</div>
	</xsl:template>
	
	<xsl:template match="Award" mode="reference">
		<xsl:for-each select="Reference">
			<tr>
				<td class="tdBorrower2">
					<xsl:apply-templates select="." mode="fullname"/><br/> <xsl:apply-templates 
					select="Contacts/Phone"/><br/><xsl:apply-templates 
					select="Contacts/AddressInfo"/><xsl:if 
					test="string-length(Contacts/Email/EmailAddress) > 0"> <xsl:apply-templates select="Contacts/Email"/><br/> </xsl:if><br/>
				</td>
			</tr>
		</xsl:for-each>
		<xsl:if test="count(Reference) = 0">
			<tr>
				<td class="tdBorrower2">
					No References Found
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
