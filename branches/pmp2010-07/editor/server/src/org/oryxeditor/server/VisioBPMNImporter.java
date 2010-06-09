package org.oryxeditor.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import de.hpi.visio.VisioToBPMNConverter;

public class VisioBPMNImporter extends HttpServlet {

	private static final long serialVersionUID = -5602000015928255933L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/xml");
		String data = req.getParameter("data");
		//TODO solve the path problem
		VisioToBPMNConverter converter = new VisioToBPMNConverter(this.getServletContext().getRealPath("/")+"execution/");
		try {
			converter.importVisioData(data);
		} catch (Exception e) {
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
	
}
