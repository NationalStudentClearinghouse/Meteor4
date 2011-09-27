<%@ page import="java.io.*,
				 java.security.*,
				 java.util.ResourceBundle,
				 org.apache.cxf.common.util.Base64Exception,
				 org.apache.cxf.common.util.Base64Utility,
				 org.meteornetwork.meteor.common.util.DigitalSignatureManager,
				 org.meteornetwork.meteor.common.util.PrivateKeyParams,
				 org.meteornetwork.meteor.saml.Role,
				 org.meteornetwork.meteor.saml.SecurityToken,
				 org.meteornetwork.meteor.saml.SecurityTokenImpl"%>

<%
	
	/* The Assurance Level your authentication process has been assigned by the MAT after review.*/
	String strAssuranceLevel = "3";
	
	/* Get request values */
	
	/* User Organization Data should be included and can be used by data providers to filter alternative loans for schools.*/
	String organizationType = request.getParameter("OrgType");
	String organizationId = request.getParameter("OrgId");
	String organizationIdType = request.getParameter("OrgIdType");
	
	/* When logging in a lender include the lender's OPEID to permit the user to see all data they are authorized to view.*/
	String lenderOPEID = request.getParameter("LOPEID");
	
	/* Basic Meteor user required information.*/
	String userRole = request.getParameter("Role");
	String borrowerSsn = request.getParameter("SSN");
	
	/* Required in SecurityToken for borrower role only.*/
	String username = request.getParameter("username");
	
	/* Use this or some other unique ID associated with it as opaque ID.*/
	String password = request.getParameter("password");
	
	/* Get Keystore Information from authentication.properties to access your private key.*/
	ResourceBundle res = ResourceBundle.getBundle("authentication");
	/* The URL for the access provider you (as a Meteor authentication agent) will send users to.*/
	String accessProviderURL = res.getString("authentication.accessProviderURL");
	String keystoreType = res.getString("authentication.keystore.type");
	String keystoreFile = res.getString("authentication.keystore.file");
	String keystorePassword = res.getString("authentication.keystore.password");
	String privateKeyAlias = res.getString("authentication.privatekey.alias");
	String privateKeyPassword = res.getString("authentication.privatekey.password");
	
	/* Include your Meteor Institution ID to identify which organization is authenticating users.*/
	String authenticationAgentID = res.getString("authentication.identifier");
	String authenticationProcessID = res.getString("authentication.process.identifier");
	//boolean shouldSign = (!"No".equalsIgnoreCase(res.getString("authentication.signassertion", "Yes")));
	
	/****************************** FOR TEST / DEBUGGING USE ONLY *************************************/
	System.out.println("authentication.keystore.type=" + keystoreType);
	System.out.println("authentication.keystore.file=" + keystoreFile);
	System.out.println("authentication.privatekey.alias=" + privateKeyAlias);
	System.out.println("authentication.identifier=" + authenticationAgentID);
	System.out.println("authentication.process.identifier=" + authenticationProcessID);
	//System.out.println("authentication.signassertion (Yes=true) converted to:" + Boolean.toString(shouldSign));
	/****************************** FOR TEST / DEBUGGING USE ONLY *************************************/
	
	/**************************************************************************/
	/* Before creating and signing a SecurityToken (aka SAML assertion) for    */
	/* Meteor the username and password must be validated against the system   */
	/* containing your users.  Continue if the username and password are valid */
	/* or stop before creating and signing a SecurityToken if the user supplied*/
	/* credentials are not valid.                                              */
	/*************************************************************************/
	
	/* Create a new SecurityToken object for Meteor.*/
	
	/* This object represents a SAML Assertion and can be serialized to produce the XML.*/
	SecurityToken token = new SecurityTokenImpl();
	
	try {
		token.setAuthenticationProcessId(authenticationProcessID); /* assigned / approved by the MAT after review */
		token.setUserHandle(username); /* aka opaque ID which is used to uniquely identify each user */
		token.setLevel(Integer.parseInt(strAssuranceLevel)); /* as assigned by the MAT */
		token.setSubjectName(authenticationAgentID); /* authenticing organization's Meteor Institution ID */

		if (userRole.equalsIgnoreCase(Role.FAA.getName())) {
			token.setRole(Role.FAA);
			
			/* When provided this can be used to filter alt loans for schools. */
			if(organizationType != null && !organizationType.equals("") &&
				organizationId != null && !organizationId.equals("") &&
				organizationIdType != null && !organizationIdType.equals("")) {
				token.setOrganizationType(organizationType);
				token.setOrganizationId(organizationId);
				token.setOrganizationIdType(organizationIdType);
			}
		} else if (userRole.equalsIgnoreCase(Role.BORROWER.getName())) {
			token.setRole(Role.BORROWER);
			token.setSsn(borrowerSsn);
		} else if (userRole.equalsIgnoreCase(Role.APCSR.getName())) {
			token.setRole(Role.APCSR);
		} else if (userRole.equalsIgnoreCase(Role.LENDER.getName())) {
			token.setRole(Role.LENDER);
			
			/* Enables lookup of lender alias list maintained by NCHELP in the Meteor registry. */
			if (lenderOPEID != null && !lenderOPEID.equals("")) {
				System.out.println("Added lender ID");
				token.setLender(lenderOPEID);
			}
		} else if(userRole.equals("") || userRole == null){
			/* Invalid condition - null or empty role, not a valid Meteor SAML assertion. */
			System.out.println("Role can not be null or empty, it is required.");
		} else {
			/* Assume the role provided is valid, but it will be checked repeatedly by the Meteor software. */
			token.setRole(Role.valueOfName(userRole));
		}
	} catch (Exception e) {
		System.out.println("An error occured adding attributes to the SecurityToken: " + e.getMessage());
	}

	/* Open the keystore, read the private key, and add the key to the security token.*/
	PrivateKey privateKey = null;
	DigitalSignatureManager digitalSignatureManager = new DigitalSignatureManager();
	
	try {
		PrivateKeyParams params = new PrivateKeyParams();
		params.setKeystoreFile(keystoreFile);
		params.setKeystoreType(keystoreType);
		params.setKeystorePass(keystorePassword);
		params.setPrivateKeyAlias(privateKeyAlias);
		params.setPrivateKeyPass(privateKeyPassword);
		privateKey = digitalSignatureManager.getPrivateKey(params);
	} catch (Exception e) {
		System.out.println("An error occured while accessing the private key: " + e.getMessage());
	}
	
	String signedAssertion = digitalSignatureManager.sign(token.toXML(), privateKey, null);
