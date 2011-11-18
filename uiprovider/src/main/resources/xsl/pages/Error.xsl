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
