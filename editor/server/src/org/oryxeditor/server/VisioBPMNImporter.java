package org.oryxeditor.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import de.hpi.visio.VisioToJSONConverter;

public class VisioBPMNImporter extends HttpServlet {

	private static final long serialVersionUID = -5602000015928255933L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/xml");
		String data = request.getParameter("data");
		//TODO solve the path problem
		VisioToJSONConverter converter = new VisioToJSONConverter(this.getServletContext().getRealPath("/"));
		try {
			String result = converter.importVisioData(data);
			response.getWriter().print(result);
			response.setStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
	
	// testing purpose
	public static void main(String[] args) {
		try {
			String testFilePath = "/Users/Thamsen/Desktop/Subprocesses.vdx";
			byte[] buffer = new byte[(int) new File(testFilePath).length()];
		    BufferedInputStream f = new BufferedInputStream(new FileInputStream(testFilePath));
		    f.read(buffer);
		    VisioToJSONConverter converter = new VisioToJSONConverter("/Users/Thamsen/Workspaces/oryx/oryx/editor/data/");
		    String result = converter.importVisioData(new String(buffer));
		    System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Put a good vdx in place...");
		}
	}
	
}
