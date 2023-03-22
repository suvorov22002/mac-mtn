<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%
	String uid = request.getParameter("uid");
	request.getSession().setAttribute("uid", uid);
	String url = "/views/xhtml/main.jsf?uid=".concat( uid );
%>
<html>
	<body>
		<jsp:forward page="<%= url %>"></jsp:forward>
	</body>
</html>
