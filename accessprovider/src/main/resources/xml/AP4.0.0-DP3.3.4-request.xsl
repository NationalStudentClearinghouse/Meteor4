<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output
		method="xml"
		encoding="UTF-8" />
		
	<xsl:template match="MeteorDataRequest/@meteorVersion" />

	<xsl:template match="MeteorDataRequest/AccessProvider/IssueInstant">
		<xsl:variable name="timezone">
			<xsl:choose>
			<xsl:when test="contains(.,'Z')">+0000</xsl:when>
			<xsl:otherwise><xsl:value-of select="concat(substring( ., string-length(.) - 5, 3), substring( ., string-length(.) - 1))"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<IssueInstant><xsl:value-of select="concat(substring( ., 1, 19), $timezone)"/></IssueInstant>
	</xsl:template>
	
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>