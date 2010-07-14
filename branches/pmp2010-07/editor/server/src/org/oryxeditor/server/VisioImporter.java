package org.oryxeditor.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
			response.setContentType("text/plain");
			FileItemFactory factory = new DiskFileItemFactory();

			List<FileItem> items = new ServletFileUpload(factory).parseRequest(request);
			Iterator<FileItem> iterator = items.iterator();
			List<FileItem> files = new ArrayList<FileItem>();
			
			String stencil = "";
	
			while (iterator.hasNext()) {
				FileItem item = iterator.next();
				if (item.isFormField() && item.getFieldName().equals("stencil")) {
					stencil = item.getString();
				} else if (item.getFieldName().equals("vdxFile")) {
					files.add(item);
				}
			}
			
			if ("".equals(stencil) || files.get(0) == null) {
				throw new RuntimeException("Stencil set isn't supported yet or the file upload wasn't correct");
			}
			
			String data = files.get(0).getString("UTF-8");

			VisioToJSONConverter converter = new VisioToJSONConverter(this.getServletContext().getRealPath("/"));

			String result = converter.importVisioData(data, stencil);
			System.out.println(result);
			response.getWriter().print(result);
			response.setStatus(HttpStatus.SC_OK);

		
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			throw new RuntimeException("Failure in attempt to import from the given .vdx-file.", e);
		}
	}
	
}
