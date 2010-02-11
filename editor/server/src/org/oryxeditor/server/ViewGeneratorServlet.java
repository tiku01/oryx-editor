package org.oryxeditor.server;


import java.io.*;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.ViewGenerator.ViewGenerator;


public class ViewGeneratorServlet extends HttpServlet {

	private static final long serialVersionUID = -2308798783469734955L;
	private String baseURL = "viewgenerator/";
	private String oryxRootDirectory = "C:\\Programme\\Apache Software Foundation\\Tomcat 6.0\\webapps\\oryx\\";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		doGetOrPost(req, resp); 	
	} 
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGetOrPost(req, resp); 
	} 
	
	private boolean isCorrectlySet(HttpServletRequest req, String paramName) {
		String value = req.getParameter(paramName); 
		if ((value == null) || ("".equals(value))) {
			return false;
		}
		return true;
	}
	
	private void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String baseParamName = "d";
		int count = 0;
		String value = req.getParameter(baseParamName + count); 
		PrintWriter out = resp.getWriter(); 
		resp.setContentType("text/plain"); 
		if (value == null) { 
			out.println("There was no input diagrams parameter d0 given.");
		} 
		else if ("".equals(value)) {
			// The request parameter 'param' was present in the query string but has no value 
			// e.g. http://hostname.com?param=&a=b  
			out.println("The input diagrams parameter was empty, no diagrams were selected as input.");
		}
		else {
			ArrayList<String> diagramIds = new ArrayList<String>();
			diagramIds.add(value.replace(" ", "%20"));
			count +=1;
			
			while (this.isCorrectlySet(req, baseParamName+count)) {
				value = req.getParameter(baseParamName + count).replace(" ", "%20");
				diagramIds.add(value);
				count +=1;
			}
			
//			out.println(diagramIds);
			ViewGenerator viewGenerator = new ViewGenerator(oryxRootDirectory, baseURL.replace("/", "\\"));
			viewGenerator.generate(diagramIds);
			
			resp.sendRedirect(baseURL + viewGenerator.getOverviewHTMLName());

			out.close(); 			 
		}
	}	
}




