package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT MODELS (MODEL+)>


@RootElement("MODELS")
public class AdonisModels extends AdonisBaseObject{
	
	@Element(name="MODEL", targetType=AdonisModel.class)
	public ArrayList<AdonisModel> children;

	public ArrayList<AdonisModel> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<AdonisModel> list){
		children = list;
	}
	
	public void writeJSON(JSONObject json) throws JSONException{
		json.put("models",getChildren());
	}

	@Override
	public void write(JSONObject json) throws JSONException {
		// do nothing -> is not part of the JSON-Diagram
	}
}
