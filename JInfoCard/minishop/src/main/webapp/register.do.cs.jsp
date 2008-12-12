<%@ page contentType="text/html; charset=iso-8859-1" language="java"
	import="org.apache.commons.logging.*,java.sql.*,de.fraunhofer.fokus.jic.bioshop.*,de.fraunhofer.fokus.jic.rp.*"
 	errorPage="error.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Iterator"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>
</head>
<%
		try {

		Log log = LogFactory.getLog("register_Do_Cs");

		String uid = request.getParameter("uid");
		String xmltoken = (String) session.getAttribute("USER_TOKEN");
		log.info("got xmltoken: " + xmltoken);
		if (xmltoken == null || xmltoken.length() == 0 || uid == null
		|| uid.length() == 0) {
			// the posting has been cancelled
			log
			.info("sending redirection to register page -> the user has canceled the input!");
			response.sendRedirect("register.jsp");
		} else {
			IRPManager rpManager = null;
			try {
		log.info("getting manager instance ...");
		IBioMiniShopUserManager mgr = BioMiniShopUserManager
				.getInstance();
		if (!mgr.doesUserAlreadyExists(uid)) {
			org.apache.xml.security.Init.init();
			SAMLTokenHandler handler = new SAMLTokenHandler();
			handler.handleSAMLToken(xmltoken);
			UserDataObject udo = handler.getUnqualifiedUserDataObject();
			rpManager = new RPManager();
				udo.setProperty("uid", uid);
				String fname = udo
				.getProperty(IAssertionAttributeNames.CLAIM_NAME_GIVENNAME);
				String lname = udo
				.getProperty(IAssertionAttributeNames.CLAIM_NAME_SURNAME);
				String email = udo.getProperty(IAssertionAttributeNames.CLAIM_NAME_EMAIL_ADDRESS);
				String pswd = "" + System.currentTimeMillis();
				BioMiniShopUserData data = new BioMiniShopUserData(
				uid, fname, lname, email, pswd, "");
				if (mgr.registerUser(data)) {
			session.setAttribute("USER_STATUS",
					"LOGGED_IN");
			session.setAttribute("USER_ID", uid.trim());
			log.info("the length of xml token: "
					+ xmltoken.length());
			log.info("registering card");
			// FIXME: this doesn't work, the token is in session.getAttribute("USER_TOKEN")!
			rpManager.registerInfoCard(xmltoken, udo);
			log.info("redirecting ...");
			session.setAttribute("USER_ID", uid);
			log.info("set uid session attribute");
			session.setAttribute("USER_STATUS",
					"LOGGED_IN");
			session.setAttribute("cardid", Utils.friendlyIdentifier(udo.getProperty("ppid")));
			log.info("set user status");
			response.sendRedirect("home.jsp");
				} else {
			session.setAttribute("ERR_CODE", "1000");
			session
					.setAttribute("ERR_TEXT",
					"fatal error occured -> please contact the administrator");
			session.setAttribute("ERR_LINK_TITLE",
					"try to register again later");
			session.setAttribute("ERR_LINK",
					"register.jsp");
			response.sendRedirect("error.jsp");
				}
		} else {
			session.setAttribute("ERR_CODE", "2100");
			session.setAttribute("ERR_TEXT",
			"a user with given user id already exists");
			session.setAttribute("ERR_LINK_TITLE",
			"register again");
			session.setAttribute("ERR_LINK", "register.jsp");
			response.sendRedirect("error.jsp");
		}
			} catch (RPException ex) {
		log.info("caught ex: " + ex.toString());
		ex.printStackTrace();
		session.setAttribute("ERR_CODE", ex.getErrnoAsString());
		session.setAttribute("ERR_TEXT", rpManager
				.getErrorMessageHandler()
				.getErrorMessage(
				ex.getErrno()));
		session.setAttribute("ERR_LINK_TITLE", "home");
		session.setAttribute("ERR_LINK", "home.jsp");
		response.sendRedirect("error.jsp");
			} catch (Exception ex) {
		ex.printStackTrace();
		log.info("caught ex: " + ex.toString());
		session.setAttribute("ERR_CODE", "1000");
		session
				.setAttribute("ERR_TEXT",
				"fatal error occured -> please contact the administrator");
		session.setAttribute("ERR_LINK_TITLE",
				"try to register again later");
		session.setAttribute("ERR_LINK", "register.jsp");
		response.sendRedirect("error.jsp");
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace();
		session.setAttribute("ERR_CODE", "1000");
		session
		.setAttribute("ERR_TEXT",
				"fatal error occured -> please contact the administrator");
		session.setAttribute("ERR_LINK_TITLE",
		"try to register again later");
		session.setAttribute("ERR_LINK", "register.jsp");
		response.sendRedirect("error.jsp");
	}
%>
<body>
</body>
</html>
