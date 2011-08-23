<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
		
	<xsl:template match="MeteorDataRequest">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="MeteorDataRequest/AssertionSpecifier" />
	
	<xsl:template match="MeteorDataRequest/AccessProvider">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="MeteorDataRequest/AccessProvider/*[name() != 'ID']">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<xsl:template match="MeteorDataRequest/SSN">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<xsl:template match="//*" priority="-100"/>
	
</xsl:stylesheet>