import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Echo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4453724722118567472L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		String xmlToken = req.getParameter("xmlToken");
		
		try {
			res.getOutputStream().println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
			res.getOutputStream().println(xmlToken);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
