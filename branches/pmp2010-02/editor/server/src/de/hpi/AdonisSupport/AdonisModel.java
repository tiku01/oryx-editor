package de.hpi.AdonisSupport;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT MODEL (MODELATTRIBUTES, INSTANCE*, CONNECTOR*)>
//<!ATTLIST MODEL
//  id        ID    #IMPLIED
//  name      CDATA #REQUIRED
//  version   CDATA #REQUIRED
//  modeltype CDATA #REQUIRED
//  libtype   CDATA #REQUIRED
//  applib    CDATA #REQUIRED
//>

@RootElement("MODEL")
public class AdonisModel extends AdonisStencil{
	private static final long serialVersionUID = 4319867261926641608L;
	
	
	@Attribute("name")
	protected String name;
	@Attribute("version")
	protected String version;
	@Attribute("modeltype")
	protected String modeltype;
	@Attribute("libtype")
	protected String libtype = "bp";
	@Attribute("applib")
	protected String applib = "ADONIS:CE BPMS BP Library";
	@Element(name="MODELATTRIBUTES")
	protected AdonisModelAttributes modelAttributes;
	@Element(name="INSTANCE")
	protected ArrayList<AdonisInstance> instance;
	@Element(name="CONNECTOR")
	protected ArrayList<AdonisConnector> connector;
	
	protected int indexCounter = 0;
	
	public void setName(String value){
		name = value;
	}
	public String getName(){
		return name;
	}
	
	public void setVersion(String value){
		version = value;
	}
	public String getVersion(){
		return version;
	}
	
	public void setModeltype(String value){
		modeltype = value;
	}
	public String getModeltype(){
		return modeltype;
	}
	
	public void setLibtype(String value){
		libtype = value;
	}
	public String getLibtype(){
		return libtype;
	}
	
	public void setApplib(String value){
		applib = value;
	}
	public String getApplib(){
		return applib;
	}
	
	public AdonisModelAttributes getModelAttributes(){
		if (modelAttributes == null){
			modelAttributes = new AdonisModelAttributes();
		} 
		return modelAttributes;
	}
	public void setModelAttributes(AdonisModelAttributes list){	
		modelAttributes = list;
	}
		
	public ArrayList<AdonisInstance> getInstance(){	
		if (instance == null){
			instance = new ArrayList<AdonisInstance>();
		} 
		return instance;
	}
	public void setInstance(ArrayList<AdonisInstance> list){
		instance = list;
	}
	
	public ArrayList<AdonisConnector> getConnector(){
		if (connector == null){
			connector = new ArrayList<AdonisConnector>();
		} 
		return connector;
	}
	
	public void setConnector(ArrayList<AdonisConnector> list){
		connector = list;
	}
	
	@Override
	protected AdonisModel getModel() {
		return this;
	}
	@Override
	protected void setModel(AdonisModel aModel) {
		//nothing to do - no nested models possible
	}

	private Map<String,AdonisStencil> modelChildren = null;
	
	@Override
	public Map<String, AdonisStencil> getModelChildren() {
		if (modelChildren == null){
			modelChildren = new HashMap<String,AdonisStencil>();
		}
		return modelChildren;
	}





	protected Map<String,String> inheritedProperties;
	
	private Double[] oryxGlobalBounds = null;
	private Double[] adonisGlobalBounds = null;
	
	public AdonisModel(){
		instance = new ArrayList<AdonisInstance>();
		connector = new ArrayList<AdonisConnector>();
		modelAttributes = new AdonisModelAttributes();
	}
	
	public int getNextStencilIndex(){
		return indexCounter++;
	}
	
	public Map<String, String> getInheritedProperties(){
		if (inheritedProperties == null){
			inheritedProperties = new HashMap<String, String>(); 
		}
		return inheritedProperties;
	}
	
	public boolean isModel(){
		return true;
	}
	
	@Override
	public AdonisAttribute getAttribute(String identifier){
		for (AdonisAttribute anAttribute : getModelAttributes().getAttribute()){
			if (identifier.equals(anAttribute.getOryxName()));
				return anAttribute;
		}
		return null;
	}
	
	/**
	 * creates a map of instances and connectors for quick access in all instances
	 */
	private void distributeChildMap(){
		Log.d("distribute Childs of map");
		for (AdonisStencil stencil : getInstance()){
			stencil.setModel(this);
			
		}
		for (AdonisStencil stencil : getConnector()){
			if (!"is inside".equalsIgnoreCase(stencil.getStencilClass())){
				stencil.setModel(this);			
			} else {
				Log.d("is inside ignored");
			}
		}
		for (AdonisStencil stencil : getModelChildren().values()){
			org.junit.Assert.assertFalse(stencil.getName().contains("inside"));
		}
	}
	
