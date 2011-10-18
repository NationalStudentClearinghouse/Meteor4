<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
	
	<xsl:output method="html"
	    doctype-public="-W3CDTD XHTML 1.0 Strict//EN"
	    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" 
	    encoding="UTF-8"
	    indent="yes" />
	
	<xsl:param name="docroot"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<link rel="stylesheet" type="text/css" href="{$docroot}/style/global.css"/>
				<xsl:call-template name="htmlhead"/>
			</head>
			<body>
				<table cellpadding="0" cellspacing="0" width="100%" class="tblContainer">
					<tr>
						<td width="20" height="27" class="noPad"><img src="{$docroot}/imgs/corner-top-left.jpg" border="0" /></td>
						<td height="27" class="orange noPad"><img src="{$docroot}/imgs/spacer.gif" height="27" width="1" border="0" /></td>
						<td width="20" height="27" class="noPad"><img src="{$docroot}/imgs/corner-top-right.jpg" border="0" /></td>
					</tr>
					<tr>
						<td class="white bkHeader" colspan="3">
							<table cellpadding="0" cellspacing="0" width="100%" class="tblHeader">
								<tr>							
									<td class="tdHeader" width="100%">
										<div class="logo"><a href="#" title="Meteor Network"><img src="{$docroot}/imgs/logo.jpg" border="0" /></a></div>
									</td>
								</tr>
							</table>
						</td>
					</tr>			
					<tr>
						<td width="20" class="white noPad"><img src="{$docroot}/imgs/spacer.gif" width="20px" border="0" /></td>
						<td class="white content">
							<xsl:call-template name="htmlbody"/>
						</td>
						<td width="20" class="white noPad"><img src="{$docroot}/imgs/spacer.gif" width="20px" border="0" /></td>
					</tr>
					<tr>
						<td width="20" height="27" class="noPad"><img src="{$docroot}/imgs/corner-bottom-left.jpg" border="0" /></td>
						<td height="27" class="orange noPad"><img src="{$docroot}/imgs/spacer.gif" height="27" width="1" border="0" /></td>
						<td width="20" height="27" class="noPad"><img src="{$docroot}/imgs/corner-bottom-right.jpg" border="0" /></td>
					</tr>		
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
	