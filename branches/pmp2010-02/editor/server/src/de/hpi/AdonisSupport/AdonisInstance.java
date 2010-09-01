package de.hpi.AdonisSupport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT INSTANCE ((ATTRIBUTE | RECORD | INTERREF)*)>
//<!ATTLIST INSTANCE
//  id    ID    #IMPLIED
//  class CDATA #REQUIRED
//  name  CDATA #REQUIRED
//>
/**
 * represents an instance of a stencil in Adonis
 */
@RootElement("INSTANCE")
public class AdonisInstance extends AdonisStencil { 	
	private static final long serialVersionUID = -9220820981615621467L;
		
	/**
	 * list of stencils which are represented by an (adonis) instance
	 * @param oryxName - the name of the stencil in oryx
	 * @return true if the stencil is represented as instance
	 */
	public static boolean handleStencil(String oryxName){
		Set<String> instances = new HashSet<String>();
		instances.add("process");
		instances.add("performance");
		instances.add("aggregation");
		instances.add("actor");
		instances.add("external partner");
		instances.add("performance indicator");
		instances.add("performance indicator overview");
		instances.add("cross reference");
		instances.add("note");
		instances.add("swimlane (horizontal)");
		instances.add("swimlane (vertical)");
		return instances.contains(oryxName);
		
	}
	
// X M a p p r   -   M a p p i n g   a t t r i b u t e s
	
	@Attribute(name="name")
	protected String name;
	
	@Element(name="ATTRIBUTE")
	protected ArrayList<AdonisAttribute> attribute;
	
	@Element(name="INTERREF")
	protected ArrayList<AdonisInterref> interref;
	
	@Element(name="RECORD")
	protected ArrayList<AdonisRecord> record;

	public String getName(){
		return name;
	}
	
	public void setName(String newName){
		name = newName;
	}
		
	public ArrayList<AdonisAttribute> getAttribute(){
		if (attribute == null){
			attribute = new ArrayList<AdonisAttribute>();
		}
		return attribute;
	}
	
	
	public void setAttribute(ArrayList<AdonisAttribute> list){
		attribute = list;
	}
	
	public ArrayList<AdonisInterref> getInterref(){
		if (interref == null){
			interref = new ArrayList<AdonisInterref>();
		}
		return interref;
	}
	
	public void setInterref(ArrayList<AdonisInterref> list){
		interref = list;
	}
	
	public ArrayList<AdonisRecord> getRecord(){
		if (record == null)
			record = new ArrayList<AdonisRecord>();
		return record;
	}
	
	public void setRecord(ArrayList<AdonisRecord> list){
		record = list;
	}
	
// h e l p e r
	
	public AdonisAttribute getAttribute(String identifier, String lang){
		for (AdonisAttribute anAttribute : getAttribute()){
			if (identifier.equals(Unifier.getOryxIdentifier(anAttribute.getAdonisName(),lang)))
				return anAttribute;
		}
		return null;
	}
	
	public AdonisInterref getInterref(String identifier, String lang){
		for (AdonisInterref anInterref : getInterref()){
			if (identifier.equals(Unifier.getOryxIdentifier(anInterref.getName(), lang))){
				return anInterref;
			}
		}
		return null;
	}
	
	public AdonisRecord getRecord(String identifier, String lang){
		for (AdonisRecord aRecord : getRecord()){
			if (identifier.equals(Unifier.getOryxIdentifier(aRecord.getName(), lang))){
				return aRecord;
			}
		}
		return null;
	}
	
	private void addAttribute(String oryxIdentifier,String language, String element){
		getAttribute().add(AdonisAttribute.create(
				language,
				oryxIdentifier,
				"STRING",
				element));
	}
	
	/**
	 * overriden for readability of objects
	 */
	public String toString(){
		if (getName() == null || getId() == null){
			return super.toString();
		}
		return "AdonisInstance "+getName()+" "+getId()+" >>> "+super.toString();
	}
	
// f o r   c o m p u t a t i o n   p u r p o s e s
	
