package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;

import de.hpi.diagram.OryxUUID;

public abstract class AdonisStencil extends AdonisBaseObject {

	public final double CENTIMETERTOPIXEL = 25;
	
	@Attribute(name="id")
	protected String id;
	
	@Attribute(name="class")
	protected String stencilClass;
	
	private String resourceId;
	
	public String getId(){return id;}
	
	public void setId(String newId){id = newId;}
	
	public String getStencilClass(){return stencilClass;}
	
	public void setStencilClass(String newName){stencilClass = newName.toLowerCase();}
	
	
	
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
	
	private  ArrayList<AdonisStencil> outgoingStencil = null;
	
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
		JSONObject stencil = getJSONObject(json,"stencil");
		stencil.put("id", getStencilClass());
		
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
	 * write the target node
	 */
	public void writeJSONtarget(JSONObject json) throws JSONException {
		getJSONObject(json,"target");
	}
}
