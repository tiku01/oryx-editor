package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3mn.poem.Identity;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.HandlerWithoutModelContext;

@HandlerWithoutModelContext(uri = "/local_login")
public class LocalLogin extends HandlerBase {
	/**
	 * This servlet provides local login for Oryx-editor.
	 *
	 * 
	 *
	 * @author Gautam Sawala
	 */

	public static final String OPENID_SESSION_IDENTIFIER = "openid";
	public static final String REPOSITORY_REDIRECT = "repository";

	@Override
	public void init() {

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res,
			Identity subject, Identity object) throws Exception {
		doPost(req, res, subject, object);
	}
	//perform the post request from the form
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			Identity subject, Identity object) throws Exception {

		
		if ("true".equals( req.getParameter("mashUp") )){
    		res.getWriter().print(subject.getUri());
    		return;
    	}
		// If logout is true remove session attribute and redirect to the
		// repository
		// which will force a new login as public user
		if ("true".equals(req.getParameter("logout"))) {
			String openid = (String) req.getSession().getAttribute("openid");

			if (openid != null && openid != "" && openid != getPublicUser()) {
				User user = new User(openid);
				user.removeAuthenticationAttributes(this.getServletContext(),
						req, res);
				User publicUser = new User(getPublicUser());
				publicUser.login(req, res);
			}
			String rPage = req.getParameter("redirect");
			res.sendRedirect(rPage != null ? rPage : REPOSITORY_REDIRECT);
			return;
		}

		else {
			// Convert username to lower case in order avoid case mismatch
			String user_name = req.getParameter("user_name").toLowerCase();
			String password = req.getParameter("password");
			this.authRequest(user_name, password, req, res);
		}

	}

	// --- placing the authentication request ---
	public String authRequest(String userName, String userPassword,
			HttpServletRequest httpReq, HttpServletResponse httpResp)
			throws IOException, ServletException {
		try {
			// initialize the user with the with the provided username
			User unchecked_user = new User(userName);

			// check if the password is a match to the one in data base
			// create getPassword() to query database for the password
			// associated to database in User.java
			if (userPassword.equals(unchecked_user.getPassword())) {
				httpReq.getSession().setAttribute("openid", userName);
				Cookie identifier = new Cookie("identifier",userName);
				identifier.setPath("/");
				identifier.setMaxAge(-1);
				httpResp.addCookie(identifier);
				httpResp.sendRedirect(REPOSITORY_REDIRECT);
			}
			// if password is not a match alert user
			else {
				PrintWriter invalidResponse = httpResp.getWriter();
				httpResp.setContentType("text/html");
				invalidResponse.println("<script type = \"text/javascript\">");
				invalidResponse
						.println("alert('username and password did not match')");
				invalidResponse.println("</script>");
			}

		} catch (Exception e) {
			// present error to the user if the user with the provided username
			// does not exist in database
			throw new ServletException("the user does not exists");
		}
		return null;
	}

}