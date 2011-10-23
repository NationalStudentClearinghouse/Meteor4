<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
   	        
	<xsl:include href="../layout-basic.xsl"/>
		
	<xsl:template name="htmlhead">
		<title>Session Expired</title>
	</xsl:template>
	
	<xsl:template name="htmlbody">
		<h1>Session is Expired</h1>						
		<p>
			Your Meteor session has expired. Please login again with your provider.
		</p>
	</xsl:template>
</xsl:stylesheet>
