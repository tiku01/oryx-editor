package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT MODELS (MODEL+)>


@RootElement("MODELS")
public class AdonisModels extends XMLConvertible{
	
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
	
	public void writeJSON(JSONObject json) throws JSONException{
		json.put("models",getModel());
	}

	@Override
	public void write(JSONObject json) throws JSONException {
		// do nothing -> is not part of the JSON-Diagram
	}
}
