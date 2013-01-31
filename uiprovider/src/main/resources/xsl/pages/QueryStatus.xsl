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

	<xsl:param name="redirect-url"/>
	
	<xsl:template name="htmlhead">
		<script type="text/javascript" src="{$docroot}/scripts/jquery-1.6.4.min.js"></script>
		<script type="text/javascript">
			$(document).ready( function() {
				$.get('<xsl:value-of select="$redirect-url"/>?splash=&#38;' + (new Date()).getTime(), function() {
					window.location.href = '<xsl:value-of select="$redirect-url"/>';
				});
			});
		</script>
		
		<style type="text/css">
			#loadingpanel {
				margin: 60px auto 0 auto;
				width: 540px;
				height: 440px;
				
				background: white center center no-repeat url('<xsl:value-of select="$docroot"/>/imgs/loading.gif');
				border: 1px solid #e7e7e8;
				
				text-align: center;
			}
			
			#loadingpanel .logo {
				margin-top: 80px;
			}
			
			#loadingpanel p {
				margin-top: 120px;
				font-size: 11px;
				text-align: center;
			}
		</style>
	</xsl:template>
	
	<xsl:template name="htmlbody">
		<div id="loadingpanel">
			<div class="logo"><a href="#" title="Meteor Network"><img src="{$docroot}/imgs/logo.jpg" border="0" /></a></div>
			<p>Please wait while we query your data.</p>
		</div>
	</xsl:template>
	

</xsl:stylesheet>
	