	/**
	 * resolves the "is inside" or equivalent connectors of adonis
	 */
	private void resolveParentChildRelations(){	
		Map<AdonisStencil,Vector<AdonisStencil>> isInsideAssociations = new HashMap<AdonisStencil,Vector<AdonisStencil>>();
		for (AdonisConnector edge : getConnector()){
			if (edge.getOryxStencilClass().equals("is inside")){
				AdonisStencil child = getModelChildren().get(edge.getFrom().getInstanceName());
				AdonisStencil parent = getModelChildren().get(edge.getTo().getInstanceName());
				
				Vector<AdonisStencil> parents = isInsideAssociations.get(child);
				if (parents == null){
					parents = new Vector<AdonisStencil>();
					isInsideAssociations.put(child, parents);
				} 
				if (!parents.contains(parent)){
					parents.add(parent);
				}
				addUsed(edge);
			}
			//edge.setModel(this);
		}
		Set<AdonisStencil> removedParents = new HashSet<AdonisStencil>();
		Set<AdonisStencil> removedKeys = new HashSet<AdonisStencil>();
		
		while (isInsideAssociations.size() > 0){
			// take all child with only one parent
			for (AdonisStencil key : isInsideAssociations.keySet()){
				Vector<AdonisStencil> parents = isInsideAssociations.get(key); 
				//in case there is only one, set it as parent to the object
				if (parents.size() == 1){
					AdonisStencil onlyParent = parents.elementAt(0); 
					key.setParent(onlyParent);
					removedParents.add(onlyParent);
					removedKeys.add(key);
				} 
			}
			for (AdonisStencil removedKey : removedKeys){
				isInsideAssociations.remove(removedKey);
			}
			
			for (Vector<AdonisStencil> otherParents : isInsideAssociations.values()){
				otherParents.removeAll(removedParents);
			}
			removedKeys.clear();
			removedParents.clear();
		}
	}
	
	
	

	
	private String[] filterArea(String area){
		String filteredArea = area.replace("w:", "");
		filteredArea = filteredArea.replace("h:", "");
		filteredArea = filteredArea.replace("cm","");
		return filteredArea.split(" ");
	}
	
	
	public Double[] getAdonisGlobalBounds(){
		if (adonisGlobalBounds != null){
			return adonisGlobalBounds;
		}
		adonisGlobalBounds = new Double[]{0.0, 0.0, null, null};
		adonisGlobalBounds[2] = oryxGlobalBounds[2] / CENTIMETERTOPIXEL;
		adonisGlobalBounds[3] = oryxGlobalBounds[3] / CENTIMETERTOPIXEL;
		return adonisGlobalBounds;
	}
	
	//@Override
	//public Double[] getOryxGlobalBounds(){
	//	Inherited from AdonisStencil
	//}
	
	
	public Double[] getOryxBounds() {
		if (oryxGlobalBounds != null){
			return oryxGlobalBounds;
		}
		oryxGlobalBounds = new Double[]{0.0, 0.0, 1485.0, 1050.0};
		AdonisAttribute anAttribute = getAttribute("World area");
		if (anAttribute != null && anAttribute.getElement() != null){
			String[] area = filterArea(anAttribute.getElement());
			// try to give the diagram a nice size
			oryxGlobalBounds[2] = Double.parseDouble(area[0])*CENTIMETERTOPIXEL;
			oryxGlobalBounds[3] = Double.parseDouble(area[1])*CENTIMETERTOPIXEL;
			} 
		addUsed(anAttribute);
		return oryxGlobalBounds;
	}
	
	public String getAdonisStencilClass(String language){
		if (language == "" || language == null){
			return Configurator.getTranslationToAdonis(modeltype, getLanguage());
		} 
		return Configurator.getTranslationToAdonis(modeltype, language);
	}
	
	//*************************************************************************
	//* methods for creation of JSON representation
	//*************************************************************************
	
	@Override
	public void prepareAdonisToOryx() throws JSONException{
		// put all stencils in the diagram in map ... all stencils have access to this map
		distributeChildMap();
		resolveParentChildRelations();
	}
	
