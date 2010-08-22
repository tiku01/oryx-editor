package de.hpi.AdonisSupport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
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
	
	public AdonisAttribute getAttribute(String identifier){
		for (AdonisAttribute anAttribute : getAttribute()){
			if (identifier.equals(anAttribute.getOryxName()))
				return anAttribute;
		}
		return null;
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
		Double left = oryxGlobalBounds[0];
		Double top = oryxGlobalBounds[1];
		Double width = oryxGlobalBounds[2] - left;
		Double height = oryxGlobalBounds[3] - top;
				
		left = left + (getLeftOffset()) / 100 * width  + getParent().getOryxGlobalBounds()[0];
		top = top + (getTopOffset()) / 100 * height + getParent().getOryxGlobalBounds()[1];
		
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
		AdonisAttribute adonisPosition = getAttribute("Position"); 
		if (adonisPosition != null){
			// extract the numbers out of the string
			addUsed(adonisPosition);
			String[] position = filterPositionString(adonisPosition.getElement());
			Double left = Double.parseDouble(position[0]);
			Double top = Double.parseDouble(position[1]);
			Double width;
			Double height;
			if (position.length <= 3){
				width = getStandardWidth();
				height = getStandardHeigth();
			} else {
				width = Double.parseDouble(position[2]);
				height = Double.parseDouble(position[3]);
			}
			// some stencils are positioned using a offset (in percentage)
			oryxGlobalBounds[0] = left - (getLeftOffset() / 100 * width);
			oryxGlobalBounds[1] = top - (getTopOffset() / 100 * height);
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
	
	/**
	 * returns the standard size of a stencil in cm (according to the used unit in the xml)
	 * @param isVertical
	 * @return
	 * @throws JSONException 
	 */
	public Double getStandardWidth(){
		try {
			JSONObject standard = getStandardConfiguration();
			if (standard != null){
				return standard.getDouble("w");
			}
		} catch (JSONException e){
			Log.e(e.getMessage());
		}
		Log.v("No standard width values avaiable for: "+getOryxStencilClass());
		return 3.25;
			
	}
	
	// s t a n d a r d   v a l u e s
	public Double getStandardHeigth(){
		try {
			JSONObject standard = getStandardConfiguration();
			if (standard != null){
				return standard.getDouble("h");
			}
		} catch (JSONException e){
			Log.e(e.getMessage());
		}
		Log.v("No standard heigth values avaiable for: "+getOryxStencilClass());
		return 1.4;
			
	}
	
	public Double getLeftOffset(){
		try {
			JSONObject standard = getStandardConfiguration();
			if (standard != null){
				return standard.getDouble("offsetPercentageX");
			}
		} catch (JSONException e){
			Log.e(e.getMessage());
		}
		Log.v("No standard offset percentage from left avaiable for: "+getOryxStencilClass());
		return 0.0;
	}
	
	public Double getTopOffset(){
		try {
			JSONObject standard = getStandardConfiguration();
			if (standard != null){
				return standard.getDouble("offsetPercentageY");
			}
		} catch (JSONException e){
			Log.e(e.getMessage());
		}
		Log.v("No standard offset percentage from top avaiable for: "+getOryxStencilClass());
		return 0.0;
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
		Log.w("ChildShapes called by "+getName()+"("+getStencilClass()+")");
		JSONArray childShapes = getJSONArray(json,"childShapes");
		JSONObject shape = null;
		
		for (AdonisStencil anInstance : getModelChildren().values()){
			//write only my childshapes
			if (anInstance.isInstance() && anInstance.getParent() == this){
				shape = new JSONObject();
				anInstance.write(shape);
				childShapes.put(shape);
			}
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
		
		AdonisAttribute element = null;
		
		element = getAttribute("Subprocess name");
		if (element != null && element.getElement() != null){
			properties.put("subprocessname", element.getElement().replace("EXPR val:", "").replace("\"", ""));
			addUsed(element);
		}
		element = getAttribute("Categories");
		if (element != null && element.getElement() != null){
			properties.put("categories", element.getElement());
			addUsed(element);
		}
		element = getAttribute("Documentation");
		if (element != null && element.getElement() != null){
			properties.put("documentation", element.getElement());
			addUsed(element);
		}
		element = getAttribute("Description");
		if (element != null && element.getElement() != null){
			properties.put("description", element.getElement());
			addUsed(element);
		}
		element = getAttribute("Comment");
		if (element != null && element.getElement() != null){
			properties.put("Comment", element.getElement());
			addUsed(element);
		}
		element = getAttribute("Open questions");
		if (element != null && element.getElement() != null){
			properties.put("open questions", element.getElement());
			addUsed(element);
		}
		element = getAttribute("External process");
		if (element != null && element.getElement() != null){
			properties.put("external process", element.getElement());
			addUsed(element);
		}
		element = getAttribute("Order");
		if (element != null && element.getElement() != null && getOryxStencilClass().equals("process")){
			properties.put("order", element.getElement());
			addUsed(element);
		}
		
		element = getAttribute("Display water marks");
		if (element != null){
			if (element.getElement() != null){
				properties.put("display watermark", element.getElement() == "Yes" ? true : false);
			} else {
				properties.put("display watermark", false);
			}
			addUsed(element);
		}
		
		element = getAttribute("Text");
		if (element != null && element.getElement() != null){
			properties.put("text", element.getElement());
			addUsed(element);
		}
	
	
		element = getAttribute("Representation");
		if (element != null && element.getElement() != null){
			properties.put("representation", element.getElement());
			addUsed(element);
		}
		element = getAttribute("Display name");
		if (element != null && element.getElement() != null){
			properties.put("display name", element.getElement().equals("Yes") ? true : false);
			addUsed(element);
		}
		
		element = getAttribute("Graphical representation");
		if (element != null && element.getElement() != null){
			properties.put("graphical representation", element.getElement());
			addUsed(element);
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
			Log.e("could not write unused elements and attributes\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
//*************************************************************************
//* JSON -> Java
//*************************************************************************
	
	/**
	 * "is inside" connectors are not displayed connectors of adonis to
	 * mark parent - child relations -> these must be create explicit
	 */
	private void createIsInsideConnector(AdonisInstance childShape){
		//we need to save the father - child relation in a connector
		AdonisConnector isInside = new AdonisConnector();
		
		
		isInside.setStencilClass("Is inside");
		isInside.getResourceId();
		isInside.setModel(getModel());
		
		//set the target (to) to the father
		AdonisConnectionPoint point = new AdonisConnectionPoint();
		point.setInstance(this);
		isInside.setTo(point);
		//set the source (from) to the child
		point = new AdonisConnectionPoint();
		point.setInstance(childShape);
		isInside.setFrom(point);
		
		AdonisAttribute attribute = new AdonisAttribute();
		attribute.setName("AutoConnect");
		attribute.setType("Is inside");
		
		ArrayList<AdonisAttribute> list =new ArrayList<AdonisAttribute>();
		list.add(attribute);
		isInside.setAttribute(list);
		
		getModel().getConnector().add(isInside);
	}
	
	/**
	 * post read in
	 * write attributes like bounds which depend on knowledge of the parent
	 * add yourself to created model
	 */
	@Override
	public void completeOryxToAdonis(){
		Log.d("read in Bounds of stencil: "+getOryxStencilClass()+" named: "+getName());
		String type = null;
		
		
		DecimalFormat f = new DecimalFormat("#.00");
		DecimalFormatSymbols p = new DecimalFormatSymbols();
		p.setDecimalSeparator('.');
		f.setDecimalFormatSymbols(p);
		
		StringBuffer adonisBounds = new StringBuffer();
		adonisBounds.append(type != null ? type : "NODE");
		adonisBounds.append(" ");
		adonisBounds.append("x:"+f.format(getAdonisGlobalBounds()[0]) +"cm ");
		adonisBounds.append("y:"+f.format(getAdonisGlobalBounds()[1]) +"cm ");
		adonisBounds.append("w:"+f.format(getAdonisGlobalBounds()[2]) +"cm ");
		adonisBounds.append("h:"+f.format(getAdonisGlobalBounds()[3]) +"cm ");
		adonisBounds.append("index:"+getIndex());
		
		AdonisAttribute temp = new AdonisAttribute();
		temp.setElement(adonisBounds.toString());
		temp.setName("Position");
		temp.setType("STRING");
		getAttribute().add(temp);
		
		
		Log.d("Created instance class "+getOryxStencilClass()+" - "+getName());
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
			Log.e("could not restore unused attributes");
		}
		
	}
	
	/**
	 * read in stencil and set related attributes
	 * @param json
	 * @throws JSONException
	 */
	public void readJSONstencil(JSONObject json) throws JSONException{
		if (getStencilClass() == null){
			JSONObject stencil = json.getJSONObject("stencil");
			setOryxStencilClass(stencil.getString("id"));
			setStencilClass(getAdonisStencilClass("en"));
			Log.d("working on stencil: "+getOryxStencilClass());
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
		Log.d("read in ChildShapes of an instance");
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
				instance.parse(stencil);
				
				//we need to save the father - child relation in a connector
				createIsInsideConnector(instance);
				
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
				Log.d("complete connector \""+connectorResourceId+"\" source from  - "+getName());
			} else {
				//create a new connector with this as start point
				connector = new AdonisConnector();
				connector.setResourceId(connectorResourceId);
				connector.setModel(getModel());
				connector.setFrom(connectionPoint);
				Log.d("create connector \""+connectorResourceId+"\" source from  - "+getName());
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
	public void readJSONproperties(JSONObject json) throws JSONException{
		JSONObject properties = json.getJSONObject("properties");
		if (getName() == null){
			setName(properties.getString("name"));
			Log.d("read in Name of stencil : "+getName());
		}
		
		String stringAttribute = null;
		Integer integerAttribute = null;
		Boolean booleanAttribute = null;
		stringAttribute = properties.optString("subprocessname");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Subprocess name","EXPRESSION","EXPR val:"+stringAttribute));
		}
		stringAttribute = properties.optString("categories");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Categories","STRING",stringAttribute));
		}
		stringAttribute = properties.optString("documentation");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Documentation","STRING",stringAttribute));
		}
		stringAttribute = properties.optString("description");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Description","STRING",stringAttribute));
		}
		stringAttribute = properties.optString("comment");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Comment","STRING",stringAttribute));
		}
		stringAttribute = properties.optString("open questions");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Open questions","STRING",stringAttribute));
		}
		integerAttribute = properties.optInt("order");
		if (integerAttribute != null){
			getAttribute().add(new AdonisAttribute("Order","INTEGER",integerAttribute.toString()));
		}
		stringAttribute = properties.optString("external process");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("External process","ENUMERATION",stringAttribute));
		}
		stringAttribute = properties.optString("representation");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Representation","ENUMERATION",stringAttribute));
		}
		booleanAttribute = properties.optBoolean("display name");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Representation","ENUMERATION",(booleanAttribute ? "Yes" : "No")));
		}
		stringAttribute = properties.optString("graphical representation");
		if (stringAttribute != null){
			getAttribute().add(new AdonisAttribute("Graphical representation","ENUMERATION",stringAttribute));
		}
	}
}

	