	private Double[] oryxGlobalBounds = null;
	private Double[] adonisGlobalBounds = null;
	
	/**
	 * remove unnecessary information of position string
	 * (NODE, SWIMLANE, index information, measuring units 
	 * @param position
	 * @return array containing [x,y,width,height]
	 */
	private String[] filterPositionString(String position){
		String filteresPosition = position.replace("NODE ","");
		filteresPosition = filteresPosition.replace("SWIMLANE ","");
		filteresPosition = filteresPosition.replace("index:", "");
		filteresPosition = filteresPosition.replace("x:", "");
		filteresPosition = filteresPosition.replace("y:", "");
		filteresPosition = filteresPosition.replace("w:", "");
		filteresPosition = filteresPosition.replace("h:", "");
		filteresPosition = filteresPosition.replace("cm","");
		return filteresPosition.split(" ");
	}

	
	
	/**
	 * the bounds formated for Adonis
	 * @param bounds
	 * @return fix point x,y, width and height
	 * @throws JSONException
	 */
	protected Double[] getAdonisGlobalBounds(){
		if (adonisGlobalBounds != null){
			return adonisGlobalBounds;
		}
		adonisGlobalBounds = new Double[4];
		Double width = oryxGlobalBounds[2] - oryxGlobalBounds[0];
		Double height = oryxGlobalBounds[3] - oryxGlobalBounds[1];
		Double leftOffset = Double.parseDouble(getStandard("offsetPercentageX", "0.0"));
		Double topOffset = Double.parseDouble(getStandard("offsetPercentageY", "0.0"));
		
		//initial the global bounds are set to the local bounds - calculate the global ones
		for (int i = 0; i < oryxGlobalBounds.length; i++){
			oryxGlobalBounds[i] += getParent().getOryxGlobalBounds()[i%2];
		}
				
		Double left = oryxGlobalBounds[0] + leftOffset / 100 * width;
		Double top = oryxGlobalBounds[1]+ topOffset / 100 * height;
		
		adonisGlobalBounds[0] = left / CENTIMETERTOPIXEL;
		adonisGlobalBounds[1] = top / CENTIMETERTOPIXEL;
		adonisGlobalBounds[2] = width / CENTIMETERTOPIXEL;
		adonisGlobalBounds[3] = height / CENTIMETERTOPIXEL;
		return adonisGlobalBounds;
	}
	/**
	 * get the bounds
	 * @return upperLeft x,y lowerRight x,y
	 */
	protected Double[] getOryxGlobalBounds(){
		if (oryxGlobalBounds != null){
			return oryxGlobalBounds;
		}
		oryxGlobalBounds = new Double[4];
		
		//get the position and size which looks like 
		//	NODE x:2.50cm y:7.00cm index:2 or
		//	NODE x:1cm y:11.5cm w:.5cm h:.6cm index:8
		AdonisAttribute adonisPosition = getAttribute("position",getLanguage()); 
		if (adonisPosition != null){
			// extract the numbers out of the string
			addUsed(adonisPosition);
			String[] position = filterPositionString(adonisPosition.getElement());
			Double left = Double.parseDouble(position[0]);
			Double top = Double.parseDouble(position[1]);
			Double width;
			Double height;
			if (position.length <= 3){
				width = Double.parseDouble(getStandard("w","3.25"));
				height = Double.parseDouble(getStandard("h","1.4"));
			} else {
				width = Double.parseDouble(position[2]);
				height = Double.parseDouble(position[3]);
			}
			Double leftOffset = Double.parseDouble(getStandard("offsetPercentageX", "0.0"));
			Double topOffset = Double.parseDouble(getStandard("offsetPercentageY", "0.0"));
			// some stencils are positioned using a offset (in percentage)
			oryxGlobalBounds[0] = left - (leftOffset / 100 * width);
			oryxGlobalBounds[1] = top - (topOffset / 100 * height);
			oryxGlobalBounds[2] = oryxGlobalBounds[0] + width;
			oryxGlobalBounds[3] = oryxGlobalBounds[1] + height;
			for (int i = 0; i < oryxGlobalBounds.length; i++){
				oryxGlobalBounds[i] = oryxGlobalBounds[i] * CENTIMETERTOPIXEL;
			}
		}
		return oryxGlobalBounds;
	}
	
