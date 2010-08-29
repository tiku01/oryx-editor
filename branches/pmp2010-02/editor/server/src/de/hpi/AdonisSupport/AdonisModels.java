package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT MODELS (MODEL+)>


@RootElement("MODELS")
public class AdonisModels extends XMLConvertible{
	
	private static final long serialVersionUID = 615356126580445563L;
	
	@Element(name="MODEL", targetType=AdonisModel.class)
	public ArrayList<AdonisModel> model;

	public ArrayList<AdonisModel> getModel(){
		if (model == null){
			model = new ArrayList<AdonisModel>();
		}
		return model;
	}
	
	public void setModel(ArrayList<AdonisModel> list){
		model = list;
	}
	
//	public void writeJSON(JSONObject json) throws JSONException{
//		json.put("models",getModel());
//	}

	@Override
	public void writeJSON(JSONObject json) throws JSONException {
		// do nothing -> is not part of the JSON-Diagram
	}
}
