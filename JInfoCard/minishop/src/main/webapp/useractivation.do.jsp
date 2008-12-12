<%@ page contentType="text/html; charset=iso-8859-1" language="java" 
	import="java.sql.*, de.fraunhofer.fokus.jic.bioshop.*, de.fraunhofer.fokus.jic.rp.*" 
	errorPage="error.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.util.Iterator"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>User activation Page</title>
</head>
<%
	try {
		String key = request.getParameter("do.update.key");
		System.out.println("Length " + key.length());
		if (key.length() != 20) {
			session.setAttribute("ERR_CODE", "500");
			session.setAttribute("ERR_TEXT", "The activation Key is not correct! Please use the key from the Email.");
			session.setAttribute("ERR_LINK_TITLE", "Please activate your account again.");
			session.setAttribute("ERR_LINK","profile_cs.jsp");
			response.sendRedirect("error.jsp");
		} else {
			String uid = (String)session.getAttribute("USER_ID");
			System.out.println("got uid from session: " + uid);
			IBioMiniShopUserManager mgr = BioMiniShopUserManager.getInstance();
			BioMiniShopUserData _data = mgr.getUserData(uid);
			if (_data.getActivateKey().compareTo(key) == 0) {
				_data.setActivateKey("");
				mgr.updateUserData(_data);
				response.sendRedirect("home.jsp");
			} else {
				session.setAttribute("ERR_CODE", "5000");
				session.setAttribute("ERR_TEXT", "The activation Key is not correct! Please use the key from the Email.");
				session.setAttribute("ERR_LINK_TITLE", "Please activate your account again.");
				session.setAttribute("ERR_LINK","profile_cs.jsp");
				response.sendRedirect("error.jsp");
			}
		}

	} catch (Exception ex) {
		System.err.println("caught ex: " + ex.toString());
	}
%>
<body>
</body>
</html>