package org.oryxeditor.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import de.hpi.pattern.Pattern;
import de.hpi.pattern.PatternFilePersistance;
import de.hpi.pattern.PatternPersistanceException;
import de.hpi.pattern.PatternPersistanceProvider;

public class PatternServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4767989831008935231L;
	/**
	 * 
	 */
	private static final String baseDir = "/Applications/apache-tomcat-6.0.26/webapps/oryx/pattern/";

	/**
	 * 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//get the requested http method
		//prototype forwards puts and deletes as POSTS with _method parameter containing original requestmethod
		String method = req.getParameterMap().containsKey("_method") ? 
						req.getParameter("_method").toUpperCase() :
						"POST";
		
		String patternJSON = req.getParameter("pattern");
		//if parameter was not included in request
		if (patternJSON == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No pattern has been supplied!");
			return; 
		}
		
		Pattern pattern = null;
		try {
			pattern = Pattern.fromJSON(patternJSON);
		} catch (JSONException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "JSON malformed!");
		}
		
		String ssNameSpace = req.getParameter("ssNameSpace");
		if (ssNameSpace == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No ssNameSpace has been supplied!");
			return; 
		}
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		
		PatternPersistanceProvider repos = null;
		try {
			repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		} catch (PatternPersistanceException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Pattern storage experienced problems."); //TODO I18N
			return;
		}
		
		Pattern savedPattern = pattern;
		try {
			if (method.equals("POST")) {
				savedPattern = repos.replacePattern(pattern);
			} else if (method.equals("PUT")) {
				savedPattern = repos.addPattern(pattern);
			} else if (method.equals("DELETE")) {
				repos.removePattern(pattern);
				return;
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Unsupported _method!");
				return;
			}
		} catch (PatternPersistanceException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not commit change!");
		}
		
		//Response for POST and PUT. DELETE does not return anything.
		resp.getWriter().println(savedPattern.toJSONString());
	}
	
	/**
	 * Expects parameter ssNameSpace for the patterns of the desired namespace
	 * Returns the patterns as an array and pattern objects. 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String ssNameSpace = req.getParameter("ssNameSpace");
		//if ssNameSpace has not been supplied
		if (ssNameSpace == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ssNameSpace parameter missing!");
			return;
		}
		
		PatternPersistanceProvider repos = null;
		try {
			repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		} catch (PatternPersistanceException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Pattern storage experienced problems."); //TODO I18N
		}
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		resp.getWriter().print(repos.toJSONString());  //TODO implement stencilset extension
	}
}