	protected Double[] getOryxBounds(){
		Double[] localBounds = new Double[4];
		for (int i = 0; i < 4; i++){
			localBounds[i] = getOryxGlobalBounds()[i] - getParent().getOryxGlobalBounds()[i%2];
		}
		return localBounds;
	}
	
	/**
	 * gives the center back
	 * @return x, y of the center
	 */
	public Double[] getCenter(){
		if (oryxGlobalBounds == null){
			getOryxGlobalBounds(); 
		}
		return new Double[]{(oryxGlobalBounds[0] + oryxGlobalBounds[2])/2, (oryxGlobalBounds[1] + oryxGlobalBounds[3])/2};   
	}

	
	// s t a n d a r d   v a l u e s

	/**
	 * returns a standard value of the stored values if available or the default 
	 * @param attribute 
	 * @return
	 * @throws JSONException 
	 */
	public String getStandard(String attribute, String defaultValue){
		return Unifier.getStandardValue(getOryxIdentifier(), attribute, defaultValue);
			
	}

	public boolean isInstance(){
		return true;
	}
	
	
//*************************************************************************
//* Java -> JSON
//*************************************************************************

	
	/**
	 * write all childshapes of the stencil
	 */
	public void writeJSONchildShapes(JSONObject json) throws JSONException {
		Logger.d("ChildShapes called by "+getName()+"("+getAdonisIdentifier()+")");
		JSONArray childShapes = getJSONArray(json,"childShapes");
		JSONObject shape = null;
		
		for (AdonisStencil anInstance : getModelChildren().values()){
			//write only my childshapes
			if (anInstance.isInstance() && anInstance.getParent() == this){
				shape = new JSONObject();
				anInstance.writeJSON(shape);
				childShapes.put(shape);
			}
		}
	}

	private void putProperty(JSONObject json, String identifier) throws JSONException{
		AdonisAttribute attribute = getAttribute(identifier,getLanguage());
		if (attribute != null && attribute.getElement() != null){
			String value = attribute.getElement();
			if (attribute.getType().equals("BOOLEAN") 
					|| attribute.getType().equals("ENUMERATION")){
				value = Unifier.getOryxIdentifier(attribute.getElement(),getLanguage());
			} else if (!attribute.getType().equals("STRING") 
						&& !attribute.getType().equals("INTEGER")){
				Logger.i("possibly not considered attribute type "+attribute.getType());
			}
			json.put(identifier, value);
			addUsed(attribute);
		}
		
		
	}
	
