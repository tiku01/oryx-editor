package org.oryxeditor.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import de.hpi.AdonisSupport.AdonisConverter;
import de.hpi.AdonisSupport.Log;

/**
 * Copyright (c) 2010 Markus Goetz
 * adapted by Christian Kieschnick
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class AdonisSupportServlet extends HttpServlet {
	private static final long serialVersionUID = -703248923411764562L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/xml");
		String data = req.getParameter("data");
		String debug = "";
		if (new File("adonisStandards.data").exists())
			debug = "direct";
		else if (new File("classes\\"+"adonisStandards.data").exists())
			debug = "classes\\";
			else if (new File("WEB-INF\\classes\\"+"adonisStandards.data").exists())
				debug = "WEB-INF\\classes";
				else if (new File("oryx\\WEB-INF\\classes\\"+"adonisStandards.data").exists())
					debug = "oryx\\WEB-INF\\classes";
		
		String action = req.getParameter("action");
		AdonisConverter converter = null;
		if ("Export".equals(action)) {
			converter = new AdonisConverter();
			converter.printData(debug);
			try {
				String exportString = converter.exportXML(data);
				//Log.v("Export: "+exportString);
				res.getWriter().print(exportString);
			} catch (Exception e) {
				res.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} else if ("Import".equals(action)){
			converter = new AdonisConverter();
			converter.printData(debug);
			String importString = converter.importXML(data);
			//Log.v("Import: "+importString);
			res.getWriter().print(importString);
		} else {
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
}
