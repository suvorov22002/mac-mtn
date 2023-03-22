<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<% 
	// Contexte de l'application
	String contextPath = getServletContext().getContextPath();
	String uid = request.getSession().getAttribute("uid").toString();
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body onload="document.getElementById('frm').submit()">
	<form id="frm" action="<%= contextPath %>/j_security_check" method="post">
		<input type="text" name="j_username" value="<%= uid %>" />
		<input type="password" name="j_password" />
	</form>
</body>
</html>