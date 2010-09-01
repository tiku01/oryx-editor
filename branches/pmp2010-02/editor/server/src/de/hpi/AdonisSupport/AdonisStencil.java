package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;

import de.hpi.diagram.OryxUUID;

public abstract class AdonisStencil extends XMLConvertible {
	private static final long serialVersionUID = 2925851400607788303L;
	
	public static boolean handleStencil(String oryxName){
		Log.e("handleStencil must be implemented - not done yet");
		return false;
	}
	
// C O N S T A N T S
	 
	public final static double CENTIMETERTOPIXEL = 50;
	
// C o m m o n   a t t r i b u t e s   f r o m   a d o n i s


	@Attribute(name="id")
	protected String id;
	@Attribute(name="class")
	protected String adonisIndentifier;
	
	public String getId(){
		return id;
	}
	
	public void setId(String newId){
		id = newId;
	}
	
// H e l p e r s
	
	
	protected String resourceId;
	private String oryxIndentifier;
	private String language;
	private ArrayList<AdonisStencil> outgoingStencil = null;
	
	private AdonisStencil parent;
	private AdonisModel model;
	private ArrayList<XMLConvertible> used;
	
	/**
	 * tries to get the language in which the original stencil was created
	 * @return the original or "en" if there was none found
	 */
	public String getLanguage(){
		if (language == null || language == ""){
			return "en";
		}
		else return language;
	}
	
	/**
	 * sets the language
	 * @param lang
	 */
	private void setLanguage(String lang){
		language = lang;
	}
	
	/**
	 * returns the name of the stencil (or the id if there is none)
	 */
	public String getName(){
		return getId();
	}
	
	/**
	 * @return the adonis stencil class
	 */
	public String getAdonisIndentifier(){
		return adonisIndentifier;
	}
	
	/**
	 * sets the adonis stencil class
	 * @param newName
	 */
	public void setAdonisIndentifier(String newName){
		adonisIndentifier = newName;
	}
	
	/**
	 * generates a resource Id for Oryx if necessary
	 * @return a id
	 */
	public String getResourceId(){
		if (resourceId == null){
			//Assert.assertFalse(AdonisConverter.export && (this.getClass() == AdonisConnector.class));
			setResourceId(OryxUUID.generate());
			
			Log.w("Set resourceId to generated value in "+getClass()+ " "+ getName());
		}
		return resourceId;
	}
	
	/**
	 * sets a resource Id from an existing stencil
	 * @param id
	 */
	public void setResourceId(String id){
		if (resourceId == null){
			resourceId = id;
		} else {
			Log.e("Tried to set resourceId from "+resourceId+" to "+id);
		}
	}
	
	/**
	 * sets the outgoing stencils
	 * @param outgoing
	 */
	public void addOutgoing(AdonisStencil outgoing){
		if (outgoingStencil == null) outgoingStencil = new ArrayList<AdonisStencil>();
		outgoingStencil.add(outgoing);
	}
	
	/**
	 * get all outgoing stencils or null if there is none
	 * @return
	 */
	public ArrayList<AdonisStencil> getOutgoing(){
		return outgoingStencil;
	}
	
	
	
	public boolean isModel(){
		return false;
	}
	
	public boolean isInstance(){
		return false;
	}
	
	public boolean isConnector(){
		return false;
	}
	
	/**
	 * @return a collection of already processed attributes
	 */
	public ArrayList<XMLConvertible> getUsed(){
		if (used == null){
			used = new ArrayList<XMLConvertible>();
		}
		return used;
	}
	
	/**
	 * add an attribute which is processed
	 * @param element
	 */
	public void addUsed(XMLConvertible element){
		getUsed().add(element);
	}
		
	/**
	 * generates a unique idenifier for each stencil
	 * @return
	 */
	public int getIndex(){
		return getModel().getNextStencilIndex();
	}
	
	/**
	 * get all instances in the model
	 * @return
	 */
	public Map<String,AdonisStencil> getModelChildren(){
		return getModel().getModelChildren();
	}
	
	public void addModelChildren(String key, AdonisStencil stencil){
		getModelChildren().put(key,stencil);
	}
	
