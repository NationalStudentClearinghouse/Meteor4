<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
		
	<xsl:template match="//SSNum">
		<xsl:copy><xsl:attribute name="unmasked"><xsl:value-of select="."/></xsl:attribute>*****<xsl:value-of select="substring(.,6)"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>