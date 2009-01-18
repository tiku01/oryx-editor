package org.oryxeditor.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.bpt.abstraction.TriAbstraction;
import de.hpi.bpt.abstraction.TriAbstractionStepInfo;
import de.hpi.bpt.hypergraph.abs.IGObject;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;

/**
 * Copyright (c) 2008 Artem Polyvyanyy
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
@SuppressWarnings("serial")
public class AbstractionServlet extends HttpServlet {

	/**
	 * Process abstraction POST request from ORYX editor 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		res.setStatus(200);
		
		String erdf = req.getParameter("erdf");
		String id = req.getParameter("shape_id");
		
		Process p = new Process();
		try {
			p.parseERDF(erdf);
		} catch (Exception e) {}
		
		TriAbstraction<ControlFlow,Node> triAbs = new TriAbstraction<ControlFlow,Node>(p);
		TriAbstractionStepInfo info = triAbs.getAbstractionInfo(id);
		
		Collection<String> ids = new ArrayList<String>();
		Iterator<IGObject> i = info.getFragment().iterator();
		while (i.hasNext())
			ids.add(i.next().getId());
		
		JSONObject response = new JSONObject();
		try {
			response.put("fragment", new JSONArray(ids));
			response.put("entry",info.getEntry());
			response.put("exit",info.getExit());
		} catch (JSONException e) {}
		
		
		
		res.setContentType("text/json");
		
		res.getWriter().println(response);
	}
}