	/**
	 * sets the parent model
	 * @param aModel
	 */
	protected void setModel(AdonisModel aModel){
		if (model == null){
			model = aModel;
			if (resourceId == null && getName() != null){
				model.addModelChildren(getName(),this);
			} else if (resourceId != null && getName() == null){
				model.addModelChildren(resourceId, this);
			}
			Log.d("set Model for "+getClass()+" - "+resourceId+" "+getName());
		} else {
			Log.d("tried to set Model for "+getClass()+" - "+resourceId+" "+getName());
		}
	}
	
	/**
	 * @return the parent model
	 */
	protected AdonisModel getModel(){
		return model;
	}	
	
	/**
	 * sets a parent stencil if available
	 * @param aStencil
	 */
	protected void setParent(AdonisStencil aStencil){
		parent = aStencil;
	}
	
	/**
	 * get the parent stencil (or the model if there is none)
	 * @return
	 */
	protected AdonisStencil getParent(){
		if (parent == null){
			return getModel();
		}
		return parent;
	}
	
	/**
	 * get the bounds relative to the parent
	 * @return upperLeft x,y lowerRight x,y
	 */
	protected abstract Double[] getOryxBounds();
	
	/**
	 * get the oryx bounds in context to the whole model
	 * @return upperLeft x,y lowerRight x,y
	 */
	protected Double[] getOryxGlobalBounds(){
		return getOryxBounds();
	}
	
	/**
	 * get the adonis bounds - in general only the whole model context 
	 * @return x,y (top left) and width height
	 */
	protected abstract Double[] getAdonisGlobalBounds();
	
	/**
	 * returns a special attribute of a stencil
	 * @return the requested attribute or null if there non matched
	 */
	public abstract AdonisAttribute getAttribute(String identifier, String language); 
	
	
	
	
//*************************************************************************
//* Java -> JSON
//*************************************************************************
	
	/**
	 * prepare transformation adonis to oryx - stub to be overwritten if needed
	 * @throws JSONException 
	 */
	public void prepareAdonisToOryx() throws JSONException{
		
	}
	
	/**
	 * operations to be done after reading in json to complete transformation
	 * @throws JSONException 
	 */
	public void completeAdonisToOryx() throws JSONException{
		
	}
	
	public void writeJSON(JSONObject json) throws JSONException{
		prepareAdonisToOryx();
		super.writeJSON(json);
		completeAdonisToOryx();
	}

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
		stencil.put("id", getOryxIndentifier());
		
	}
	/**
	 * write the outgoing edges
	 */
	public abstract void writeJSONoutgoing(JSONObject json) throws JSONException;

	/**
	 * write the properties (including not used ones and except special attributes with a representation in oryx)
	 */
	public abstract void writeJSONproperties(JSONObject json) throws JSONException;
	
	/**
	 * write all childshapes to the diagram
	 * @throws JSONException 
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
	
//*************************************************************************
//* JSON -> Java
//*************************************************************************
	
	/**
	 * complete transformation oryx to adonis - stub to be overwritten if needed
	 */
	public void completeOryxToAdonis(){
	}
	
	public void readJSON(JSONObject json){
		super.readJSON(json);
		completeOryxToAdonis();
	}
	
	public void readJSONresourceId(JSONObject json){
		Random random = new Random();
		setId("obj."+random.nextInt(100000));
	}
	
//*************************************************************************
//* Configurator access TODO needs overhauling
//*************************************************************************
	
	public void setOryxIndentifier(String oryxName){
		oryxIndentifier = oryxName;
	}
	
	/**
	 * translates from a adonis stencil name to a oryx stencil name
	 */
	public String getOryxIndentifier(){
		if (oryxIndentifier == null){
			AdonisAttribute anAttribute = getAttribute("Name (english)","en");
			if (anAttribute != null) {
				oryxIndentifier = anAttribute.getElement().toLowerCase();
				setLanguage("en");
				addUsed(anAttribute);
			} else {
				setLanguage(Configurator.getLanguage(getAdonisIndentifier()));
				oryxIndentifier = Configurator.getOryxIdentifier(getAdonisIndentifier(),"en");
			}
		}
		return oryxIndentifier;
	}
	
	/**
	 * translates a oryx stencil name back to a adonis stencil name in the given language
	 * @param language
	 * @return
	 */
	public String getAdonisStencilClass(String language){
		if (language == "" || language == null){
			return Configurator.getAdonisIdentifier(oryxIndentifier, getLanguage());
		} 
		// the language is known
		return Configurator.getAdonisIdentifier(oryxIndentifier, language);
	}
	
}