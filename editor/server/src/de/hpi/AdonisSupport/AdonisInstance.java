package de.hpi.AdonisSupport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
		return instances.contains(oryxName);
		
	}
	
	
	@Element(name="ATTRIBUTE")
	protected ArrayList<AdonisAttribute> attribute;
		
	public ArrayList<AdonisAttribute> getAttribute(){
		if (attribute == null){
			attribute = new ArrayList<AdonisAttribute>();
		}
		return attribute;
	}
	
	public void setAttribute(ArrayList<AdonisAttribute> list){
		attribute = list;
	}
	
	@Attribute(name="name")
	protected String name;

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
	
	public AdonisAttribute getAttribute(String identifier){
		for (AdonisAttribute anAttribute : getAttribute()){
			if (identifier.equals(anAttribute.getOryxName()))
				return anAttribute;
		}
		return null;
	}
	
	//*************************************************************************
	//* methods for computation purposes
	//*************************************************************************
	
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

	private Double[] oryxGlobalBounds = null;
	private Double[] adonisGlobalBounds = null;
	
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
		
		left = width * getLeftOffset() / 100 + left + getParent().getOryxGlobalBounds()[0];
		top = height * getTopOffset() / 100 + top + getParent().getOryxGlobalBounds()[1];
		
		adonisGlobalBounds[0] = left / CENTIMETERTOPIXEL/* + getParent().getAdonisGlobalBounds()[0]*/;
		adonisGlobalBounds[1] = top / CENTIMETERTOPIXEL/* + getParent().getAdonisGlobalBounds()[1]*/;
		adonisGlobalBounds[2] = width / CENTIMETERTOPIXEL;
		adonisGlobalBounds[3] = height / CENTIMETERTOPIXEL;
		//TODO move according the parent element - Adonis uses global positioning, oryx local
		
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
			Double x = Double.parseDouble(position[0]);
			Double y = Double.parseDouble(position[1]);
			Double w;
			Double h;
			if (position.length <= 3){
				w = getStandardWidth();
				h = getStandardHeigth();
			} else {
				w = Double.parseDouble(position[2]);
				h = Double.parseDouble(position[3]);
			}
			// some stencils are positioned using a offset (in percentage)
			oryxGlobalBounds[0] = x - (getLeftOffset() / 100 * w);
			oryxGlobalBounds[1] = y - (getTopOffset() / 100 * h);
			oryxGlobalBounds[2] = oryxGlobalBounds[0] + ((100 - getLeftOffset()) / 100 * w);
			oryxGlobalBounds[3] = oryxGlobalBounds[1] + ((100 - getTopOffset()) / 100 * h);
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
	//* write methods for JSON
	//*************************************************************************

//	@Override - this is handled by AdonisStencil
//	/**
//	 * write all childshapes to the diagram
//	 */
//	public void writeJSONchildShapes(JSONObject json) throws JSONException {
//		JSONArray childShapes = getJSONArray(json,"childShapes");
//		JSONObject shape = null;
//		
//		for (AdonisInstance aInstance : getModelInstance().values()){
//			//write only my childshapes
//			if (aInstance.getParent() == this){
//				shape = new JSONObject();
//				aInstance.write(shape);
//				childShapes.put(shape);
//			}
//		}
//	}

	@Override
	public void writeJSONproperties(JSONObject json) throws JSONException {
		JSONObject properties = getJSONObject(json,"properties");
//		properties.put("id",getId());
//		properties.put("class",getStencilClass());
		properties.put("name",getName());
		
//		TODO sort out
//		for (AdonisAttribute aAttribute : getAttribute()){
//			aAttribute.write(properties);
//		}
	}

	@Override
	public void writeJSONdockers(JSONObject json) throws JSONException {
		// TODO currently I have no idea what should be in this^^
		getJSONArray(json, "dockers");
		
	}

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

	@Override
	public void writeJSONoutgoing(JSONObject json) throws JSONException {
		JSONArray outgoing = getJSONArray(json,"outgoing");
		JSONObject temp = null;
		for (AdonisConnector connector : getModel().getConnector()){
			if (connector.getFrom().getInstance().equalsIgnoreCase((getName()))){
				temp = new JSONObject();
				temp.put("resourceId", connector.getResourceId());
				outgoing.put(temp);
			}
		}
	}

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
	//* write methods for JSON
	//*************************************************************************
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
		temp.setName("POSITION");
		temp.setType("STRING");
		getAttribute().add(temp);
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
	
	
	public void readJSONstencil(JSONObject json) throws JSONException{
		if (getStencilClass() == null){
			JSONObject stencil = json.getJSONObject("stencil");
			setOryxStencilClass(stencil.getString("id"));
			setStencilClass(getAdonisStencilClass("en"));
			Log.d("working on stencil: "+getOryxStencilClass());
		}
	}
	
	public void readJSONdockers(JSONObject json){
		//XXX currently nothing
	}
	
	public void readJSONchildShapes(JSONObject json) throws JSONException{
		readJSONstencil(json);
		readJSONbounds(json);
		Log.d("read in ChildShapes of stencil: "+getOryxStencilClass()+" named: "+getName());
		JSONArray childShapes = json.getJSONArray("childShapes");
		JSONObject stencil = null;
		for (int i = 0; i < childShapes.length(); i++){
			stencil = childShapes.getJSONObject(i);
			if (AdonisInstance.handleStencil(stencil.getJSONObject("stencil").getString("id"))){
				AdonisInstance anInstance = new AdonisInstance();
				Log.d("read in ChildShape of ("+getOryxStencilClass()+" - "+getName()+")"+stencil.getJSONObject("stencil").getString("id")+" named: "+stencil.getJSONObject("properties").getString("name"));
				anInstance.setModel(getModel());
				anInstance.setParent(this);
				anInstance.parse(stencil);
				getModel().getInstance().add(anInstance);
			}
		}
	}
	
	public void readJSONoutgoing(JSONObject json){
		//TODO needs to be implemented
	}
	
	public void readJSONtarget(JSONObject json){
		//XXX nothing to do for stencils?
	}
	
	public void readJSONresourceId(JSONObject json){
		Random random = new Random();
		setId("obj."+random.nextInt(100000));
	}
	
	public void readJSONbounds(JSONObject json) throws JSONException{
		JSONObject bounds = json.getJSONObject("bounds");
		JSONObject upperLeft = bounds.getJSONObject("upperLeft");
		JSONObject lowerRight = bounds.getJSONObject("lowerRight");
		
		oryxGlobalBounds = new Double[4];
		oryxGlobalBounds[0] = upperLeft.getDouble("y");
		oryxGlobalBounds[1] = upperLeft.getDouble("x");
		oryxGlobalBounds[2] = lowerRight.getDouble("x");
		oryxGlobalBounds[3] = lowerRight.getDouble("y");
	}
	
	public void readJSONproperties(JSONObject json) throws JSONException{
		JSONObject properties = json.getJSONObject("properties");
		if (getName() == null){
			setName(properties.getString("name"));
			Log.d("set name of stencil to: "+getName());
		}
	}
}

	