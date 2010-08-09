package org.oryxeditor.server;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

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
		
		String patternJSON = req.getParameter("pattern"); //TODO catch non existent parameter
		Pattern pattern = Pattern.fromJSON(patternJSON);
		
		String ssNameSpace = req.getParameter("ssNameSpace");
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		
		if (delete) {
			deletePattern(pattern, ssNameSpace);  //TODO what about a response here???
		} else {
			resp.getWriter().println(this.savePattern(pattern, ssNameSpace).toJSONString());
		}
	}

	private Pattern savePattern(Pattern p, String ssNameSpace) {
		
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		Pattern result = repos.setPattern(p);
		
		return result;
	}

	private void deletePattern(Pattern p, String ssNameSpace) {
		PatternPersistanceProvider repos = new PatternFilePersistance(ssNameSpace, PatternServlet.baseDir);
		repos.deletePattern(p);		
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
		List<Pattern> patternList = repos.getAll();
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
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
