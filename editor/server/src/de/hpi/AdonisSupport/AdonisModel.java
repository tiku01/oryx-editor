package de.hpi.AdonisSupport;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.springframework.util.Assert;
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
	
// X M a p p r   -   m a p p  i n g   a t t r i b u t e s 
	
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
	
	protected int indexCounter = 1;
	
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
	
// h e l p e r
	
	private Map<String,AdonisStencil> modelChildren = null;
	protected Map<String,String> inheritedProperties;
	
	private Double[] oryxGlobalBounds = null;
	private Double[] adonisGlobalBounds = null;

	public AdonisModel(){
		instance = new ArrayList<AdonisInstance>();
		connector = new ArrayList<AdonisConnector>();
		modelAttributes = new AdonisModelAttributes();
	}
	
	@Override
	protected AdonisModel getModel() {
		return this;
	}
	
	@Override
	protected void setModel(AdonisModel aModel) {
		//nothing to do - no nested models possible
	}
	
	/**
	 * returns all children of the model 
	 */
	@Override
	public Map<String, AdonisStencil> getModelChildren() {
		if (modelChildren == null){
			modelChildren = new HashMap<String,AdonisStencil>();
		}
		return modelChildren;
	}
	
	/**
	 * in adonis the stencils are counted ("index:" in Position attribute)
	 * @return the next index
	 */
	public int getNextStencilIndex(){
		return indexCounter++;
	}
	
	/**
	 * @return attributes which should be distributed form global to model level
	 */
	public Map<String, String> getInheritedProperties(){
		if (inheritedProperties == null){
			inheritedProperties = new HashMap<String, String>(); 
		}
		return inheritedProperties;
	}
	
	/**
	 * indicates the type
	 */
	public boolean isModel(){
		return true;
	}
	
	@Override
	public AdonisAttribute getAttribute(String identifier, String lang){
		for (AdonisAttribute anAttribute : getModelAttributes().getAttribute()){
			if (identifier.equals(Configurator.getOryxIdentifier(anAttribute.getAdonisName(),"en")))
				return anAttribute;
		}
		return null;
	}
	
	/**
	 * add a attriute to the model attributes - determine type by using configurator
	 * @param oryxIdentifier
	 * @param language
	 * @param element
	 */
	private void addAttribute(String oryxIdentifier,String language,String element){
		getModelAttributes().getAttribute().add(
				AdonisAttribute.create(
						language,
						oryxIdentifier,
						"STRING",
						element));
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
			if (!"is inside".equals(stencil.getOryxIndentifier())){
				stencil.setModel(this);			
			} else {
				Log.d("is inside ignored");
			}
		}
		for (AdonisStencil stencil : getModelChildren().values()){
			Assert.isTrue(!stencil.getOryxIndentifier().equals("is inside"));
		}
	}
	
	
	
	/**
	 * resolves the "is inside" or equivalent connectors of adonis
	 */
	private void resolveParentChildRelations(){	
		Map<AdonisStencil,Vector<AdonisStencil>> isInsideAssociations = new HashMap<AdonisStencil,Vector<AdonisStencil>>();
		for (AdonisConnector edge : getConnector()){
			if (edge.getOryxIndentifier().equals("is inside")){
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
	
	/**
	 * removes unnecessary elements of world area string
	 */
	private String[] filterArea(String area){
		String filteredArea = area.replace("w:", "");
		filteredArea = filteredArea.replace("h:", "");
		filteredArea = filteredArea.replace("cm","");
		return filteredArea.split(" ");
	}
	
	/**
	 * translates the oryx bounds to adonis bounds
	 * @return array with [x,y,width,heigth] 
	 */
	public Double[] getAdonisGlobalBounds(){
		if (adonisGlobalBounds != null){
			return adonisGlobalBounds;
		}
		adonisGlobalBounds = new Double[]{0.0, 0.0, null, null};
		adonisGlobalBounds[2] = oryxGlobalBounds[2] / CENTIMETERTOPIXEL;
		adonisGlobalBounds[3] = oryxGlobalBounds[3] / CENTIMETERTOPIXEL;
		return adonisGlobalBounds;
	}
	
	/**
	 * translates the adonis bounds to oryx bounds
	 * @return array with [topLeft x,topLeft y,bottomRight x, bottomRight y]
	 */
	public Double[] getOryxBounds() {
		if (oryxGlobalBounds != null){
			return oryxGlobalBounds;
		}
		oryxGlobalBounds = new Double[]{0.0, 0.0, 1485.0, 1050.0};
		AdonisAttribute anAttribute = getAttribute("world area","en");
		if (anAttribute != null && anAttribute.getElement() != null){
			String[] area = filterArea(anAttribute.getElement());
			// try to give the diagram a nice size
			if (area.length >= 3 && area[2] != "" && area[3] != ""){
				oryxGlobalBounds[2] = Double.parseDouble(area[0])*CENTIMETERTOPIXEL;
				oryxGlobalBounds[3] = Double.parseDouble(area[1])*CENTIMETERTOPIXEL;
			} 
			} 
		addUsed(anAttribute);
		return oryxGlobalBounds;
	}
	
	public String getAdonisStencilClass(String language){
		if (language == "" || language == null){
			return Configurator.getAdonisIdentifier(modeltype, getLanguage());
		} 
		return Configurator.getAdonisIdentifier(modeltype, language);
	}
	
//*************************************************************************
//* JAVA -> JSON
//*************************************************************************
	
	@Override
	public void completeAdonisToOryx() throws JSONException{
		super.completeAdonisToOryx();
		
		//this attribute is added while export dynamically
		addUsed(getAttribute("number of objects and relations","en"));
		
	}
	
	@Override
	public void prepareAdonisToOryx() throws JSONException{
		super.prepareAdonisToOryx();
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
		
		AdonisAttribute attribute = null;
		
		attribute = getAttribute("author","en");
		addUsed(attribute);
		properties.put("author", attribute.getElement() == null ? "" : attribute.getElement());
		
		attribute = getAttribute("base name","en");
		if (attribute != null && attribute.getElement() != null && attribute.getElement().length() > 0){
			properties.put("base name", attribute.getElement());
			addUsed(attribute);
		}
		
		attribute = getAttribute("last user","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("last user", attribute.getElement());
			addUsed(attribute);
		}

		attribute = getAttribute("keywords","en");
		addUsed(attribute);
		properties.put("keywords", attribute.getElement() == null ? "" : attribute.getElement());
		
		attribute = getAttribute("comment","en");
		addUsed(attribute);
		properties.put("comment", attribute.getElement() == null ? "" : attribute.getElement());
		
		attribute = getAttribute("description","en");
		addUsed(attribute);
		properties.put("description", attribute.getElement() == null ? "" : attribute.getElement());
		
		attribute = getAttribute("state","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("state", Configurator.getOryxIdentifier(attribute.getElement(),"en"));
			addUsed(attribute);
		}
		
//TODO viewable area should be considered - need information when the attribute is set
//		<ATTRIBUTE name="Viewable area" type="STRING">VIEW representation:graphic
//		GRAPHIC x:-16 y:121 w:546 h:500
//		TABLE
//		</ATTRIBUTE>

		
		attribute = getAttribute("reviewed on","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("reviewed on", Helper.dateAdonisToOryx(attribute.getElement(),false));
			addUsed(attribute);
		}
		
		attribute = getAttribute("date last changed","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("date last changed", Helper.dateAdonisToOryx(attribute.getElement(),true));
			addUsed(attribute);
		}
		
		attribute = getAttribute("creation date","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("creation date", Helper.dateAdonisToOryx(attribute.getElement(),true));
			addUsed(attribute);
		}
		
		attribute = getAttribute("reviewed by","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("reviewed by", attribute.getElement() == null ? "" : attribute.getElement());
			addUsed(attribute);
		}
		
		attribute = getAttribute("version number","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("version number", attribute.getElement());
			addUsed(attribute);
		}
		
		attribute = getAttribute("context of version","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("context of version", attribute.getElement());
			addUsed(attribute);
		}
		
		attribute = getAttribute("contact person","en");
		if (attribute != null && attribute.getElement() != null){
			properties.put("contact person", attribute.getElement());
			addUsed(attribute);
		}
		
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
		AdonisAttribute type = getAttribute("type","en");
		if (type != null && type.getElement() != null){
			addUsed(type);
			//TODO hier kommt das passende Stencilset rein
		}
		getJSONArray(json, "ssextensions");
	}
	/**
	 * write all childshapes to the diagram
	 */
	@Override
	public void writeJSONchildShapes(JSONObject json) throws JSONException {
		Log.w("ChildShapes called by "+getName()+"("+getAdonisIndentifier()+")");
		JSONArray childShapes = getJSONArray(json,"childShapes");
		JSONObject shape = null;
		// writes all stencils - only connectors are missing
		for (AdonisStencil aStencil : getModelChildren().values()){
			//write only my childshapes
			if (aStencil.isInstance() && aStencil.getParent() == this){
				shape = new JSONObject();
				aStencil.writeJSON(shape);
				childShapes.put(shape);
			} else if (aStencil.isConnector()){
				if (!"Is inside".equalsIgnoreCase(aStencil.getAdonisIndentifier())) {
					Log.d("Connector "+getAdonisIndentifier());
					shape = new JSONObject();
					aStencil.writeJSON(shape);
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

	/**
	 * store unused
	 */
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
//* JSON -> Java
//*************************************************************************
	/**
	 * adonis need uses indices to place the swimlanes (instead of using the coordinates)</br>
	 * this method ensures the right order and sets unique indices for all shapes 
	 */
	private void addIndicesToShapes(){
		ArrayList<AdonisInstance> swimlanes = new ArrayList<AdonisInstance>();
		int index = 1;
		for (AdonisInstance aStencil : getInstance()){
			if (aStencil.getOryxIndentifier().contains("swimlane")){
				swimlanes.add(aStencil);
			}
		}
		AdonisInstance[] sortedSwimlanes = swimlanes.toArray(new AdonisInstance[0]);
		Arrays.sort(sortedSwimlanes,new Comparator<AdonisInstance>(){

			@Override
			public int compare(AdonisInstance lhs, AdonisInstance rhs) {
				double lx = lhs.getAdonisGlobalBounds()[0];
				double ly = lhs.getAdonisGlobalBounds()[1];
				double rx = rhs.getAdonisGlobalBounds()[0];
				double ry = rhs.getAdonisGlobalBounds()[1];
				if (lx < rx || ly < ry) return -1;
				if (lx == rx && ly == ry) return 0;
				return 1;
			}
			
		});
		for (AdonisInstance aSwimlane : sortedSwimlanes){
			AdonisAttribute position = aSwimlane.getAttribute("position","en");
			position.setElement(position.getElement()+"index:"+(++index));
		}
		for (AdonisInstance anIntance : getInstance()){
			AdonisAttribute position = anIntance.getAttribute("position","en"); 
			if (!position.getElement().contains("index")){
				position.setElement(position.getElement()+"index:"+(++index));
			}
		}
		for (AdonisConnector aConnector : getConnector()){
			AdonisAttribute position = aConnector.getAttribute("positions","en"); 
			if (position != null && !position.getElement().contains("index")){
				position.setElement(position.getElement()+"index:"+(++index));
			}
		}
	}
	
	/**
	 * triggers the processing of the position of all shapes</br>
	 * is separated to guarantee that all necessary positions are set in the parent shapes
	 */
	public void calculateAdonisPosition(){
		Set<AdonisStencil> ready = new HashSet<AdonisStencil>();
		ready.add(this);
		while (ready.size() < getInstance().size()){
			for (AdonisInstance anInstance : getInstance()){
				if (ready.contains(anInstance.getParent())
					&& !ready.contains(anInstance)){
					anInstance.calculateAdonisPosition();
					ready.add(anInstance);
				}
			}
		}		
	}
	
	public void generateObjectCounterAttribute(){
		getModelAttributes().getAttribute().add(
				AdonisAttribute.create(
						"en",
						"number of objects and relations",
						"INTEGER",
						""+(getInstance().size()+getConnector().size())));
	}
	
	public void completeOryxToAdonis(){
		getModelAttributes().getAttribute().add(
				AdonisAttribute.create(
						"en",
						"world area",
						"STRING",
						"w:"+getAdonisGlobalBounds()[2]+"cm h:"+getAdonisGlobalBounds()[3]+"cm"));
		int connectorNumber = 0;
		for (AdonisConnector aConnector : getConnector()){
			aConnector.getTo().distributeValues();
			aConnector.getFrom().distributeValues();
			if (!aConnector.getOryxIndentifier().equals("is inside")){
				aConnector.getAttribute().add(
						AdonisAttribute.create(
								"en",
								"connector number",
								"INTEGER",
								""+(++connectorNumber)));
			}
		}
		
		Log.e("Created "+getInstance().size()+" Instances and "+getConnector().size()+" Connectors");
		
		calculateAdonisPosition();
		addIndicesToShapes();	
		
		super.completeOryxToAdonis();
	}
	
	@SuppressWarnings("unchecked")
	public void readJSONinheritedProperties(JSONObject json) throws JSONException {
		JSONObject inheritedProperties = json.getJSONObject("inheritedProperties");
		if (inheritedProperties != null){
			Iterator<String> iterator = inheritedProperties.keys();
			String key = null;
			while (	iterator.hasNext()){
				key = iterator.next();
				getInheritedProperties().put(key,inheritedProperties.getString(key));
			}
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
	
	@SuppressWarnings("unchecked")
	public void readJSONproperties(JSONObject json) throws JSONException{
		JSONObject propertyObject = json.getJSONObject("properties");
		if (propertyObject == null){
			return;
		}
				
		HashMap<String,String> properties = new HashMap<String,String>();
		Iterator<String> keyIterator = propertyObject.keys();
		String key = null;
		while(keyIterator.hasNext()){
			key = keyIterator.next();
			properties.put(key,propertyObject.getString(key));
		}

		
	
		String element;

		element = properties.get("name");
		setName(properties.get("name"));
		
		element = properties.get("author");
		if (element != null) addAttribute("author","en",element.length()== 0 ? "Admin" : element);
		
		element = properties.get("last user");
		if (element != null && element.length() > 0) addAttribute("last user","en",element);
		
		element = properties.get("base name");
		if (element != null && element.length() > 0) addAttribute("base name","en",element);
		
		element = properties.get("keywords");
		if (element != null) addAttribute("keywords","en",element);
		
		element = properties.get("comment");
		if (element != null) addAttribute("comment","en",element);
		
		element = properties.get("description");
		if (element != null) addAttribute("description","en",element);
		
		element = properties.get("state");
		if (element != null) addAttribute("state","en",Configurator.getAdonisIdentifier(element,"en"));
		
		element = properties.get("reviewed on");
		if (element != null) addAttribute("reviewed on","en",Helper.dateOryxToAdonis(element,false));
		
		element = properties.get("date last changed");
		if (element != null && element.length() > 0) addAttribute("date last changed","en",Helper.dateOryxToAdonis(element,true));
		
		element = properties.get("creation date");
		if (element != null && element.length() > 0) addAttribute("creation date","en",Helper.dateOryxToAdonis(element,true));
		
		element = properties.get("reviewed by");
		if (element != null) addAttribute("reviewed by","en",element);		
		
		element = properties.get("version number");
		if (element != null) addAttribute("version number","en",element);
		
		element = properties.get("context of version");
		if (element != null) addAttribute("context of version","en",element);
		
		element = properties.get("contact person");
		if (element != null) addAttribute("contact person","en",element);
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
				if (anInstance == null){
					anInstance = new AdonisInstance();
					anInstance.setResourceId(stencilResourceId);
					anInstance.setModel(this);
					anInstance.setParent(this);
				}
				anInstance.readJSON(stencil);
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
				if (aConnector == null){
					aConnector = new AdonisConnector();
					aConnector.setResourceId(stencilResourceId);
					aConnector.setModel(this);
					aConnector.setParent(this);
				}
				aConnector.readJSON(stencil);
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
	//TODO needs dynamically load/unload
	public void readJSONssextensions(JSONObject json) throws JSONException{
		json.getJSONObject("ssextensions");
		getModelAttributes().getAttribute().add(
				AdonisAttribute.create(
						"en",
						"type",
						"ENUMERATION",
						/*TODO remove hardcoding*/ 
						Configurator.getAdonisIdentifier("company map", "en")));
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