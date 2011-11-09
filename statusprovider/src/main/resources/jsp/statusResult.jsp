<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

</head>
<body>
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
</body>
</html>