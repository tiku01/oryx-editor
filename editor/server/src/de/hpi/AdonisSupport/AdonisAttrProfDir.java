package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT ATTRPROFDIR (ATTRIBUTEPROFILE*, ATTRPROFDIR*)>
//<!ATTLIST ATTRPROFDIR
//  name CDATA #REQUIRED
//>


@RootElement("ATTRPROFDIR")
public class AdonisAttrProfDir extends XMLConvertible {

	private static final long serialVersionUID = -7501442084178668626L;

	@Attribute("name")
	protected String name;
	
	@Element(name="ATTRIBUTEPROFILE", targetType=AdonisAttributeProfile.class)
	protected ArrayList<AdonisAttributeProfile> attributeProfile;
	
	@Element(name="ATTRPROFDIR", targetType=AdonisAttrProfDir.class)	
	protected ArrayList<AdonisAttrProfDir> attrProfDir;

	public void setAttributeProfile(ArrayList<AdonisAttributeProfile> list){
		attributeProfile = list;
	}
	
	public ArrayList<AdonisAttributeProfile> getAttributeProfile(){
		return attributeProfile;
	}
	
	public void setAttrProfDir(ArrayList<AdonisAttrProfDir> list){
		attrProfDir = list;
	}
	
	public ArrayList<AdonisAttrProfDir> getAttrProfDir(){
		return attrProfDir;
	}
	
	public void setName(String value){
		name = value;
	}
	
	public String getName(){
		return name;
	}

	public void writeJSON(JSONObject json) throws JSONException {
		JSONObject tempJSON = new JSONObject();
		tempJSON.put("name",getName());
		json.put("attrProfDir", tempJSON);
		
	}

//	@Override
//	public void write(JSONObject json) throws JSONException {
//		json.object();
//		json.key("name").value(getName());
//		json.key("attrProfDir_children").array();
//		for (AdonisAttributeProfile aAttributeProfile : getAttributeProfile()){
//			aAttributeProfile.write(json);
//		}
//		for (AdonisAttrProfDir aAttrProfDir : getAttrProfDir()){
//			aAttrProfDir.write(json);
//		}
//		json.endArray();
//		json.endObject();
//	}
	

}
