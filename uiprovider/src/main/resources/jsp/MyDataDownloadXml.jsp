<%@ page isELIgnored="false" %><%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %><% 
response.setContentType("text/xml");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("pragma", "no-cache");
response.setHeader("Content-Disposition", "attachment; filename=MeteorData.xml");
%><c:out value="${meteorDataDownload}" escapeXml="false"/>