	/**
	 * extract all mapped properties of stencil and try integrate them to oryx
	 * if mapping successful, mark attribute as used 
	 */
	@Override
	public void writeJSONproperties(JSONObject json) throws JSONException {
		JSONObject properties = getJSONObject(json,"properties");
		
		properties.put("name",getName());
	
		//add simple attributes (like STRING BOOLEAN or INTEGER type)
		String[] batchAttributes = {
				"categories","documentation","description","comment",
				"role","entity","open questions","external process","order",
				"display watermark","text","representation","graphical representation"};
		for (String attribute : batchAttributes){
			putProperty(properties, attribute);
		}

		AdonisAttribute element = null;
		element = getAttribute("display name",getLanguage());
		if (element != null && element.getElement() != null){
			properties.put("display name", element.getElement().equalsIgnoreCase("yes") ? true : false);
			addUsed(element);
		}
		
		//store all required data of referenced process in a string of 
		//format: modelname # modelversion [ modeltype ]
		AdonisInterref modelReferences = getInterref("referenced process",getLanguage());
		if (modelReferences != null && modelReferences.getIref() != null){
			int counter = 0;
			for (AdonisIref reference : modelReferences.getIref()){
				if (Unifier.getOryxIdentifier(reference.getType(),getLanguage()).equals("modelreference")){
					counter++;
					properties.put(
							"modelreference", 
							reference.getTmodelname()
								+" #"+reference.getTmodelver()
								+" ["+Unifier.getOryxIdentifier(reference.getTmodeltype(),getLanguage())+"]");
					addUsed(reference);
					Logger.i("added modelrerference from instance "+reference.getTmodelname()+" #"+reference.getTmodelver()+" ["+Unifier.getOryxIdentifier(reference.getTmodeltype(),getLanguage())+"]");
				} else {
					Logger.i("ignored reference from instance ");
				}
			}
			// there should be only one reference
			Assert.isTrue(counter <= 1);
		}
		
		
		AdonisRecord performanceIndicatorOverview = getRecord("performance indicator overview",getLanguage());
		if (performanceIndicatorOverview != null){
			String value = "";
			AdonisRow[] rows = performanceIndicatorOverview.getRow().toArray(new AdonisRow[0]);
			Arrays.sort(rows,new Comparator<AdonisRow>(){
				@Override
				public int compare(AdonisRow lhs, AdonisRow rhs) {
					return rhs.getNumber() - lhs.getNumber(); 
				} 
			});
			AdonisAttribute attribute = null;
			AdonisInterref interref = null;
			for (int i = 0; i < rows.length; i++){
				AdonisRow row = rows[i];
				attribute = row.getAttribute("status",getLanguage());
				value += Helper.removeExpressionTags(attribute.getElement());
				value += " : ";
				interref = row.getInterref("reference", getLanguage());
				//there should only be one reference at all
				AdonisIref reference = interref.getIref().get(0);
				value += reference.getTobjname();
				value += " [ ";
				value += reference.getTmodelname();
				value += " | ";
				value += reference.getTmodelver();
				value += " | ";
				value += reference.getTclassname();
				value += " | ";
				value += Unifier.getOryxIdentifier(reference.getTmodeltype(),getLanguage());
				value += " ] ";
				//TODO not all attributes which are necessary to restore are considered
				value += " Current value: ";
				attribute = row.getAttribute("current value", getLanguage());
				value += Helper.removeExpressionTags(attribute.getElement());
				value += " | Score: ";
				attribute = row.getAttribute("score", getLanguage());
				value += Helper.removeExpressionTags(attribute.getElement());
				value += " | Target value: ";
				attribute = row.getAttribute("target value", getLanguage());
				value += Helper.removeExpressionTags(attribute.getElement());
				value += " | Updated: ";
				attribute = row.getAttribute("updated", getLanguage());
				value += Helper.removeExpressionTags(attribute.getElement());
				if (i < rows.length-1){
					value += "\n";
				}
			}
			Assert.doesNotContain(value, "EXPR");
			properties.put("indicators",value);
		}
	}

	@Override
	public void writeJSONdockers(JSONObject json) throws JSONException {
		// not needed?
		getJSONArray(json, "dockers");
		
	}

	/**
	 * write transformed bounds
	 */
	@Override
	public void writeJSONbounds(JSONObject json) throws JSONException {
		// try to give the node a nice size
		JSONObject bounds = getJSONObject(json,"bounds");
		JSONObject upperLeft = getJSONObject(bounds,"upperLeft");
		JSONObject lowerRight = getJSONObject(bounds,"lowerRight");
		
		upperLeft.put("x",getOryxBounds()[0]);
		upperLeft.put("y",getOryxBounds()[1]);
		
		lowerRight.put("x",getOryxBounds()[2]);
		lowerRight.put("y",getOryxBounds()[3]);	
	}

