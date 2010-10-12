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
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class TBPMServlet extends HttpServlet {

	private String TBPM_RECOGNITION_URL = "http://tbpm.oryx-project.org/tbpm/recognition";
	
	private static final long serialVersionUID = 199569859231394515L;
	
	@Override
	public void init() throws ServletException {
		String url = getServletContext().getInitParameter("tbpm-recognition-url");
		if(url!=null){
//			if an url is defined overwrite standard
			TBPM_RECOGNITION_URL=url;
		}
		
	}
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
			String response = this.processUploadedFile(fileItem);
			out.write(response);

		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
	}

	private String processUploadedFile(FileItem item) {
		String fileName = item.getName();
		String tmpPath = this.getServletContext().getRealPath("/")
				+ File.separator + "tmp" + File.separator;
		// create tmp folder
		File tmpFolder = new File(tmpPath);
		if (!tmpFolder.exists()) {
			tmpFolder.mkdirs();
		}

		File uploadedImage = new File(tmpFolder, fileName);
		String response = "";
		try {
			item.write(uploadedImage);
			response = this.sendRequest(uploadedImage, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}
	
	private String sendRequest(File img, String fileName){

		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
        PostMethod method = new PostMethod(TBPM_RECOGNITION_URL);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
        		new DefaultHttpMethodRetryHandler(3, false));
        String response = "";
        try {
        	Part[] parts = {new FilePart(img.getName(), img)};
            method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));

			client.executeMethod(method);
			//TODO handle response status
        	response = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        method.releaseConnection();
        return response;
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
}
