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
	
	private Double[] bounds = null; 
	
	/**
	 * get the bounds
	 * @return upperLeft x,y lowerRight x,y
	 */
	protected Double[] getBounds(){
		if (bounds != null){
			return bounds;
		}
		bounds = new Double[4];
		//get the position and size which looks like 
		//	NODE x:2.50cm y:7.00cm index:2 or
		//	NODE x:1cm y:11.5cm w:.5cm h:.6cm index:8
		for (AdonisAttribute aAttribute : getAttribute()){
			if (aAttribute.getName().equalsIgnoreCase("Position")){
				// extract the numbers out of the string
				String area = aAttribute.getElement().replace("NODE ","");
				area = area.replace("SWIMLANE ","");
				area = area.replace("index:", "");
				area = area.replace("x:", "");
				area = area.replace("y:", "");
				area = area.replace("w:", "");
				area = area.replace("h:", "");
				area = area.replace("cm","");
				String[] position = area.split(" ");
				
				//upperLeft
				bounds[0] = Double.parseDouble(position[0])*CENTIMETERTOPIXEL;
				bounds[1] = Double.parseDouble(position[1])*CENTIMETERTOPIXEL;
				//lowerRight
				//TODO look if there is variant with 3 space values (x y w or x y h)
				if (position.length <= 3){
					bounds[2] = (getStandardSize(true))*CENTIMETERTOPIXEL + bounds[0];
					bounds[3] = (getStandardSize(false))*CENTIMETERTOPIXEL + bounds[1];
				} else {
					bounds[2] = Double.parseDouble(position[2])*CENTIMETERTOPIXEL + bounds[0];
					bounds[3] = Double.parseDouble(position[3])*CENTIMETERTOPIXEL + bounds[1];
				}
				break;
			}
		}
		return bounds;
	}
	
	
	public Double[] getCenter(){
		if (bounds == null){
			getBounds(); 
		}
		return new Double[]{(bounds[0] + bounds[2])/2, (bounds[1] + bounds[3])/2};   
	}
	/**
	 * these attributes have influences on the representation in oryx
	 * so they are stored including the type
	 */
	@Override
	public Map<String, String> getEvaluatedAttributes() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("Position", "STRING");
		if (getStencilClass().equalsIgnoreCase("")){
			
		}
		return null;
	}
	
	/**
	 * returns the standard size of a stencil in cm (according to the used unit in the xml)
	 * @param isVertical
	 * @return
	 */
	public double getStandardSize(boolean isVertical){
		if (getStencilClass().equalsIgnoreCase("Process"))
			return (isVertical ? 3.25 : 1.5); 
		return (isVertical ? 3.25 : 1.5);
	}

	public boolean isInstance(){
		return true;
	}
	
	
	
	//*************************************************************************
	//* write methods for JSON
	//*************************************************************************

	@Override
	public void writeJSONchildShapes(JSONObject json) throws JSONException {
		getJSONArray(json,"childShapes");
		
	}

	@Override
	public void writeJSONproperties(JSONObject json) throws JSONException {
		JSONObject properties = getJSONObject(json,"properties");
//		properties.put("id",getId());
//		properties.put("class",getStencilClass());
//		properties.put("name",getName());
		
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
		temp.put("x",(int)(double)getBounds()[0]);
		temp.put("y",(int)(double)getBounds()[1]);
		
		temp = getJSONObject(bounds,"lowerRight");
		temp.put("x",(int)(double)getBounds()[2]);
		temp.put("y",(int)(double)getBounds()[3]);	
	}

	@Override
	public void writeJSONoutgoing(JSONObject json) throws JSONException {
		JSONArray outgoing = getJSONArray(json,"outgoing");
		JSONObject temp = null;
		for (AdonisConnector connector : getModel().getConnector()){
			if (connector.getFrom().getInstance().equals(getName())){
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
