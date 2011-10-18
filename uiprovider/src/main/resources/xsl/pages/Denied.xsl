<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-basic.xsl"/>
	
	<xsl:param name="contact-url" select="''"/>
		
	<xsl:template name="htmlhead">
		<title>HTTP 403 - Access Denied</title>
	</xsl:template>
	
	<xsl:template name="htmlbody">
		<h1>Access Denied</h1>						
		<p>
			You are not allowed to access the requested resource. If you feel you have received this message in error, please <a target="_blank" href="{$contact-url}">contact us.</a>
		</p>
	</xsl:template>
</xsl:stylesheet>
