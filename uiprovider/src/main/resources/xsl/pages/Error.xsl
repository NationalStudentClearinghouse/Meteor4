<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-basic.xsl"/>
	
	<xsl:param name="contact-url" select="''"/>
		
	<xsl:template name="htmlhead">
		<title>HTTP 500 - Internal Server Error</title>
	</xsl:template>
	
	<xsl:template name="htmlbody">
		<h1>Internal Server Error</h1>						
		<p>
			An unspecified error has occurred. If the problem persists, please <a target="_blank" href="{$contact-url}">contact us.</a>
		</p>
	</xsl:template>
</xsl:stylesheet>
