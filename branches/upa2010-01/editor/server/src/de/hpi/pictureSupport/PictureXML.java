package de.hpi.pictureSupport;

//import java.util.HashMap;
//import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.pictureSupport.Logger;

public class PictureXML extends XMLConvertible {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void writeJSON(JSONObject json){
		//nothing to do
	}
	
	public Vector<JSONObject> writeJSON() throws JSONException{
		Logger.i("writeDiagrams");
      
		
		Vector<JSONObject> jsonDiagrams = new Vector<JSONObject>();
		//JSONObject json = null;
		
		//Map<String,String> inheritedProperties = new HashMap<String,String>();
		//inheritedProperties.put("version",getVersion());
		
		/*for (PictureModel aModel : getModels().getModel()){
			//pass global information to models
			aModel.getInheritedProperties().putAll(inheritedProperties);
			
			
			Logger.i("write Model "+aModel.getName());
			
			json = new JSONObject();
			//set the appropriate language to translate correctly
			aModel.setLanguage(Unifier.getLanguage(aModel.getModeltype()));
			aModel.writeJSON(json);
			jsonDiagrams.add(json);
		}*/
		
		return jsonDiagrams;
	}
}
