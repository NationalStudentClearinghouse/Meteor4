<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
	
	<xsl:include href="../layout-basic.xsl"/>

	<xsl:param name="redirect-url"/>
	
	<xsl:template name="htmlhead">
		<script type="text/javascript" src="{$docroot}/scripts/jquery-1.6.4.min.js"></script>
		<script type="text/javascript">
			$(document).ready( function() {
				$.get('<xsl:value-of select="$redirect-url"/>?splash', function() {
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
	