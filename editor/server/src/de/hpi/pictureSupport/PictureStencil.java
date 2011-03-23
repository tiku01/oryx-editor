package de.hpi.pictureSupport;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;

public class PictureStencil extends XMLConvertible {

	// TODO write some "writeJSON..." methods, that write necessary parts into the given JSON
	@Attribute(name="id")
	protected String id;
	
	public String getId(){
		return id;
	}
	
	public void setId(String newId){
		id = newId;
	}
}
