<%@ page %><% 
response.setHeader("Content-Type", "multipart/mixed; boundary=education_mydata");
response.setHeader("Content-Disposition", "attachment; filename=MeteorData.dat");
%>--education_mydata
Content-type: text/plain; charset=us-ascii
NAME: Jane Doe
STREET1: 123 Main Street
CITY: Anywhere
STATE: CA
COLLEGE: California State University
START_YEAR: 2002
END_YEAR: 2006
DEGREE_LEVEL: Master, Arts
DEGREE: Education
GPA: 3.85
