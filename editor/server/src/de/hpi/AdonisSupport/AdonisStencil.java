package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;

import de.hpi.diagram.OryxUUID;

public abstract class AdonisStencil extends XMLConvertible {

	public final static double CENTIMETERTOPIXEL = 50;
	
	@Attribute(name="id")
	protected String id;
	
	@Attribute(name="class")
	protected String stencilClass;
	
	private String resourceId;
	
	public String getId(){return id;}
	
	public void setId(String newId){id = newId;}
	
	private String language;
	
	private String oryxStencilClass;
	
	public String getLanguage(){
		if (language == null || language == ""){
			return "en";
		}
		else return language;
	}
	
	private void setLanguage(String lang){
		language = lang;
	}
	
	/**
	 * returns the name of the stencil (or the id if there is none)
	 */
	public String getName(){
		return getId();
	}
	
	public String getStencilClass(){
		return stencilClass;
	}
	
	public void setStencilClass(String newName){
		stencilClass = newName;
	}
	
	
	
	public String getResourceId(){
		if (resourceId == null){
			resourceId = OryxUUID.generate();
		}
		return resourceId;
	}
	
	public abstract ArrayList<AdonisAttribute> getAttribute(); 
	
	private Map<String,AdonisInstance> modelInstances = null;
	
	public void setModelInstances(Map<String,AdonisInstance> children){
		modelInstances = children;
	}
	
	public Map<String,AdonisInstance> getModelInstance(){
		if (modelInstances == null){
			setModelInstances(new HashMap<String,AdonisInstance>());
		}
		return modelInstances;
	}
	
	private ArrayList<AdonisStencil> outgoingStencil = null;
	
	public void addOutgoing(AdonisStencil outgoing){
		if (outgoingStencil == null) outgoingStencil = new ArrayList<AdonisStencil>();
		outgoingStencil.add(outgoing);
	}
	
	public ArrayList<AdonisStencil> getOutgoing(){
		return outgoingStencil;
	}
	
	/**
	 * all attributes of the list which evaluated in a special way
	 * @return a map of name and type
	 */
	public abstract Map<String,String> getEvaluatedAttributes();


	
	
	
	private AdonisModel model;
	
	protected void setModel(AdonisModel aModel){
		model = aModel;
	}
	
	protected AdonisModel getModel(){
		return model;
	}
	
	private AdonisStencil parent;
	
	protected void setParent(AdonisStencil aStencil){
		parent = aStencil;
	}
		
	protected AdonisStencil getParent(){
		if (parent == null){
			return getModel();
		}
		return parent;
	}
	
	protected abstract Double[] getBounds();
	
	protected Double[] getGlobalBounds(){
		return getBounds();
	}
	
	//*************************************************************************
	//* JSON methods
	//*************************************************************************
	
	/**
	 * write the resource id
	 */
	public void writeJSONresourceId(JSONObject json) throws JSONException{
		json.put("resourceId",getResourceId());
	}
	
	/**
	 * write the stencil
	 */
	public void writeJSONstencil(JSONObject json) throws JSONException {
		JSONObject stencil = getJSONObject(json,"stencil");
		stencil.put("id", getOryxStencilClass());
		
	}
	/**
	 * write the outgoing edges
	 */
	public void writeJSONoutgoing(JSONObject json) throws JSONException{
		JSONArray outgoing = getJSONArray(json,"outgoing");
		JSONObject temp = null;
		if (getOutgoing() != null){
			for (AdonisStencil outgoingStencil : getOutgoing()){
				temp = new JSONObject();
				outgoingStencil.writeJSONresourceId(temp);
				outgoing.put(temp);			}
		}
	}
	/**
	 * write the properties (including not used ones and except special attributes with a representation in oryx)
	 */
	public abstract void writeJSONproperties(JSONObject json) throws JSONException;
	/**
	 * write all childshapes to the diagram
	 */
	public void writeJSONchildShapes(JSONObject json) throws JSONException {
		JSONArray childShapes = getJSONArray(json,"childShapes");
		JSONObject shape = null;
		
		for (AdonisInstance aInstance : getModelInstance().values()){
			//write only my childshapes
			if (aInstance.getParent() == this){
				shape = new JSONObject();
				aInstance.write(shape);
				childShapes.put(shape);
			}
		}
	}
	/**
	 * write the bounds of the object
	 */
	public abstract void writeJSONbounds(JSONObject json) throws JSONException;
	/**
	 * write dockers of the object
	 */
	public abstract void writeJSONdockers(JSONObject json) throws JSONException;

	/**
	 * write the target node
	 */
	public void writeJSONtarget(JSONObject json) throws JSONException {
		getJSONObject(json,"target");
	}
	
	//*************************************************************************
	//* Configurator access
	//*************************************************************************
	public String getOryxStencilClass(){
		if (oryxStencilClass == null){
			for (AdonisAttribute attribute : getAttribute()){
				if ("Name (english)".equals(attribute.getName()) && attribute.getElement() != null){
					oryxStencilClass = attribute.getElement().toLowerCase();
				}
			}
		}
		if (oryxStencilClass == null){
			setLanguage(Configurator.getLanguage(getStencilClass()));
			oryxStencilClass = Configurator.getTranslationToOryx(getStencilClass());
		}
		return oryxStencilClass;
	}

	public JSONObject getStandardConfiguration(){
		
		try {
			JSONObject config = Configurator.getStencilConfiguration(getOryxStencilClass()); 
			if (config == null) {
				Log.d("No config available for "+getOryxStencilClass());
			}
			return config; 
		} catch (JSONException e){
			Log.d("Error during getting config of stencil class - no config available?\n"+e.getMessage());
			return null;
		}
	}
	
}