	/**
	 * write global attributes inherited from AdoXML 
	 */
	public void writeJSONinheritedProperties(JSONObject json) throws JSONException{
		json.put("inheritedProperties",getInheritedProperties());
		
		
	}
	/**
	 * write the resource id
	 */
	@Override
	public void writeJSONresourceId(JSONObject json) throws JSONException{
		json.put("resourceId","oryx-canvas123");
	}
	/**
	 * write the stencil
	 */
	@Override
	public void writeJSONstencil(JSONObject json) throws JSONException{ 
		JSONObject stencil = getJSONObject(json,"stencil");
		stencil.put("id","Diagram");
	}
	/**
	 * write the properties (including not used ones and except special attributes with a representation in oryx)
	 */
	@Override
	public void writeJSONproperties(JSONObject json) throws JSONException{
		JSONObject properties = getJSONObject(json, "properties");
		properties.put("name",getName());

	}
	/**
	 * write the used stencilsets to the file 
	 */
	public void writeJSONstencilset(JSONObject json) throws JSONException{
		JSONObject stencilset = getJSONObject(json, "stencilset");
		stencilset.put("url","/oryx//stencilsets/adonis/adonis.json");
	}
	/**
	 * write the namespace
	 */
	public void writeJSONnamespace(JSONObject json) throws JSONException{
		JSONObject stencilset = getJSONObject(json, "stencilset");
		stencilset.put("namespace","http://b3mn.org/stencilset/adonis#");
	}
	/**
	 * write down the used stencilset extensions
	 */
	public void writeJSONssextensions(JSONObject json) throws JSONException{
		getJSONArray(json, "ssextensions");
	}
	/**
	 * write all childshapes to the diagram
	 */
	@Override
	public void writeJSONchildShapes(JSONObject json) throws JSONException {
		Log.w("ChildShapes called by "+getName()+"("+getStencilClass()+")");
		JSONArray childShapes = getJSONArray(json,"childShapes");
		JSONObject shape = null;
		// writes all stencils - only connectors are missing
		for (AdonisStencil aStencil : getModelChildren().values()){
			//write only my childshapes
			if (aStencil.isInstance() && aStencil.getParent() == this){
				shape = new JSONObject();
				aStencil.write(shape);
				childShapes.put(shape);
			} else if (aStencil.isConnector()){
				if (!"Is inside".equalsIgnoreCase(aStencil.getStencilClass())) {
					Log.d("Connector "+getStencilClass());
					shape = new JSONObject();
					aStencil.write(shape);
					childShapes.put(shape);
				} else {
					Log.d("\"is inside\" detected");
				}
			} 
		}
	}
	
	/**
	 * write the bounds of the diagram
	 */
	@Override
	public void writeJSONbounds(JSONObject json) throws JSONException {
		JSONObject bounds = getJSONObject(json,"bounds");
		JSONObject temp = getJSONObject(bounds,"upperLeft");
		temp.put("x",getOryxBounds()[0]);
		temp.put("y",getOryxBounds()[1]);
		temp = getJSONObject(bounds,"lowerRight");
		temp.put("x",getOryxBounds()[2]);
		temp.put("y",getOryxBounds()[3]);
	}
	/**
	 * a diagram has no dockers - no implementation needed
	 */
	@Override
	public void writeJSONdockers(JSONObject json) throws JSONException {
		// not needed
	}
	
	/**
	 * a diagram has no outgoing edges
	 */
	@Override
	public void writeJSONoutgoing(JSONObject json) throws JSONException {
		// nothing to do
	}
	
