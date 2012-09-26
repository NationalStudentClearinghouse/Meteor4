<%@ page isELIgnored="false" %><%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %><% 
response.setHeader("Content-Type", "multipart/mixed; boundary=education_mydataUniq1232");
response.setHeader("Content-Disposition", "attachment; filename=MeteorData.dat");
%>
Content-Type: multipart/mixed; boundary=education_mydataUniq1232

--education_mydataUniq1232
Content-Type: text/xml

<c:out value="${meteorDataDownload}" escapeXml="false"/>

--education_mydataUniq1232--
