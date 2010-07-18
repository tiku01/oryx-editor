package de.hpi.AdonisSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT ADOXML (ATTRIBUTEPROFILES, MODELS, APPLICATIONMODELS, MODELGROUPS)>
//<!ATTLIST ADOXML
//  version    CDATA #REQUIRED
//  date       CDATA #REQUIRED
//  time       CDATA #REQUIRED
//  database   CDATA #IMPLIED
//  username   CDATA #IMPLIED
//  adoversion CDATA #REQUIRED
//>

@RootElement("ADOXML")
public class AdonisXML extends XMLConvertible {
	
	public AdonisXML(){
		setAdoversion("Version 3.9");
		setDate("03.01.1959");
		setTime("13:37");
		setVersion("8.3");		
	}

	@Attribute(name="adoversion")
	protected String adoversion;
	public String getAdoversion(){return adoversion;}
	public void setAdoversion(String value){adoversion = value;}

	
	@Attribute(name="username")
	protected String username;
	public String getUsername(){return username;}
	public void setUsername(String value){username = value;}

	
	@Attribute(name="database")
	protected String database;
	public String getDatabase(){return database;}
	public void setDatabase(String value){database = value;}

	
	@Attribute(name="time")
	protected String time;
	public String getTime(){return time;}
	public void setTime(String value){time = value;}
	
	@Attribute(name="version")
	protected String version;
	public String getVersion(){return version;}
	public void setVersion(String value){version = value;}
	
	@Attribute(name="date")
	protected String date;
	public String getDate(){return date;}
	public void setDate(String value){date = value;}
	
	@Element(name="ATTRIBUTEPROFILES", targetType=AdonisAttributeProfiles.class)
	protected AdonisAttributeProfiles attributeProfiles;
	
	@Element(name="MODELS", targetType=AdonisModels.class)
	protected AdonisModels models;
	
	@Element(name="MODELGROUPS", targetType=AdonisModelGroups.class)
	protected AdonisModelGroups modelGroups;

	@Element(name="APPLICATIONMODELS", targetType=AdonisApplicationModels.class)
	protected AdonisApplicationModels applicationModels;

	public AdonisApplicationModels getApplicationModels(){
		if (applicationModels == null) applicationModels = new AdonisApplicationModels();
		return applicationModels;
	}
	
	public void setApplicationModels(AdonisApplicationModels model){
		applicationModels = model;
	}
	
	public AdonisModelGroups getModelGroups(){
		if (modelGroups == null) modelGroups = new AdonisModelGroups();
		return modelGroups;
	}
	
	public void setModelGroups(AdonisModelGroups group){
		modelGroups = group;
	}
	
	public AdonisModels getModels(){
		if (models == null) models = new AdonisModels();
		return models;
	}
	
	public void setModels(AdonisModels items){
		models = items;
	}
	
	public AdonisAttributeProfiles getAttributeProfiles(){
		if (attributeProfiles == null) attributeProfiles = new AdonisAttributeProfiles();
		return attributeProfiles;
	}
	
	public void setAttributeProfiles(AdonisAttributeProfiles profiles){
		attributeProfiles = profiles;
	}
	
	public Map<String,String> getAttributes(){
	//  version    CDATA #REQUIRED
	//  date       CDATA #REQUIRED
	//  time       CDATA #REQUIRED
	//  database   CDATA #IMPLIED
	//  username   CDATA #IMPLIED
	//  adoversion CDATA #REQUIRED
		Map<String,String> map = new HashMap<String,String>();
		
		return map;
	}

	public Vector<JSONObject> writeDiagrams() throws JSONException{
		Log.v("writeDiagrams");
		HashMap<String,String> modelAttributes = new HashMap<String, String>();
		modelAttributes.put("adoversion",getAdoversion());
		modelAttributes.put("date",getDate());            
		modelAttributes.put("time",getTime());            
		
		Vector<JSONObject> jsonDiagrams = new Vector<JSONObject>();
		JSONObject json = null;
		
		for (AdonisModel aModel : getModels().getChildren()){
			//pass global information to models
			aModel.setInheritedProperties(modelAttributes);
			
			Log.v("write Model "+aModel.getName());
			
			json = new JSONObject();
			aModel.write(json);
			jsonDiagrams.add(json);
		}
		
		return jsonDiagrams;
	}
}