	@Override
	public void writeJSONtarget(JSONObject json) throws JSONException{
		// not used
	}

	
	public void writeJSONunused(JSONObject json) throws JSONException{
		//JSONObject unused = getJSONObject(json, "unused");
		SerializableContainer<XMLConvertible> unused = new SerializableContainer<XMLConvertible>();
		
		try {
			for (AdonisAttribute aAttribute : getModelAttributes().getAttribute()){
				if (getUsed().indexOf(aAttribute) < 0){
					unused.getElements().add(aAttribute);
				}
			}
			for (AdonisRecord aRecord : getModelAttributes().getRecord()){
				if (getUsed().indexOf(aRecord) < 0){
					unused.getElements().add(aRecord);
				}
			}
		
			//unused.put("attributes", makeStorable(unusedAttributes));
			json.put("unused", makeStorable(unused));
		} catch (JSONException e) {
			Log.e("could not write unused elements and attributes\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	//*************************************************************************
	//* methods for reading of JSON representation
	//*************************************************************************
	
	public void completeOryxToAdonis(){
		getModelAttributes().getAttribute().add(new AdonisAttribute("World area","STRING","w:"+getAdonisGlobalBounds()[2]+"cm h:"+getAdonisGlobalBounds()[3]+"cm"));
		for (AdonisConnector aConnector : getConnector()){
			aConnector.getTo().distributeValues();
			aConnector.getFrom().distributeValues();
		}
		
		Log.e("Created "+getInstance().size()+" Instances and "+getConnector().size()+" Connectors");
		
	}
	
	public void readJSONinheritedProperties(JSONObject json) throws JSONException {
		JSONObject inheritedProperties = json.getJSONObject("inheritedProperties");
		Iterator<String> iterator = inheritedProperties.keys();
		String key = null;
		while (	iterator.hasNext()){
			key = iterator.next();
			getInheritedProperties().put(key,inheritedProperties.getString(key));
		}
		Log.d("");
	}
	
	@SuppressWarnings("unchecked")
	public void readJSONunused(JSONObject json){
		SerializableContainer<XMLConvertible> unused;
		String encodedString;
		try {
			encodedString = json.getString("unused");
			if (encodedString != null){
				unused = (SerializableContainer<XMLConvertible>) fromStorable(encodedString);
				for (XMLConvertible element : unused.getElements()){
					if (element.getClass() == AdonisAttribute.class)
						getModelAttributes().getAttribute().add((AdonisAttribute)element);
					if (element.getClass() == AdonisRecord.class){
						getModelAttributes().getRecord().add((AdonisRecord)element);
					}
				}
			}
		} catch (JSONException e){
			Log.e("could not restore unused attributes");
		}
		
	}
	
	public void readJSONbounds(JSONObject json) throws JSONException{
		JSONObject bounds = json.getJSONObject("bounds");
		JSONObject lowerRight = bounds.getJSONObject("lowerRight");
		oryxGlobalBounds = new Double[]{0.0, 0.0, lowerRight.getDouble("x"), lowerRight.getDouble("y")};
	}
	
	public void readJSONproperties(JSONObject json) throws JSONException{
		JSONObject properties = json.getJSONObject("properties"); 
		setName(properties.getString("name"));
	}
	
	public void readJSONresourceId(JSONObject json){
		super.readJSONresourceId(json);
		Random random = new Random();
		setId("mod."+random.nextInt(100000));
		setVersion("");
		//Adonis generates a own ID - so this is only to respect the format
	}
	
	public void readJSONchildShapes(JSONObject json) throws JSONException{
		Log.d("Read in ChildShapes of a model");
		//readJSONbounds(json);
		JSONArray childShapes = json.getJSONArray("childShapes");
		JSONObject stencil = null;
		String stencilName = null;
		String stencilResourceId = null;
		
		AdonisConnector aConnector = null;
		AdonisInstance anInstance = null;
		
		Map<String,JSONObject> unhandled = new HashMap<String,JSONObject>();

		
		for (int i = 0; i < childShapes.length(); i++){
			stencil = childShapes.getJSONObject(i);
			stencilName = stencil.getJSONObject("stencil").getString("id");
			stencilResourceId = stencil.getString("resourceId");
			if (AdonisInstance.handleStencil(stencilName)){	
				anInstance = (AdonisInstance)getModelChildren().get(stencilResourceId);
//				for (AdonisStencil aStencil : getModelChildren().values()){
//					Assert.notNull(aStencil.resourceId);
//					if (aStencil.isInstance() && stencilResourceId.equals(aStencil.resourceId)){
//						anInstance = (AdonisInstance)aStencil;
//					}
//				}
				if (anInstance == null){
					anInstance = new AdonisInstance();
					anInstance.setResourceId(stencilResourceId);
					anInstance.setModel(this);
					anInstance.setParent(this);
				}
				anInstance.parse(stencil);
				anInstance = null;
			} else {
				unhandled.put(stencilName, stencil);
			}
		}
		
		Log.w("Stencils: "+getInstance().size()+"  Connectors: "+getConnector().size());
		for (int i = 0; i < childShapes.length(); i++){
			stencil = childShapes.getJSONObject(i);
			stencilName = stencil.getJSONObject("stencil").getString("id");
			stencilResourceId = stencil.getString("resourceId");
			if (AdonisConnector.handleStencil(stencilName)){
				aConnector = (AdonisConnector)getModelChildren().get(stencilResourceId);
//				for (AdonisStencil aStencil : getModelChildren().values()){
//					Assert.notNull(aStencil.resourceId);
//					if (aStencil.isConnector() && stencilResourceId.equals(aStencil.resourceId)){
//						aConnector = (AdonisConnector)aStencil;
//					}
//				}
				if (aConnector == null){
					aConnector = new AdonisConnector();
					aConnector.setResourceId(stencilResourceId);
					aConnector.setModel(this);
					aConnector.setParent(this);
				}
				aConnector.parse(stencil);
				aConnector = null;
			}
			unhandled.remove(stencilName);
		}
		if (getConnector().size()+getInstance().size() != childShapes.length()){
			//Log.e("Could not convert "+stencilName+" to Connector or Instance\n"+stencil);
			for (String unhandledStencil : unhandled.keySet()){
				Log.w("Unhandled Stencil "+unhandledStencil);
			}
		}
		Log.d("Stencils: "+getInstance().size()+"  Connectors: "+getConnector().size());
	}
	
	public void readJSONssextensions(JSONObject json){
		//TODO consider extensions
	}
	
	public void readJSONstencilset(JSONObject json){
		//TODO don't know if needed to export
		modeltype = "company map";
		setModeltype(getAdonisStencilClass("en"));
	}
	
	public void readJSONstencil(JSONObject json){
		//TODO don't know if needed to export
	}
}