%>
<html>
<head>
  <title>Meteor Example Login Code</title>
  <link type="text/css" href="css/meteorlogin.css" rel="stylesheet"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">
<table style="border: 0px none ; width: 100%;" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td align="left" valign="bottom"><a href="login.html"><img src="images/meteor.gif" border="0"></a></td>
      <td>
      <div class="PageTitle" valign="bottom" align="center">Meteor Example Login Code</div>
      </td>
      <td valign="top">
      <div align="right"><img src="images/redstripes.gif"></div>
      </td>
    </tr>
    <tr>
      <td class="NavTable-Head " colspan="3">&nbsp;</td>
    </tr>
  </tbody>
</table>
<br>
<center>
	<span style="font-family:arial, serif; font-size:18; font-weight:bold;">
		SecurityToken / SAML assertion created.
	</span>
	<p>
	<span style="font-family:arial, serif; font-size:small;">
		The SecurityToken / SAML assertion has been added to your HTTP session.
		Click the button below to continue to the Meteor Access Provider.
	</span>
	</p>
</center>

<form name="gotometeor" action="<%=accessProviderURL%>" method="POST" target="_blank">

	<input type="hidden" name="Level" value="dump"/>
	<input type="hidden" name="Ssn" value="<%=borrowerSsn%>"/>
	<input type="hidden" name="SecurityToken" value="<%=Base64Utility.encode(signedAssertion.getBytes())%>"/>
	
	<center>
	<br/>
	<input type="submit" name="submit" value="Continue to Access Provider"/>
	<br/><br/>
	<a href="login.html">Log-In Again</a>
	</center>
</form>

</body>
</html>