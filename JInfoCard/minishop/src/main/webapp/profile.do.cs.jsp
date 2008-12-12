<%@ page contentType="text/html; charset=iso-8859-1" language="java" 
	import="java.sql.*, de.fraunhofer.fokus.jic.bioshop.*, de.fraunhofer.fokus.jic.rp.*" errorPage="error.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.util.Enumeration"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
</head>
<%
	try {
		String uid = (String)session.getAttribute("USER_ID");
		
		String _action_ = request.getParameter("_action_");
		
		if (_action_.equals("do.update")) {
			String fname = request.getParameter("fname");
			if (fname == null || fname.length() == 0) {
				session.setAttribute("ERR_CODE", "2001");
				session.setAttribute("ERR_TEXT", "bad input parameter: first name");
				session.setAttribute("ERR_LINK_TITLE", "show profile again");
				session.setAttribute("ERR_LINK","profile_cs.jsp");
				response.sendRedirect("error.jsp");
			}
			String lname = request.getParameter("lname");
			if (lname == null || lname.length() == 0) {
				session.setAttribute("ERR_CODE", "2002");
				session.setAttribute("ERR_TEXT", "bad input parameter: last name");
				session.setAttribute("ERR_LINK_TITLE", "show profile again");
				session.setAttribute("ERR_LINK","profile_cs.jsp");
				response.sendRedirect("error.jsp");
			}
			String email = request.getParameter("email");
			if (email == null || email.length() == 0) {
				session.setAttribute("ERR_CODE", "2003");
				session.setAttribute("ERR_TEXT", "bad input parameter: email address");
				session.setAttribute("ERR_LINK_TITLE", "show profile again");
				session.setAttribute("ERR_LINK","profile_cs.jsp");
				response.sendRedirect("error.jsp");
			}
			String pswd = request.getParameter("pswd");
			String pswd2 = request.getParameter("pswd2");
			if (pswd == null || pswd.length() == 0 || pswd2 == null || pswd2.length() == 0 || !pswd.equals(pswd2)) {
				session.setAttribute("ERR_CODE", "2005");
				session.setAttribute("ERR_TEXT", "bad input parameter: password");
				session.setAttribute("ERR_LINK_TITLE", "show profile again");
				session.setAttribute("ERR_LINK","profile_cs.jsp");
				response.sendRedirect("error.jsp");
			}
			
			IBioMiniShopUserManager mgr = BioMiniShopUserManager.getInstance();
			BioMiniShopUserData _data = mgr.getUserData(uid);	
			BioMiniShopUserData data = new BioMiniShopUserData(uid, fname, lname, email, pswd, _data.getActivateKey());
			mgr.updateUserData(data);
			response.sendRedirect("profile_cs.jsp");
		} else if (_action_.equals("do.link")) {
			if (request.getParameter("xmltoken") == null) {
				// probably the operation has been canceled by the user -> show current page again
				System.out.println("xmltoken is null");
				response.sendRedirect("profile_cs.jsp");
			} else {	
				IRPManager rpManager = null;
				String xmltoken = request.getParameter("xmltoken");
				try {
					UserDataObject udo = new UserDataObject();
					udo.setProperty("uid", uid);
					rpManager = new RPManager();
					rpManager.registerInfoCard(xmltoken, udo);
					response.sendRedirect("profile_cs.jsp");
				} catch (RPException ex) {
					ex.printStackTrace();
					session.setAttribute("ERR_CODE", ex.getErrnoAsString());
					session.setAttribute("ERR_TEXT", rpManager.getErrorMessageHandler().getErrorMessage(ex.getErrno()));
					session.setAttribute("ERR_LINK_TITLE", "home");
					session.setAttribute("ERR_LINK","home.jsp");
					response.sendRedirect("error.jsp");
				}
			}
		} else {
			// no action specified -> repeat the page
			response.sendRedirect("profile_cs.jsp");
		}
	} catch (Exception ex) {
		System.err.println("caught ex: " + ex.toString());
		ex.printStackTrace();
	}
%>
<body>
</body>
</html>
