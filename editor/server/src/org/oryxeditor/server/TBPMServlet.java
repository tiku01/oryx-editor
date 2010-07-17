/***************************************
 * Copyright (c) 2008
 * Helen Kaltegaertner
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
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONException;
import org.json.JSONObject;

/*
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
*/


public class TBPMServlet extends HttpServlet {

	private static final long serialVersionUID = 199569859231394515L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {

		res.setContentType("text/html");
		res.setStatus(200);
		
		PrintWriter out = null;
		try {
			out = res.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// No isMultipartContent => Error
		final boolean isMultipartContent = ServletFileUpload.isMultipartContent(req);
		if (!isMultipartContent) {
			printError(res, "No Multipart Content transmitted.");
			return;
		}

		final FileItemFactory factory = new DiskFileItemFactory();
		final ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		servletFileUpload.setSizeMax(-1);
		try {
			final List<?> items = servletFileUpload.parseRequest(req);
			final FileItem fileItem = (FileItem) items.get(0);
			final String fileName = fileItem.getName();
			if (! (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) ){
	    		printError(res, "No file with .png or .jpg extension uploaded.");
	    		return ;
	    	}
			JSONObject object = this.processUploadedFile(fileItem);
			System.out.println(object.getString("model"));
			object.write(out);

		} catch (FileUploadException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONObject processUploadedFile(FileItem item) {
//		String fileName = item.getName();
		String tmpPath = this.getServletContext().getRealPath("/")
				+ File.separator + "tmp" + File.separator;
		String rootDir = "/" + this.getServletContext().getServletContextName() + "/";
		JSONObject object = new JSONObject();	
		// create tmp folder
		File tmpFolder = new File(tmpPath);
		if (!tmpFolder.exists()) {
			tmpFolder.mkdirs();
		}

//		File uploadedImage = new File(tmpFolder, fileName);
		String json = "";
		try {
//			item.write(uploadedImage);
//			
//			String rootDir = "/" + this.getServletContext().getServletContextName() + "/";
//			
//			TBPM2BPMNConverter converter = new TBPM2BPMNConverter(tmpFolder + "\\"
//					+ fileName, uploadedImage, rootDir);
//			json = converter.convertImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;

	}
	
	private void printError(HttpServletResponse res, String err){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/html");
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print("{success:false, content:'"+err+"'}");
    	}
    }

	/**
	 * Performs the generation of BPMN 2.0 XML and triggers the XSLT
	 * transformation.
	 * 
	 * @param json
	 *            The diagram in JSON format
	 * @param writer
	 *            The HTTP-response writer
	 * @throws Exception
	 *             Exception occurred while processing
	 */
	protected StringWriter performTransformationToDi(String json)
			throws Exception {

		// response.setContentType("image/jpeg");

		StringWriter writer = new StringWriter();

		/* Retrieve diagram model from JSON */

//		Diagram diagram = DiagramBuilder.parseJson(json);

		/*
		 * Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram);
		 * Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
		 * 
		 * 
		 * JAXBContext context = JAXBContext.newInstance(Definitions.class);
		 * Marshaller marshaller = context.createMarshaller();
		 * marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
		 * Boolean.TRUE);
		 * 
		 * NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		 * marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
		 * nsp);
		 * 
		 * marshaller.marshal(bpmnDefinitions, writer);
		 */

		return writer;
	}

//	private static String escapeJSON(String json) {
//		// escape (some) JSON special characters
//		String res = json.replace("\"", "\\\"");
//		res = res.replace("\n", "\\n");
//		res = res.replace("\r", "\\r");
//		res = res.replace("\t", "\\t");
//		return res;
//	}

}