	/**
	 * write outgoing edges
	 */
	@Override
	public void writeJSONoutgoing(JSONObject json) throws JSONException {
		JSONArray outgoing = getJSONArray(json,"outgoing");
		JSONObject temp = null;
		
		for (AdonisStencil aStencil: getModelChildren().values()){
			if (aStencil.isConnector() && ((AdonisConnector)aStencil).getFrom().getInstanceName().equals(getName())){
				temp = new JSONObject();
				temp.put("resourceId", aStencil.getResourceId());
				outgoing.put(temp);
			}
		}
	}
	
	/**
	 * store unused attributes, records, interrefs in container to restore them in export
	 */
	public void writeJSONunused(JSONObject json) throws JSONException{
		//TODO only for development - remove
		if (true)
			return;
		//JSONObject unused = getJSONObject(json, "unused");
		SerializableContainer<XMLConvertible> unused = new SerializableContainer<XMLConvertible>();
		
		try {
			for (AdonisAttribute aAttribute : getAttribute()){
				if (getUsed().indexOf(aAttribute) < 0){
					unused.getElements().add(aAttribute);
				}
			}
			for (AdonisRecord aRecord : getRecord()){
				if (getUsed().indexOf(aRecord) < 0){
					unused.getElements().add(aRecord);
				}
			}
			for (AdonisInterref aInterref : getInterref()){
				if (getUsed().indexOf(aInterref) < 0){
					unused.getElements().add(aInterref);
				}
			}
			//unused.put("attributes", makeStorable(unusedAttributes));
			json.put("unused", makeStorable(unused));
		} catch (JSONException e) {
			Logger.e("could not write unused elements and attributes",e);
		}
	}
	
//*************************************************************************
//* JSON -> Java
//*************************************************************************
	
	/**
	 * this method is called after everything is read to consider parent child 
	 * relations
	 */
	public void calculateAdonisPosition(){
		String type = getOryxIdentifier().contains("swimlane") ? "SWIMLANE" : "NODE";
		
		DecimalFormat f = new DecimalFormat("#.00");
		DecimalFormatSymbols p = new DecimalFormatSymbols();
		p.setDecimalSeparator('.');
		f.setDecimalFormatSymbols(p);
		
		StringBuffer adonisBounds = new StringBuffer();
		adonisBounds.append(type);
		adonisBounds.append(" ");
		adonisBounds.append("x:"+f.format(getAdonisGlobalBounds()[0]) +"cm ");
		adonisBounds.append("y:"+f.format(getAdonisGlobalBounds()[1]) +"cm ");
		adonisBounds.append("w:"+f.format(getAdonisGlobalBounds()[2]) +"cm ");
		adonisBounds.append("h:"+f.format(getAdonisGlobalBounds()[3]) +"cm ");
		
		getAttribute().add(
				AdonisAttribute.create(
					getLanguage(),
					"position",
					"STRING",
					adonisBounds.toString()));
	}
	
	/**
	 * post read in
	 * write attributes like bounds which depend on knowledge of the parent
	 * add yourself to created model
	 */
	@Override
	public void completeOryxToAdonis(){
		Logger.d("read in Bounds of stencil: "+getOryxIdentifier()+" named: "+getName());
		
		
		
		Logger.d("Created instance class "+getOryxIdentifier()+" - "+getName());
		getModel().getInstance().add(this);
		super.completeOryxToAdonis();
	}
	
	
	/**
	 * extract unused attributes etc. from stencil in hope they are not conflicting with
	 * edited ones
	 * @param json
	 */
	@SuppressWarnings("unchecked")
	public void readJSONunused(JSONObject json){
		SerializableContainer<XMLConvertible> unused;
		String encodedString;
		try {
			encodedString = json.getString("unused");
			if (encodedString != null){
				unused = (SerializableContainer<XMLConvertible>) fromStorable(encodedString);
				for (XMLConvertible element : unused.getElements()){
					if (element.getClass() == AdonisAttribute.class){
						getAttribute().add((AdonisAttribute)element);
					}
					if (element.getClass() == AdonisRecord.class){
						getRecord().add((AdonisRecord)element);
					}
					if (element.getClass() == AdonisInterref.class){
						getInterref().add((AdonisInterref)element);
					}
				}
			}
		} catch (JSONException e){
			Logger.e("could not restore unused attributes",e);
		}
		
	}
	
