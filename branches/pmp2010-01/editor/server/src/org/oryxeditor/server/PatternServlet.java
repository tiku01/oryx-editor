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
		boolean modify = new Boolean(req.getParameter("modify"));
		
		if (delete) {
			deletePattern(req, resp);
		} else if (modify) {
			modifyPatternDescription(req, resp);
		} else {
			saveNewPattern(req, resp);
		}
	}
	
	private void modifyPatternDescription(HttpServletRequest req,
			HttpServletResponse resp) {
		int id = new Integer(req.getParameter("id"));
		String desc = req.getParameter("description");
		String ssNameSpace = req.getParameter("ssNameSpace");
		
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		if (repos.changePatternDescription(id, desc) == null){
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);			
		}
	}

	private void saveNewPattern(HttpServletRequest req, HttpServletResponse resp) {
		String serPattern = req.getParameter("serPattern");
		String ssNameSpace = req.getParameter("ssNameSpace");
		String description = req.getParameter("description");
		
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		repos.saveNewPattern(serPattern, description);
		
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
	 * Expects parameter ssNameSpace for the patterns of the desired namespace
	 * Returns the patterns as an array and pattern objects. 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String ssNameSpace = req.getParameter("ssNameSpace");
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		List<Pattern> patternList = repos.getPatterns();
		
		resp.setContentType("application/json");
		
		resp.getWriter().print(patternsToJson(patternList));
	}

	private String patternsToJson(List<Pattern> patternList) {
		String result = "[";
		
		ListIterator<Pattern> it = patternList.listIterator();
		
		while(it.hasNext()) {
			result += it.next().toJSONString();			
			if (it.hasNext()) result += ", ";
		}
		
		result += "]";
		
		return result;
	}
}
