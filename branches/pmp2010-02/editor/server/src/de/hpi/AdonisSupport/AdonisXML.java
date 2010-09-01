package de.hpi.AdonisSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
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

/**
 * this class maps the outmost container of the adonis xml file which contains
 * information to the used adonis version, database, ... and several elements like
 * attribute profiles (not read) and models
 */
@RootElement("ADOXML")
public class AdonisXML extends XMLConvertible {

	private static final long serialVersionUID = -2468830739359873363L;

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
		//	if (applicationModels == null) applicationModels = new AdonisApplicationModels();
		return applicationModels;
	}
	
	public void setApplicationModels(AdonisApplicationModels model){
		applicationModels = model;
	}
	
	public AdonisModelGroups getModelGroups(){
//		if (modelGroups == null){
//			modelGroups = new AdonisModelGroups();
//		}
		return modelGroups;
	}
	
	public void setModelGroups(AdonisModelGroups group){
		modelGroups = group;
	}
	
	public AdonisModels getModels(){
//		if (models == null){
//			models = new AdonisModels();
//		}
		return models;
	}
	
	public void setModels(AdonisModels items){
		models = items;
	}
	
	public AdonisAttributeProfiles getAttributeProfiles(){
		//if (attributeProfiles == null) attributeProfiles = new AdonisAttributeProfiles();
		return attributeProfiles;
	}
	
	public void setAttributeProfiles(AdonisAttributeProfiles profiles){
		attributeProfiles = profiles;
	}
	
	/**
	 * entry point for generating diagrams for Oryx
	 * @return a collection of diagrams in JSON
	 * @throws JSONException
	 */
	public Vector<JSONObject> writeDiagrams() throws JSONException{
		Logger.i("writeDiagrams");
      
		
		Vector<JSONObject> jsonDiagrams = new Vector<JSONObject>();
		JSONObject json = null;
		
		Map<String,String> inheritedProperties = new HashMap<String,String>();
		inheritedProperties.put("adoversion",getAdoversion());
		inheritedProperties.put("database",getDatabase());            
		inheritedProperties.put("username",getUsername());      
		inheritedProperties.put("version",getVersion());
		
		for (AdonisModel aModel : getModels().getModel()){
			//pass global information to models
			aModel.getInheritedProperties().putAll(inheritedProperties);
			
			
			Logger.i("write Model "+aModel.getName());
			
			json = new JSONObject();
			aModel.writeJSON(json);
			jsonDiagrams.add(json);
		}
		
		return jsonDiagrams;
	}
	
	/**
	 * overriden to restore a valid xml file 
	 */
	@Override
	public void readJSON(JSONObject json){
		//it should be only one, but it may extended
		AdonisModel diagram = new AdonisModel();
		if (getModels() == null){
			setModels(new AdonisModels());
		}
		getModels().getModel().add(diagram);
		diagram.readJSON(json);
		Map<String,String> inheritedProperties = null;
		for (AdonisModel model : getModels().getModel()){
			inheritedProperties = model.getInheritedProperties(); 
			if (inheritedProperties.containsKey("adoversion")){
				setAdoversion(inheritedProperties.get("adoversion"));
			}
			if (inheritedProperties.containsKey("database")){
				setDatabase(inheritedProperties.get("database"));
			}
			if (inheritedProperties.containsKey("username")){
				setUsername(inheritedProperties.get("username"));
			}
			if (inheritedProperties.containsKey("version")){
				setVersion(inheritedProperties.get("version"));
			}
		}
		if (getAdoversion() == null){
			Logger.d("adoversion empty - set default");
			setAdoversion("Version 3.9");
		}
		if (getDatabase() == null){
			Logger.d("database empty - set default");
			setDatabase("unknown");
		}
		if (getUsername() == null){
			Logger.d("username empty - set default");
			setUsername("Admin");
		}
		if (getVersion() == null){
			Logger.d("version empty - set default");
			setVersion("3.1");
		}
		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("dd.MM.yyyy");
		setDate(formatter.format(new Date()));
		formatter.applyPattern("hh:mm");
		setTime(formatter.format(new Date()));
		
	}
}