<%@ page contentType="text/html; charset=iso-8859-1" language="java" 
	import="java.sql.*, de.fraunhofer.fokus.jic.bioshop.*, de.fraunhofer.fokus.jic.rp.*" 
	errorPage="error.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.util.Iterator"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>
</head>
<%
	try {
		String _action_ = request.getParameter("_action_");
		if (_action_.equals("uid.login")) {
			IBioMiniShopUserManager mgr = BioMiniShopUserManager.getInstance();
			String uid = request.getParameter("uid");
			String pswd = request.getParameter("pswd");
			if (mgr.login(uid, pswd)) {
				session.setAttribute("USER_STATUS", "LOGGED_IN");
				session.setAttribute("USER_ID", uid);
				response.sendRedirect("home.jsp");
			} else {
				session.setAttribute("ERR_CODE", "12");
				session.setAttribute("ERR_TEXT", "login failed ...");
				session.setAttribute("ERR_LINK_TITLE", "login again");
				session.setAttribute("ERR_LINK","login_cs.jsp");
				response.sendRedirect("error.jsp");
			}
		} else {
			String xmltoken = request.getParameter("xmltoken");
			System.out.println("got xmltoken: " + xmltoken);
			if (xmltoken == null || xmltoken.length() == 0) {
				// the posting has been cancelled
				System.out.println("sending redirection to login page -> the user has canceled the input!");
				response.sendRedirect("login_cs.jsp");
			} else {
				IRPManager rpManager = null;
				try {
					System.out.println("calling the RPManager ...");
					UserDataObject udo = null;
					rpManager = new RPManager();
					System.out.println("the length of xml token: " + xmltoken.length());
					udo = rpManager.login(xmltoken);
					// udo = rpManager.login(xmltoken);
					session.setAttribute("cardid", Utils.friendlyIdentifier(udo.getProperty("ppid")));
					String uid = udo.getProperty("uid");
					System.out.println("got uid from login: " + uid);
					session.setAttribute("USER_ID", uid);
					System.out.println("set uid session attribute");
					session.setAttribute("USER_STATUS", "LOGGED_IN");
					System.out.println("set user status");
					response.sendRedirect("home.jsp");
				} catch (RPException ex) {
					ex.printStackTrace();
					session.setAttribute("ERR_CODE", ex.getErrnoAsString());
					session.setAttribute("ERR_TEXT", rpManager.getErrorMessageHandler().getErrorMessage(ex.getErrno()));
					session.setAttribute("ERR_LINK_TITLE", "home");
					session.setAttribute("ERR_LINK","home.jsp");
					response.sendRedirect("error.jsp");
				}
			}
		}
	} catch (Exception ex) {
		System.err.println("caught ex: " + ex.toString());
	}
%>
<body>
</body>
</html>