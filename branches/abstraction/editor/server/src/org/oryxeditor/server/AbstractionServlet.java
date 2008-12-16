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

import de.hpi.bpt.abstraction.TriconnectedAbstraction;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;

/**
 * 
 * @author Artem Polyvyanyy
 *
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
		
		TriconnectedAbstraction<ControlFlow,Node> triAbs = new TriconnectedAbstraction<ControlFlow,Node>(p);
		Collection<Node> nodes = triAbs.getAbstractionCandidates(id);
		Collection<String> ids = new ArrayList<String>();
		Iterator<Node> i = nodes.iterator();
		while (i.hasNext())
			ids.add(i.next().getId());
		
		res.setContentType("text/json");
		JSONArray arr = new JSONArray(ids);
		res.getWriter().print(arr.toString());
	}
}
