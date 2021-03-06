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
		
	<xsl:template match="MeteorDataRequest/@meteorVersion" />

	<xsl:template match="MeteorDataRequest/AccessProvider/MeteorInstitutionIdentifier">
		<xsl:element name="MeteorInstitutionIdentifier"><xsl:value-of select="."/></xsl:element>
		<xsl:element name="ID"><xsl:value-of select="."/></xsl:element>
	</xsl:template>
	
	<xsl:template match="MeteorDataRequest/AccessProvider/IssueInstant">
		<xsl:variable name="timezone">
			<xsl:choose>
			<xsl:when test="contains(.,'Z')">+0000</xsl:when>
			<xsl:otherwise><xsl:value-of select="concat(substring( ., string-length(.) - 5, 3), substring( ., string-length(.) - 1))"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="IssueInstant"><xsl:value-of select="concat(substring( ., 1, 19), $timezone)"/></xsl:element>
	</xsl:template>
	
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
