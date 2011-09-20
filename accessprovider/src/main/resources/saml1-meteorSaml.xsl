<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	Meteor 3.3.4's implementation of SAML was created before the SAML 1.0 spec was finalized. 
	This stylesheet transforms standard SAML 1 into Meteor SAML 1.
-->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:saml1="urn:oasis:names:tc:SAML:1.0:assertion"
	xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output
		method="xml"
		encoding="UTF-8" />

	<xsl:template match="//saml1:NameIdentifier">
		<xsl:element name="saml:NameIdentifier"  namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:attribute name="Name"><xsl:value-of select="."/></xsl:attribute>
			<xsl:attribute name="SecurityDomain">nchelp.org/meteor</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template match="saml1:Assertion/@IssueInstant | //saml1:Conditions/@NotBefore | //saml1:Conditions/@NotOnOrAfter | //saml1:AuthenticationStatement/@AuthenticationInstant">
		<xsl:variable name="timezone">
			<xsl:choose>
			<xsl:when test="contains(.,'Z')">+0000</xsl:when>
			<xsl:otherwise><xsl:value-of select="concat(substring( ., string-length(.) - 5, 3), substring( ., string-length(.) - 1))"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:attribute name="{name()}"><xsl:value-of select="concat(substring( ., 1, 19), $timezone)"/></xsl:attribute>
	</xsl:template>
	
	<xsl:template match="//saml1:Conditions">
		<xsl:element name="saml:Condition" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:copy-of select="@*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="saml1:*">
		<xsl:element name="saml:{local-name()}" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
		    <xsl:copy-of select="@*"/>
		    <xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>