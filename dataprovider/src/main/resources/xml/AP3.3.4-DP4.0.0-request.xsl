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
	xmlns:fn="http://www.w3.org/2005/xpath-functions">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
		
	<xsl:template match="MeteorDataRequest">
		<xsl:copy>
			<xsl:attribute name="meteorVersion">3.3.4</xsl:attribute>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="MeteorDataRequest/AssertionSpecifier" />
	
	<xsl:template match="MeteorDataRequest/AccessProvider">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="MeteorDataRequest/AccessProvider/IssueInstant">
		<xsl:copy><xsl:value-of select="substring( ., 1, string-length(.) - 2)"/>:<xsl:value-of select="substring( ., string-length(.) - 1)"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="MeteorDataRequest/AccessProvider/*[name() != 'ID' and name() != 'IssueInstant']">
		<xsl:copy-of select="."/>
	</xsl:template>

	<xsl:template match="MeteorDataRequest/SSN">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<xsl:template match="//*" priority="-100"/>
	
</xsl:stylesheet>
