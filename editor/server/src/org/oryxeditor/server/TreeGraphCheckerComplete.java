package org.oryxeditor.server;

import java.net.ResponseCache;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hpi.treeGraph.Diagram;
import de.hpi.treeGraph.Shape;

public class TreeGraphCheckerComplete extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			request.setCharacterEncoding("UTF-8"); 
			response.setStatus(200); // Prepare response
			
			String eRdf = request.getParameter("data"); // Extract eRdf
			
			Diagram diagram = new Diagram(eRdf);  // Run parsing

			// Get resource ids with errors
			Collection<String> errorNodeIds = diagram.getUnconnectedEdgeIds();
			
			if (diagram.getRootNodeIds().size() > 1) {
				errorNodeIds.addAll(diagram.getRootNodeIds());
			}

			// Encode ids in json array
			JSONArray jsonResponse = new JSONArray(errorNodeIds);

			// Write json to http response
			response.getWriter().println(jsonResponse.toString());

		} catch (Exception e) {
			// If something goes wrong, forward the exception
			throw new ServletException(e);
		}
	}
}
