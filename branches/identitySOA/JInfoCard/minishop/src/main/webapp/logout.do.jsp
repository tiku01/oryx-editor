<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="error.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>
</head>
<%
	try {
		String status = (String)session.getAttribute("USER_STATUS");
		if (status.equals("LOGGED_IN")) {
			// invalidate the data structures !!!!!
			session.setAttribute("USER_STATUS", "LOGGED_OUT");
			session.setAttribute("cardid", null);
			response.sendRedirect("index.jsp");
		}
	} catch (Exception ex) {
		System.err.println("caught ex: " + ex.toString());
	}
%>
<body>
</body>
</html>
