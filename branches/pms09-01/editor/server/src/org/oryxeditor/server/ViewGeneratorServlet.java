/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/
package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;
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




