<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pescxml="http://schemas.pescxml.org">
	
	<xsl:output method="html"
	    doctype-public="-W3CDTD XHTML 1.0 Strict//EN"
	    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" 
	    encoding="UTF-8"
	    indent="yes" />
	
	<xsl:include href="common.xsl"/>
	
	<xsl:template name="subnavigation-award-detail">
		<xsl:param name="awardId"/>
		<strong>For this Loan:</strong> <a href="{$docroot}/meteor/awardDetail.do?apsUniqAwardId={$awardId}">View Eligibility Details</a> | <a href="{$docroot}/meteor/disbursement.do?apsUniqAwardId={$awardId}">View Disbursement Details</a> | <a href="{$docroot}/meteor/references.do?apsUniqAwardId={$awardId}">View References</a> | <a href="{$docroot}/meteor/repaymentDetail.do?apsUniqAwardId={$awardId}">View Repayment and Billing Details</a><xsl:if test="$role = 'FAA' or $inquiryRole = 'FAA'"> | <a href="{$docroot}/meteor/consolidated.do?apsUniqAwardId={$awardId}">Consolidated View</a></xsl:if>
	</xsl:template>
	
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				
				<link rel="stylesheet" type="text/css" href="{$docroot}/style/global.css"/>
				
				<script type="text/javascript" src="{$docroot}/scripts/jquery-1.6.4.min.js"></script>
				<script type="text/javascript" src="{$docroot}/scripts/jquery.simplemodal-1.4.1.js"></script>
				
				<script type="text/javascript">
					function showModal(id, opts) {
						var defaultops = { maxWidth: $(window).width() - 80, maxHeight: $(window).height() - 80 };
						$.extend(defaultops, opts);
						$('#modal_' + id).modal(defaultops);
						return false;
					}
					
					$(document).ready( function() {
						var $inputSSN = $(".inputSSN input.SSN");
						$inputSSN.data('startingValue', $inputSSN.val())
						$inputSSN.focus( function() {
							if ($(this).val() == $inputSSN.data('startingValue')) {
								$(this).val("");
							}
						});
						
						$inputSSN.blur( function() {
							if ($(this).val() == "") {
								$(this).val($inputSSN.data('startingValue'));
							}
						});
					});
				</script>
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
										<div class="studentInfo">
										<xsl:choose>
											<xsl:when test="($person='student')">
												<p>Student Name: <strong><xsl:value-of select="//Award[Student/SSNum/@unmasked=$ssn]/Student/FirstName"/>&#32;<xsl:value-of select="//Award[Student/SSNum/@unmasked=$ssn]/Student/MiddleInitial"/>&#32;<xsl:value-of select="//Award[Student/SSNum/@unmasked=$ssn]/Student/LastName"/>&#32;(<xsl:value-of select="//Award[Student/SSNum/@unmasked=$ssn]/Student/SSNum"/>)</strong></p>
											</xsl:when>
											<xsl:when test="($person='borrower') and (count(//Borrower) > 0)">
												<p>Borrower Name: <strong><xsl:value-of select="//Award[Borrower/SSNum/@unmasked=$ssn]/Borrower/FirstName"/>&#32;<xsl:value-of select="//Award[Borrower/SSNum/@unmasked=$ssn]/Borrower/MiddleInitial"/>&#32;<xsl:value-of select="//Award[Borrower/SSNum/@unmasked=$ssn]/Borrower/LastName"/>&#32;(<xsl:value-of select="//Award[Borrower/SSNum/@unmasked=$ssn]/Borrower/SSNum"/>)</strong></p>
											</xsl:when>
										</xsl:choose>
										<xsl:if test="$role = 'FAA' or $role = 'APCSR' or $role = 'LENDER'">
											<div class="inputSSN">
												<form method="POST" action="{$docroot}/meteor/summary.do">
													<div>
														<input type="text" class="SSN" name="ssn" value="Enter New SSN" /><input type="image" src="{$docroot}/imgs/btn-ssn.jpg" class="btn" />
													</div>
													<xsl:if test="$role = 'APCSR' or $role = 'LENDER'">
													<div style="clear: left">
														View as: <input type="radio" name="inquiryRole" value="BORROWER"><xsl:if test="$inquiryRole != 'FAA'"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if></input>Borrower <input type="radio" name="inquiryRole" value="FAA"><xsl:if test="$inquiryRole = 'FAA'"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if></input>FAA
													</div>
													</xsl:if>
												</form>
											</div>
										</xsl:if>
										</div>
										
										<div class="navigation">
											<ul>
												<li class="navA"><a href="{$docroot}/meteor/summary.do">View Student Loans and Other Financial Aid Awards</a></li>
												<li class="navB"><a href="{$docroot}/meteor/repaymentSummary.do">View Repayment and Billing Summary</a></li>
												<xsl:if test="$role = 'FAA' or $inquiryRole = 'FAA'">
													<li class="navC"><a href="{$docroot}/meteor/aversion.do">View Default Aversion Request, Claim and Default Details</a></li>
												</xsl:if>					
											</ul>
										</div>
										<div class="subnavigation">
											<xsl:call-template name="subnavigation"/>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>			
					<tr>
						<td width="20" class="white noPad"><img src="{$docroot}/imgs/spacer.gif" width="20px" border="0" /></td>
						<td class="white content">
							<xsl:apply-templates select="pescxml:MeteorRsMsg"/>
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
	