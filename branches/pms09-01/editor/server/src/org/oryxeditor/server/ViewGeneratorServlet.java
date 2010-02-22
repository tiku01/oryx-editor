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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.ViewGenerator.ViewGenerator;


public class ViewGeneratorServlet extends HttpServlet {

	private static final long serialVersionUID = -2308798783469734955L;
	private String baseURL = "viewgenerator/";
//	private String oryxRootDirectory = "C:\\Programme\\Apache Software Foundation\\Tomcat 6.0\\webapps\\oryx\\";
	private String oryxRootDirectory;
	
	
	@Override
	public void init() throws ServletException {
		super.init();
		oryxRootDirectory = this.getServletContext().getRealPath("") + File.separator;
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		doGetOrPost(req, resp); 	
	} 
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGetOrPost(req, resp); 
	} 

	
	private void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String paramName = "modeluris";
		String[] value = req.getParameterValues(paramName); 
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/plain"); 
		if (value == null) {
			resp.sendError(400);
			out.println("There was no input diagrams parameter " + paramName + " given.");
		} 
		else if ("".equals(value)) {
			// The request parameter 'param' was present in the query string but has no value 
			// e.g. http://hostname.com?param=&a=b 
			resp.sendError(400);
			out.println("The input diagrams parameter was empty, no diagrams were selected as input.");
		}
		else {
			ArrayList<String> diagramIds = new ArrayList<String>();
			for (int i=0;i<value.length;i++) {
				diagramIds.add(value[i].replace(" ", "%20"));
			}
			long current = System.currentTimeMillis();
			File f = new File(oryxRootDirectory +  baseURL.replace("/", File.separator)+current+File.separator);
			f.mkdirs();
			ViewGenerator viewGenerator = new ViewGenerator(oryxRootDirectory, baseURL.replace("/", File.separator)+current+File.separator);
			viewGenerator.generate(diagramIds);
			resp.setStatus(200);
//			resp.sendRedirect(baseURL + viewGenerator.getOverviewHTMLName())
			String url = req.getServerName()+":" + req.getServerPort() +"/oryx"+"/"+ baseURL + current + "/" + viewGenerator.getOverviewHTMLName();
			out.write(url);
		}
		out.close(); 			 
	}
}




