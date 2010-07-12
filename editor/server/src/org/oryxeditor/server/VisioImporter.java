package org.oryxeditor.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import de.hpi.visio.VisioToJSONConverter;

public class VisioImporter extends HttpServlet {

	private static final long serialVersionUID = -5602000015928255933L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/xml");
		String data = request.getParameter("data");
		String action = request.getParameter("action");
		VisioToJSONConverter converter = new VisioToJSONConverter(this.getServletContext().getRealPath("/"));
		try {
			String result = converter.importVisioData(data, action);
			response.getWriter().print(result);
			response.setStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			throw new RuntimeException("Failure in attempt to import from the given .vdx-file.", e);
		}
	}
	
	
}
