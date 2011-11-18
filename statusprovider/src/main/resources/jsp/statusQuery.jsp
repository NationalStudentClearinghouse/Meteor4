<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Meteor Status Provider</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="google" value="notranslate">
		
		<link rel="stylesheet" type="text/css" href="<spring:url value="/style/global.css"/>"/>
		<script type="text/javascript" src="<spring:url value="/scripts/jquery-1.6.4.min.js"/>"></script>
		
		<script type="text/javascript">
			$(document).ready( function() {
				$("#checkAll").click( function(e) {
					$(".checkboxes input[type='checkbox']").attr('checked', true);
					
					e.preventDefault();
				});
				
				$("#uncheckAll").click( function(e) {
					$(".checkboxes input[type='checkbox']").attr('checked', false);
					
					e.preventDefault();
				});
			});
		</script>
		
		<style type="text/css">
			.checkboxes span {
				display: block;
			}
		</style>
	</head>
	<body>
		<table cellpadding="0" cellspacing="0" width="100%" class="tblContainer">
			<tr>
				<td width="20" height="27" class="noPad"><img src="<spring:url value="/imgs/corner-top-left.jpg"/>" border="0" /></td>
				<td height="27" class="orange noPad"><img src="<spring:url value="/imgs/spacer.gif"/>" height="27" width="1" border="0" /></td>
				<td width="20" height="27" class="noPad"><img src="<spring:url value="/imgs/corner-top-right.jpg"/>" border="0" /></td>
			</tr>
			<tr>
				<td class="white bkHeader" colspan="3">
					<table cellpadding="0" cellspacing="0" width="100%" class="tblHeader">
						<tr>							
							<td class="tdHeader" width="100%">
								<div class="logo"><a href="#" title="Meteor Network"><img src="<spring:url value="/imgs/logo.jpg"/>" border="0" /></a></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>			
			<tr>
				<td width="20" class="white noPad"><img src="<spring:url value="/imgs/spacer.gif"/>" width="20px" border="0" /></td>
				<td class="white content">
					<h1>Data Provider Status</h1>
					<c:choose>
					<c:when test="${not empty command.error}">
						<b>Error: <c:out value="${command.error}"/></b>
					</c:when>
					<c:otherwise>
					<form:form>
						<div><a href="#" id="checkAll">Check All</a> | <a href="#" id="uncheckAll">Uncheck All</a></div>
						<br/>
						<div class="checkboxes">
							<form:checkboxes items="${command.dataProviders}" path="dataProvidersToQuery" itemLabel="id" itemValue="id"/>
						</div>
						<br/>
						<input type="submit"/>
					</form:form>
					</c:otherwise>
					</c:choose>
				</td>
				<td width="20" class="white noPad"><img src="<spring:url value="/imgs/spacer.gif"/>" width="20px" border="0" /></td>
			</tr>
			<tr>
				<td width="20" height="27" class="noPad"><img src="<spring:url value="/imgs/corner-bottom-left.jpg"/>" border="0" /></td>
				<td height="27" class="orange noPad"><img src="<spring:url value="/imgs/spacer.gif"/>" height="27" width="1" border="0" /></td>
				<td width="20" height="27" class="noPad"><img src="<spring:url value="/imgs/corner-bottom-right.jpg"/>" border="0" /></td>
			</tr>		
		</table>
	</body>
</html>