package org.oryxeditor.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpStatus;

import de.hpi.visio.VisioToJSONConverter;

public class VisioImporter extends HttpServlet {

	private static final long serialVersionUID = -5602000015928255933L;

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			FileItemFactory factory = new DiskFileItemFactory();

			List<FileItem> items = new ServletFileUpload(factory).parseRequest(request);
			
			String data = items.get(0).getString("UTF-8");
			String stencil = items.get(1).getString("UTF-8");
		
			VisioToJSONConverter converter = new VisioToJSONConverter(this.getServletContext().getRealPath("/"));
			
			String result = converter.importVisioData(data, stencil);
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(result);
			response.setStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			throw new RuntimeException("Failure in attempt to import from the given .vdx-file.", e);
		}
	}
	
}
