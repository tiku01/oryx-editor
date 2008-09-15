package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.JavaBeanJsonTransformation;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelInfoHandler extends  HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		if (object != null) {
			Model model = new Model(object.getId());
			Collection<String> attributes = new ArrayList<String>();
			attributes.add("title");			
			attributes.add("summary");
			attributes.add("type");
			attributes.add("creationDate");
			attributes.add("lastUpdate");
			JSONObject data = JavaBeanJsonTransformation.toJsonObject(model, attributes);
			data.put("thumbnailUri", this.getServerPath(request) + model.getUri() + "/png");
			// Create an envelop to be able to return results for more than one model later
			JSONObject envelop = new JSONObject(); 
			envelop.put(String.valueOf(model.getId()), data);	
			envelop.write(response.getWriter());
			response.setStatus(200);
		}
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

	}

}
