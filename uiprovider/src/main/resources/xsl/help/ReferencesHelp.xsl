<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
	<xsl:template name="references-help">
		<h3>Help</h3>
		<p>This document provides an English description of each of the data elements on the screen that you are viewing.  If you require additional information regarding the loan that you are viewing, please contact the source of the data.</p>
		<table cellpadding="0" cellspacing="0" class="tblHelp">
			<tbody>
				<xsl:choose>
				<xsl:when test="$role = 'BORROWER'">
				<tr>
					<td class="tdHelp1">Borrower's Name</td>
					<td class="tdHelp2">First, middle initial and last name of the borrower.</td>
				</tr> 
				</xsl:when>
				<xsl:otherwise>
				<tr>
					<td class="tdHelp1">Student's Name</td>
					<td class="tdHelp2">First, middle initial and last name and SSN of the student.</td>
				</tr>
				</xsl:otherwise>
				</xsl:choose> 
				<tr>
					<td class="tdHelp1">References</td>
					<td class="tdHelp2">Information on the reference(s) provided on a particular promissory note.</td>
				</tr>
			</tbody>				
		</table>
	</xsl:template>
	                
</xsl:stylesheet>