package org.oryxeditor.server;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.pattern.Pattern;
import de.hpi.pattern.PatternFilePersistance;
import de.hpi.pattern.PatternPersistanceProvider;

public class PatternServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6225767840683054466L;
	private static final String baseDir = "/Applications/apache-tomcat-6.0.26/webapps/oryx/pattern/";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		boolean delete = new Boolean(req.getParameter("delete"));
		
		if (delete) {
			deletePattern(req, resp);
		} else {
			saveNewPattern(req, resp);
		}
	}
	
	private void saveNewPattern(HttpServletRequest req, HttpServletResponse resp) {
		String serPattern = req.getParameter("serPattern");
		String ssNameSpace = req.getParameter("ssNameSpace");
		
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		repos.saveNewPattern(serPattern);
		
	}

	private void deletePattern(HttpServletRequest req, HttpServletResponse resp) {
		String idString = req.getParameter("id");
		//TODO get wrong number and parse error! Return appropriate error code!
		int id = idString != null ? new Integer(req.getParameter("id")) : null;
		String ssNameSpace = req.getParameter("ssNameSpace");
		
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		repos.deletePattern(id);		
	}

	/**
	 * just for testing purposes
	 * TODO delete!
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String serPattern = req.getParameter("serPattern");
		String ssNameSpace = req.getParameter("ssNameSpace");
		resp.getWriter().println("Sie haben '" + serPattern + "' als Pattern eingegeben.");
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		
		int id = repos.saveNewPattern(serPattern);
		
		resp.getWriter().println(id);
		
		
	}
	
	/**
	 * Expects parameter ssNameSpace for the patterns of the desired namespace
	 * Returns the patterns as an array and pattern objects. 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String ssNameSpace = req.getParameter("ssNameSpace");
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		List<Pattern> patternList = repos.getPatterns();
		resp.getWriter().print(patternsToJson(patternList));
	}

	private String patternsToJson(List<Pattern> patternList) {
		String result = "[";
		
		ListIterator<Pattern> it = patternList.listIterator();
		
		while(it.hasNext()) {
			Pattern p = it.next();
			
			result += "{'id': " + p.getId() + ", ";
			result += "'serPattern': '" + p.getSerPattern() + "', ";
			result += "'imageUrl': '" + p.getImageUrl() + "'}";
			
			if (it.hasNext()) result += ", ";
		}
		
		result += "]";
		
		return result;
	}
}
