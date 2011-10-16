<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	Meteor 3.3.4's implementation of SAML was created before the SAML 1.0 spec was finalized. 
	This stylesheet transforms standard SAML 1 into Meteor SAML 1.
-->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:saml1="urn:oasis:names:tc:SAML:1.0:assertion"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:output
		method="xml"
		encoding="UTF-8" />
	
	<xsl:template match="//saml1:NameIdentifier">
		<xsl:element name="saml:NameIdentifier"  namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:attribute name="Name"><xsl:value-of select="."/></xsl:attribute>
			<xsl:attribute name="SecurityDomain">nchelp.org/meteor</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="saml1:Assertion">
		<xsl:element name="saml:Assertion" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:copy-of select="@*[name() != 'xmlns:xsi' and name() != 'xsi:type']"/>
			<xsl:call-template name="timestamp-transform">
				<xsl:with-param name="time" select="@IssueInstant"/>
			</xsl:call-template>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="//saml1:AuthenticationStatement">
		<xsl:element name="saml:AuthenticationStatement" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:copy-of select="@*[name() != 'xmlns:xsi' and name() != 'xsi:type']" />
			<xsl:attribute name="AuthenticationMethod">http://nchelp.org</xsl:attribute>
			<xsl:call-template name="timestamp-transform">
				<xsl:with-param name="time" select="@AuthenticationInstant"/>
			</xsl:call-template>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="//saml1:Conditions">
		<xsl:element name="saml:Condition" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:call-template name="timestamp-transform">
				<xsl:with-param name="time" select="@NotBefore"/>
			</xsl:call-template>
			<xsl:call-template name="timestamp-transform">
				<xsl:with-param name="time" select="@NotOnOrAfter"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="//saml1:Attribute">
		<xsl:element name="saml:Attribute" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:copy-of select="@*[name() != 'xmlns:xsi' and name() != 'xsi:type']" />
			<xsl:attribute name="AttributeNamespace">nchelp.org/meteor</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="//saml1:AttributeValue">
		<xsl:element name="saml:AttributeValue" namespace="http://www.oasis-open.org/committees/security/docs/draft-sstc-schema-assertion-27.xsd">
			<xsl:copy-of select="@*[name() != 'xmlns:xsi' and name() != 'xsi:type']" />
			<xsl:apply-templates />
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
    
    <!-- 
    	Converts xsd:dateTime data type into Meteor's custom date time datatype 
    	(parsable by Java's SimpleDateFormat)
     -->
    <xsl:template name="timestamp-transform">
		<xsl:param name="time"/>
		<xsl:variable name="timezone">
			<xsl:choose>
			<xsl:when test="contains($time,'Z')">+0000</xsl:when>
			<xsl:otherwise><xsl:value-of select="concat(substring( $time, string-length($time) - 5, 3), substring( $time, string-length($time) - 1))"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:attribute name="{name($time)}"><xsl:value-of select="concat(substring( $time, 1, 19), $timezone)"/></xsl:attribute>
	</xsl:template>

</xsl:stylesheet>