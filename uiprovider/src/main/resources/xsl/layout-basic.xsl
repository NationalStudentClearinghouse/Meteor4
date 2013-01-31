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
	
