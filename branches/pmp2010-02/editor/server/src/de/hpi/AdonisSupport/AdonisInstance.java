package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	@Element(name="ATTRIBUTE", targetType=AdonisAttribute.class)
	protected ArrayList<AdonisAttribute> attribute;
		
	public ArrayList<AdonisAttribute> getAttribute(){return attribute;}
	
	public void setAttribute(ArrayList<AdonisAttribute> list){attribute = list;}
	
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
		return interref;
	}
	
	public void setInterref(ArrayList<AdonisInterref> list){
		interref = list;
	}
	
	public ArrayList<AdonisRecord> getRecord(){
		return record;
	}
	
	public void setRecord(ArrayList<AdonisRecord> list){
		record = list;
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

	private Double[] globalBounds = null;
	/**
	 * get the bounds
	 * @return upperLeft x,y lowerRight x,y
	 */
	protected Double[] getGlobalBounds(){
		if (globalBounds != null){
			return globalBounds;
		}
		globalBounds = new Double[4];
		
		//get the position and size which looks like 
		//	NODE x:2.50cm y:7.00cm index:2 or
		//	NODE x:1cm y:11.5cm w:.5cm h:.6cm index:8
		for (AdonisAttribute aAttribute : getAttribute()){
			if (aAttribute.getName().equalsIgnoreCase("Position")){
				// extract the numbers out of the string
				
				String[] position = filterPositionString(aAttribute.getElement());
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
				Log.v(getOryxStencilClass()+" x "+x+" w "+w+" off "+getLeftOffset()+" - "+y+" h "+h+" off "+getTopOffset());
				globalBounds[0] = x - (getLeftOffset() / 100 * w);
				globalBounds[1] = y - (getTopOffset() / 100 * h);
				globalBounds[2] = globalBounds[0] + ((100 - getLeftOffset()) / 100 * w);
				globalBounds[3] = globalBounds[1] + ((100 - getTopOffset()) / 100 * h);
				for (int i = 0; i < globalBounds.length; i++){
					globalBounds[i] = globalBounds[i] * CENTIMETERTOPIXEL;
				}
				break;
			}
		}
		return globalBounds;
	}
	
	protected Double[] getBounds(){
		Double[] localBounds = new Double[4];
		if (globalBounds == null){
			getGlobalBounds();
		}
		for (int i = 0; i < 4; i++){
			localBounds[i] = globalBounds[i] - getParent().getGlobalBounds()[i%2];
		}
		return localBounds;
	}
	
	
	public Double[] getCenter(){
		if (globalBounds == null){
			getGlobalBounds(); 
		}
		return new Double[]{(globalBounds[0] + globalBounds[2])/2, (globalBounds[1] + globalBounds[3])/2};   
	}
	/**
	 * these attributes have influences on the representation in oryx
	 * so they are stored including the type
	 */
	@Override
	public Map<String, String> getEvaluatedAttributes() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("Position", "STRING");
		if (getOryxStencilClass().equalsIgnoreCase("")){
			
		}
		return null;
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
				return standard.getDouble("offsetPercentageX");
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

//	@Override
//	public void writeJSONchildShapes(JSONObject json) throws JSONException {
//		getJSONArray(json,"childShapes");
//		
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
		
		JSONObject temp = getJSONObject(bounds,"upperLeft");
		temp.put("x",getBounds()[0]);
		temp.put("y",getBounds()[1]);
		
		temp = getJSONObject(bounds,"lowerRight");
		temp.put("x",getBounds()[2]);
		temp.put("y",getBounds()[3]);	
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

	

	
//	public void write(JSONObject json) throws JSONException{
//		if (!stencilClass.contains("Swimlane") && 
//				(stencilClass.contains("actor") ||
//						stencilClass.contains("Process"))){
//			super.write(json);
//		}
//	}
}
