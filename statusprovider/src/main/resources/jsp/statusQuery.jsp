<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<style type="text/css">
	.checkboxes span {
		display: block;
	}
</style>
</head>
<body>
	<c:choose>
	<c:when test="${not empty command.error}">
		<b>Error: <c:out value="${command.error}"/></b>
	</c:when>
	<c:otherwise>
		<form:form>
		<div class="checkboxes">
			<form:checkboxes items="${command.dataProviders}" path="dataProvidersToQuery" itemLabel="description" itemValue="id"/>
		</div>
		<input type="submit"/>
	</form:form>
	</c:otherwise>
	</c:choose>
</body>
</html>