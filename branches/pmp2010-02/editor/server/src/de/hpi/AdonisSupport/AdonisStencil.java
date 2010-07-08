package de.hpi.AdonisSupport;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;

import de.hpi.diagram.OryxUUID;

public abstract class AdonisStencil extends AdonisBaseObject {

	public final double CENTIMETERTOPIXEL = 50;
	
	@Attribute(name="id")
	protected String id;
	
	@Attribute(name="class")
	protected String stencilClass;
	
	private String resourceId;
	
	public String getId(){return id;}
	
	public void setId(String newId){id = newId;}
	
	public String getStencilClass(){return stencilClass;}
	
	public void setStencilClass(String newName){stencilClass = newName;}
	
	
	
//	public boolean isModel(){
//		return false;
//	}
//	public boolean isConnector(){
//		return false;
//	}
//	public boolean isInstance(){
//		return false;
//	}
	
	
	
//	protected Map<String,AdonisStencil> idObjectMap;
//	protected Map<String,String> idNameMap;
	
	/**
	 * all attributes of the list which evaluated in a special way
	 * @return a map of name and type
	 */
	public abstract Map<String,String> getEvaluatedAttributes();
	/**
	 * returns the name of the stencil (or the id if there is none)
	 */
	public abstract String getName();
	
//	public void prepareComputation(Map<String,String> idMap,Map<String,AdonisStencil> objectMap){
//		if (!idMap.containsKey(getId())){
//			idMap.put(getId(), getName());
//			objectMap.put(getId(), this);
//		}
//		idNameMap = idMap;
//		idObjectMap = objectMap;
//	}
	
	private AdonisModel model;
	
	protected void setModel(AdonisModel aModel){
		model = aModel;
	}
	
	protected AdonisModel getModel(){
		return model;
	}
	
	public String getResourceId(){
		if (resourceId == null){
			resourceId = OryxUUID.generate();
		}
		return resourceId;
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
		json.put("stencil", getStencilClass());
		
	}
	/**
	 * write the properties (including not used ones and except special attributes with a representation in oryx)
	 */
	public abstract void writeJSONproperties(JSONObject json) throws JSONException;
	/**
	 * write all childshapes to the diagram
	 */
	public abstract void writeJSONchildShapes(JSONObject json) throws JSONException;
	/**
	 * write the bounds of the object
	 */
	public abstract void writeJSONbounds(JSONObject json) throws JSONException;
	/**
	 * write dockers of the object
	 */
	public abstract void writeJSONdockers(JSONObject json) throws JSONException;
	/**
	 * write the outgoing edges
	 */
	public abstract void writeJSONoutgoing(JSONObject json) throws JSONException;

	/**
	 * write the target node - only used in edges
	 */
	public abstract void writeJSONtarget(JSONObject json) throws JSONException;
}