	/**
	 * read in stencil and set related attributes
	 * @param json
	 * @throws JSONException
	 */
	public void readJSONstencil(JSONObject json) throws JSONException{
		if (getAdonisIdentifier() == null){
			JSONObject stencil = json.getJSONObject("stencil");
			setOryxIndentifier(stencil.getString("id"));
//			setAdonisIdentifier(getAdonisIdentifier()());
			Logger.d("working on stencil: "+getOryxIdentifier());
		}
	}
	
	public void readJSONdockers(JSONObject json){
		//not needed?
	}
	
	/**
	 * read in the childshapes and trigger read in for them
	 * @param json
	 * @throws JSONException
	 */
	public void readJSONchildShapes(JSONObject json) throws JSONException{
		Logger.d("read in ChildShapes of an instance");
		JSONArray childShapes = json.getJSONArray("childShapes");
		JSONObject stencil = null;
		AdonisInstance instance = null;
		
		String stencilResourceId = null;
		for (int i = 0; i < childShapes.length(); i++){
			stencil = childShapes.getJSONObject(i);
			stencilResourceId = stencil.getString("resourceId");
			if (AdonisInstance.handleStencil(stencil.getJSONObject("stencil").getString("id"))){
				for (AdonisStencil aStencil : getModel().getModelChildren().values()){
					Assert.notNull(aStencil.resourceId);
					if (aStencil.isInstance() && stencilResourceId.equals(aStencil.resourceId)){
						instance = (AdonisInstance)aStencil;
					}
				}
				if (instance == null){
					instance = new AdonisInstance();
					instance.setResourceId(stencilResourceId);
					instance.setModel(getModel());
					instance.setParent(this);
				}
				instance.readJSON(stencil);
				
				//we need to save the father - child relation in a connector
				getModel().getConnector().add(
						AdonisConnector.insideRelation(
								getLanguage(),
								getModel(), 
								this, 
								instance));
				
				instance = null;
//				Log.d("read in ChildShape of ("+getOryxStencilClass()+" - "+getName()+")"+stencil.getJSONObject("stencil").getString("id")+" named: "+stencil.getJSONObject("properties").getString("name"));
				
			}
		}
	}
	
	/**
	 * read in the connections and create or complete connectors  
	 * @param json
	 * @throws JSONException
	 */
	public void readJSONoutgoing(JSONObject json) throws JSONException{
		JSONArray outgoing = json.getJSONArray("outgoing");
		
		AdonisConnectionPoint connectionPoint = null;
		
		String connectorResourceId = null;
		AdonisConnector connector = null;
	
		for (int i = 0; i < outgoing.length(); i++){
			
			connectionPoint = new AdonisConnectionPoint();
			connectionPoint.setInstance(this);
			connectorResourceId = outgoing.getJSONObject(i).getString("resourceId");
			//look for an existing stencil with this resource id
			connector = (AdonisConnector)getModelChildren().get(connectorResourceId);
			
			if (connector != null){
				//add this to existing connector
				connector.setFrom(connectionPoint);
				Logger.d("complete connector \""+connectorResourceId+"\" source from  - "+getName());
			} else {
				//create a new connector with this as start point
				connector = new AdonisConnector();
				connector.setResourceId(connectorResourceId);
				connector.setModel(getModel());
				connector.setFrom(connectionPoint);
				Logger.d("create connector \""+connectorResourceId+"\" source from  - "+getName());
			}
			Assert.isTrue(getModelChildren().containsValue(connector));
			connector = null;
		}
		
	}
	
	public void readJSONtarget(JSONObject json){
		//not needed?
	}
	
