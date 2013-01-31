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
					<h1>Data Provider Results</h1>
					<c:choose>
					<c:when test="${not empty status.error}">
						<b>Error: <c:out value="${status.error}"/></b>
					</c:when>
					<c:otherwise>
						<table>
							<col width="300px"/>
							<col width="250px"/>
							<col width="250px"/>
							<col width="80px"/>
							<col width="*"/>
							<thead>
								<tr>
									<th>Data Provider</th>
									<th>Start Time</th>
									<th>End Time</th>
									<th>Elapsed Time</th>
									<th>Messages</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items="${status.dataProviders}" var="dp">
								<tr>
									<td>${dp.description} (${dp.id})</td>
									<td>${dp.startTime}</td>
									<td>${dp.endTime}</td>
									<td>${dp.elapsedTime}</td>
									<td>${dp.message}</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</c:otherwise>
					</c:choose>
					<br/>
					<div>
						<a href="<spring:url value="/meteor/status"/>">New Query</a>
					</div>
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