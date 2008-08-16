package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CollectionHandler extends HandlerBase {
	
    // Returns a date object representing the input string 
	// TODO: just make it better
	protected Date parseDate(String strDate, boolean set1970OnError) {
    	if (strDate != null){
    		try {
				return DateFormat.getDateInstance().parse(strDate);
			} catch (ParseException e) {
				Calendar c = Calendar.getInstance();
				c.set(1970, 1, 1);
				return c.getTime();
			}
    	}
    	else {
			if (set1970OnError) {
	    		Calendar c = Calendar.getInstance();
				c.set(1970, 1, 1);
				return c.getTime();
			}
			else {
				return new Date(); // Return a date from now
			}
    	}
    }
	
	protected boolean checkParameter(HttpServletRequest req, String parameter) {
		if (req.getParameter(parameter) != null) {
			return req.getParameter(parameter).equals("true");
		}
		else {
			return false;
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws IOException {
    	
    	Date from  = this.parseDate(req.getParameter("from"), true);
    	Date to = this.parseDate(req.getParameter("to"), false);
   
    	boolean owner = this.checkParameter(req, "owner");
    	boolean is_shared = this.checkParameter(req, "is_shared");
    	boolean contributor = this.checkParameter(req, "contributor");
    	boolean reader= this.checkParameter(req, "contributor");
    	boolean is_public = this.checkParameter(req, "is_public");
    	
        String type = null;
        if (req.getParameter("type") != null) {
        	type = req.getParameter("type");
        }
        else {
        	type = "%";
        }
        
        // Run query
        List<Representation> models = subject.getModels(type, from, to, owner, 
        		is_shared, is_public, contributor, reader);
        
        // Collect meta data as json
        JSONArray output = new JSONArray();
        for (Representation model : models) {
        	output.put(this.getModelMetaData(subject, object, req));
        }
        // Write json to output stream
        try {
			output.write(res.getWriter());
		} catch (JSONException e) {e.printStackTrace();}
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws IOException {
    	Identity identity = Identity.newModel(subject, 
    			req.getParameter("title"), 
    			req.getParameter("type"), 
    			req.getParameter("mime_type"), 
    			req.getParameter("language"), 
    			req.getParameter("summary"), 
    			req.getParameter("svg"), 
    			req.getParameter("content")); 
    			
    	res.setStatus(201); // Http status "created"
    	res.setHeader("location", identity.getUri()); // return the model uri to the editor
	}
}