	/**
	 * prepares bounds for post processing
	 * @param json
	 * @throws JSONException
	 */
	public void readJSONbounds(JSONObject json) throws JSONException{
		JSONObject bounds = json.getJSONObject("bounds");
		JSONObject upperLeft = bounds.getJSONObject("upperLeft");
		JSONObject lowerRight = bounds.getJSONObject("lowerRight");
		
		oryxGlobalBounds = new Double[4];
		oryxGlobalBounds[0] = upperLeft.getDouble("x");
		oryxGlobalBounds[1] = upperLeft.getDouble("y");
		oryxGlobalBounds[2] = lowerRight.getDouble("x");
		oryxGlobalBounds[3] = lowerRight.getDouble("y");
	}
	


	
	/**
	 * read in properties of stencil and store them in AdonisAttributes
	 * TODO currently a lot of attributes are hardcoded for the english version
	 * @param json
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	public void readJSONproperties(JSONObject json) throws JSONException {
		JSONObject propertyObject = json.getJSONObject("properties");
		if (propertyObject == null){
			return;
		}
		
		
		String attribute = null;
		HashMap<String,String> properties = new HashMap<String,String>();
		Iterator<String> keyIterator = propertyObject.keys();
		String key = null;
		while(keyIterator.hasNext()){
			key = keyIterator.next();
			properties.put(key,propertyObject.getString(key));
		}
		
		attribute = properties.get("name");
		if (getName() == null){
			setName(attribute);
			Logger.d("read in Name of stencil : "+getName());
		}
		
		attribute = properties.get("categories");
		if (attribute != null){
			addAttribute("categories", getLanguage(), attribute);
		}
		
		attribute = properties.get("documentation");
		if (attribute != null){
			addAttribute("documentation", getLanguage(), attribute);
		}
		
		attribute = properties.get("description");
		if (attribute != null){
			addAttribute("description", getLanguage(), attribute);
		}
		attribute = properties.get("comment");
		if (attribute != null){
			addAttribute("comment", getLanguage(), attribute);
		}
		attribute = properties.get("role");
		if (attribute != null){
			addAttribute("role", getLanguage(), attribute);
		}
		attribute = properties.get("entity");
		if (attribute != null){
			addAttribute("entity", getLanguage(), attribute);
		}
		attribute = properties.get("open questions");
		if (attribute != null){
			addAttribute("open questions", getLanguage(), attribute);
		}
		attribute = properties.get("order");
		if (attribute != null){
			addAttribute("order", getLanguage(), attribute);
		}
		attribute = properties.get("external process");
		if (attribute != null){
			addAttribute("external process", getLanguage(), Unifier.getAdonisIdentifier(attribute,getLanguage()));
		}
		attribute = properties.get("representation");
		if (attribute != null){
			addAttribute("representation", getLanguage(), attribute);
		}
		attribute = properties.get("display name");
		if (attribute != null){
			addAttribute("display name", getLanguage(), Boolean.parseBoolean(attribute) ? 
					Unifier.getAdonisIdentifier("yes",getLanguage()) :	Unifier.getAdonisIdentifier("no",getLanguage()));
		}
		attribute = properties.get("display watermark");
		if (attribute != null){
			addAttribute("display watermark", getLanguage(), Unifier.getAdonisIdentifier(attribute,getLanguage()));
		}
		attribute = properties.get("graphical representation");
		if (attribute != null){
			addAttribute("graphical representation", getLanguage(), Unifier.getAdonisIdentifier(attribute,getLanguage()));
		}
		attribute = properties.get("modelreference");
		if (attribute != null && attribute.length() > 7){
			int versionStart = attribute.lastIndexOf('#');
			int versionEnd = attribute.substring(versionStart).indexOf(' ')+versionStart;
			int modeltypeStart = attribute.lastIndexOf('[')+1;
			int modeltypeEnd = attribute.lastIndexOf(']');
			String modelver = "";
			String modeltype = "";
			if (modeltypeStart < modeltypeEnd){
				modelver = attribute.substring(
						versionStart,
						versionEnd < versionStart ? attribute.length()-1 : versionEnd).replaceAll("\\D", "");
				try {
					modelver = ""+Integer.parseInt(modelver);
				} catch (NumberFormatException e){
					Logger.i("wrong version number format",e);
					modelver = "";
				}
				modeltype = attribute.substring(modeltypeStart, modeltypeEnd);
				
			}
			AdonisInterref modelReference = getInterref("modelreference",getLanguage());
			if (modelReference == null){
				modelReference = AdonisInterref.createInterref("modelreference",getLanguage());
				getInterref().add(modelReference);
			}
			modelReference.getIref().add(
					AdonisIref.create(
						getLanguage(),
						"modelreference",
						modeltype,
						attribute.substring(0,modeltypeStart).trim(),
						modelver));
				
		}
		
		attribute = properties.get("indicators");
		if (attribute != null && attribute != ""){
			String[] indicators = attribute.split("\n");
			if (indicators.length > 0){
				AdonisRecord performanceIndicatorOverview = AdonisRecord.create("performance indicator overview",getLanguage());
				getRecord().add(performanceIndicatorOverview);
				for (int i = 0; i < indicators.length; i++){
					String indicator = indicators[i];
					
					int endStatus = indicator.indexOf(":");
					int endObjectName = indicator.indexOf("[",endStatus+1);
					int endModelName = indicator.indexOf("|", endObjectName+1);
					int endModelVersion = indicator.indexOf("|", endModelName+1);
					int endClassName = indicator.indexOf("|", endModelVersion+1);
					int endModelType = indicator.indexOf("]",endClassName+1);
					int endCurrentValue = indicator.indexOf("|",endModelType+1);
					int endScore = indicator.indexOf("|", endCurrentValue+1);
					int endTargetValue = indicator.indexOf("|", endScore+1);
					String status = indicator.substring(0, endStatus).trim();
					String objectName = indicator.substring(endStatus+1, endObjectName).trim();
					String modelName = indicator.substring(endObjectName+1, endModelName).trim();
					String modelVersion = indicator.substring(endModelName+1, endModelVersion).trim();
					String className = indicator.substring(endModelVersion+1, endClassName).trim();
					String modelType = indicator.substring(endClassName+1, endModelType).trim();
					String currentValue = indicator.substring(endModelType+1, endCurrentValue).replaceFirst("Current value\\:","").trim();
					String score = indicator.substring(endCurrentValue+1, endScore).replaceFirst("Score\\:","").trim();
					String targetValue = indicator.substring(endScore+1, endTargetValue).replaceFirst("Target value\\:","").trim();
					String updated = indicator.substring(endTargetValue+1, indicator.length()).replaceFirst("Updated\\:","").trim();
					AdonisRow row = AdonisRow.create(i+1);
					row.getAttribute().add(AdonisAttribute.create(getLanguage(), "status", "EXPRESSION", "EXPR val:\""+status+"\""));
					row.getAttribute().add(AdonisAttribute.create(getLanguage(), "current value", "EXPRESSION", "EXPR val:\""+currentValue+"\""));
					row.getAttribute().add(AdonisAttribute.create(getLanguage(), "score", "EXPRESSION", "EXPR val:\""+score+"\""));
					row.getAttribute().add(AdonisAttribute.create(getLanguage(), "target value", "EXPRESSION", "EXPR val:\""+targetValue+"\""));
					row.getAttribute().add(AdonisAttribute.create(getLanguage(), "updated", "EXPRESSION", "EXPR val:\""+updated+"\""));
					AdonisInterref reference = AdonisInterref.createInterref("reference",getLanguage());
					AdonisIref iref = AdonisIref.create(getLanguage(),"objectreference", modelType, modelName, modelVersion);
					iref.setTclassname(className);
					iref.setTobjname(objectName);
					reference.getIref().add(iref);
					
					row.getInterref().add(reference);
					performanceIndicatorOverview.getRow().add(row);
				}
				
			}
		}
	}
}

	