package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;


//<!ELEMENT ATTRIBUTEPROFILES (ATTRPROFDIR+ | ATTRIBUTEPROFILE+)>

@RootElement("ATTRIBUTEPROFILES")
public class AdonisAttributeProfiles extends AdonisBaseObject {


	@Element(name="ATTRIBUTEPROFILE", targetType=AdonisAttributeProfile.class)
	protected ArrayList<AdonisAttributeProfile> attributeProfile;
	
	@Element(name="ATTRPROFDIR", targetType=AdonisAttrProfDir.class)
	protected ArrayList<AdonisAttrProfDir> attrProfDir;

	public void setAttributeProfile(ArrayList<AdonisAttributeProfile> list){
		attributeProfile = list;
	}
	
	public ArrayList<AdonisAttributeProfile> getAttributeProfile(){
		if (attributeProfile == null) attributeProfile = new ArrayList<AdonisAttributeProfile>();
		return attributeProfile;
}	
		
	public void setAttrProfDir(ArrayList<AdonisAttrProfDir> list){
		attrProfDir = list;
	}
	
	public ArrayList<AdonisAttrProfDir> getAttrProfDir(){
		if (attrProfDir == null) attrProfDir = new ArrayList<AdonisAttrProfDir>();
		return attrProfDir;
	}
	
//	public void write(JSONObject json) throws JSONException{
//		JSONObject profiles = new JSONObject();
//		for (AdonisAttributeProfile aAttributeProfile : getAttributeProfile()){
//			aAttributeProfile.write(profiles);
//		}
//		for (AdonisAttrProfDir aAttrProfDir : getAttrProfDir()){
//			aAttrProfDir.write(profiles);
//		}
//		json.append("attributeProfiles", profiles);;
//	}

	@Override
	public void write(JSONObject json) throws JSONException {
		
	}
}
