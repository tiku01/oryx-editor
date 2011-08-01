package de.hpi.bpt.mashup.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ABPGeneratorProxy extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String SERVLET_PATH = "/extensions/generate"; 
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			URL url = new URL(getAddress(req));
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), req.getCharacterEncoding());
	    
	        // send the parameters
	        int i = 0;
	        for (String key:(Set<String>)req.getParameterMap().keySet()) {
	        	writer.write(key + "=" + req.getParameter(key));
	        	if (i < req.getParameterMap().keySet().size() - 1)
	        		writer.write("&");
	        }
	        writer.flush();
	        
	        // Get the response
	        int status = conn.getResponseCode();
	        StringBuffer answer = new StringBuffer();
	        BufferedReader reader = null;
	        if (status == HttpURLConnection.HTTP_OK) 
		        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		     else 
	        	reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
 	        String line;
	        while ((line = reader.readLine()) != null) {
	            answer.append(line);
	        }
	        writer.close();
	        reader.close();
	        res.getOutputStream().print(answer.toString());
	        res.setStatus(status);
		} catch (MalformedURLException e) {
			e.printStackTrace();	
			res.setStatus(500);
		} catch (IOException e) {
			e.printStackTrace();
			res.setStatus(500);
		}
	}
	
	private String getAddress(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("http://");
		buffer.append(req.getLocalName());
		buffer.append(":").append(req.getLocalPort());
		buffer.append(SERVLET_PATH);
		return buffer.toString();
	}
}
