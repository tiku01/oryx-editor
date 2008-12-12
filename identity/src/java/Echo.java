import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

public class Echo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4453724722118567472L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			String xmlToken = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
					+ req.getParameter("xmlToken");
			System.out.println("xmltoken= " + xmlToken);

			res.setContentType("text/html");
			res.getWriter().println(
					this.getRedirectPage("http://localhost:8080/backend/poem/saml", xmlToken));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String getRedirectPage(String url, String xmltoken) {
		try {
			String page = "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
					+ "<head>"
					+ "<title>Redirection with SAML token</title>"
					+ "</head>"
					+ "<body>"
					+ "<form name=\"saml-form-redirection\" action=\"" + url
					+ "\" method=\"get\" accept-charset=\"utf-8\" >"
					+ "<input type=\"hidden\" name=\"xmltoken\" value=\"" + StringEscapeUtils.escapeHtml(xmltoken) + "\"/>"
					+ "<input type=\"hidden\" name=\"returnto\" value=\"/identity/newmodel.html\"/>"
					+ "<button type=\"submit\">Login to Oryx via GET</button>" + "</form>"
					
					+ "<form name=\"saml-form-redirection\" action=\"" + url
					+ "\" method=\"post\" accept-charset=\"utf-8\" >"
					+ "<input type=\"hidden\" name=\"xmltoken\" value=\"" + StringEscapeUtils.escapeHtml(xmltoken) + "\"/>"
					+ "<input type=\"hidden\" name=\"returnto\" value=\"/identity/newmodel.html\"/>"
					+ "<button type=\"submit\">Login to Oryx via POST</button>" + "</form>"
					
					+ "<a href=\"" + url + "?xmltoken="
					+ URLEncoder.encode(xmltoken, "UTF-8")
					+ "&returnto=" + URLEncoder.encode("/identity/newmodel.html", "UTF-8")
					+ "\">Login to Oryx</a>"
					+ "</body>" + "</html>";

			return page;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
