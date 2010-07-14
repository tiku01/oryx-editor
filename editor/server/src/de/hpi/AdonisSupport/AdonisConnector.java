package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import sun.security.action.GetBooleanAction;

//<!ELEMENT CONNECTOR (FROM, TO, (ATTRIBUTE | RECORD | INTERREF)*)>
//<!ATTLIST CONNECTOR
//  id    ID    #IMPLIED
//  class CDATA #REQUIRED
//>

@RootElement("CONNECTOR")
public class AdonisConnector extends AdonisStencil{
	
	@Element(name="ATTRIBUTE", targetType=AdonisAttribute.class)
	protected ArrayList<AdonisAttribute> attribute;
		
	public ArrayList<AdonisAttribute> getAttribute(){return attribute;}
	
	public void setAttribute(ArrayList<AdonisAttribute> list){attribute = list;}
	
	@Element(name="FROM", targetType=AdonisFrom.class)
	protected AdonisConnectionPoint from;
	
	@Element(name="TO", targetType=AdonisTo.class)
	protected AdonisConnectionPoint to;
	
	public void setFrom(AdonisConnectionPoint e){
		from = e;
	}
	
	public void setTo(AdonisConnectionPoint e){
		to = e;
	}
	public AdonisConnectionPoint getFrom(){
		return from;
	}
	public AdonisConnectionPoint getTo(){
		return to;
	}
	
	@Element(name="RECORD")
	protected ArrayList<AdonisRecord> record;


	public ArrayList<AdonisRecord> getRecord(){
		return record;
	}
	
	public void setRecord(ArrayList<AdonisRecord> list){
		record = list;
	}
	
	@Element(name="INTERREF", targetType=AdonisInterref.class)
	protected ArrayList<AdonisInterref> interref;


	public ArrayList<AdonisInterref> getInterref(){
		return interref;
	}
	
	public void setInterref(ArrayList<AdonisInterref> list){
		interref = list;
	}

	//*************************************************************************
	//* methods for computing purposes
	//**************************************************************************
	
	private static Map<String,String> evaluatedAttributes;
	static {
		evaluatedAttributes = new HashMap<String,String>();
		evaluatedAttributes.put("Position","STRING");
	}
	
	public String getName(){
		return getId();
	}
	
	@Override
	public Map<String, String> getEvaluatedAttributes() {
		return evaluatedAttributes;
	}
	
	public AdonisInstance getAsInstance(AdonisConnectionPoint target){
		for (AdonisInstance instance : getModel().getInstance()){
			if (instance.getName().equals(target.getInstance())){
				return instance;
			}
		}
		return null;
	}
	/**
	 * upper left x,y | lower right x,y
	 * @return
	 */
	public Double[] getBounds(){
		AdonisInstance source = getAsInstance(getFrom());
		AdonisInstance target = getAsInstance(getTo());
		
		Double[] boundingRect = new Double[4];
		if (source.getCenter()[0] < target.getCenter()[0]){
			boundingRect[0] = source.getCenter()[0];
			boundingRect[2] = target.getCenter()[0];
		} else {
			boundingRect[0] = target.getCenter()[0];
			boundingRect[2] = source.getCenter()[0];
		}
		if (source.getCenter()[1] < target.getCenter()[1]){
			boundingRect[1] = source.getCenter()[1];
			boundingRect[3] = target.getCenter()[1];
		} else {
			boundingRect[1] = target.getCenter()[1];
			boundingRect[3] = source.getCenter()[1];
		}
		return boundingRect;
	}
	
//	public boolean isConnector(){
//		return true;
//	}
	
	//*************************************************************************
	//* write methods for JSON
	//**************************************************************************
	
	@Override
	public void writeJSONchildShapes(JSONObject json) throws JSONException {
		getJSONArray(json,"childShapes");
		
	}

	@Override
	public void writeJSONproperties(JSONObject json) throws JSONException {
		JSONObject properties = getJSONObject(json,"properties");
//		properties.put("id",getId());
//		properties.put("class",getStencilClass());
		
//		TODO sort out
//		for (AdonisAttribute aAttribute : getAttribute()){
//			aAttribute.write(properties);
//		}
	}

	/**
	 * to make the import easier to handle, dockers are ignored
	 */
	@Override
	public void writeJSONdockers(JSONObject json) throws JSONException {
		JSONArray dockers = getJSONArray(json, "dockers");
		JSONObject temp = new JSONObject();

		Double[] bounds = getAsInstance(getFrom()).getBounds(); 
		temp.put("x",(bounds[2]-bounds[0])/2);
		temp.put("y",(bounds[3]-bounds[1])/2);
		dockers.put(temp);
		
		if (getStencilClass().equalsIgnoreCase("value flow")){
			temp = new JSONObject();
			bounds = getBounds();
			temp.put("x",(bounds[0]+bounds[2])/2);
			temp.put("y",(bounds[1]+bounds[3])/2);
			dockers.put(temp);
		}
		
		bounds = getAsInstance(getTo()).getBounds();
		temp = new JSONObject();
		temp.put("x",(bounds[2]-bounds[0])/2);
		temp.put("y",(bounds[3]-bounds[1])/2);
		dockers.put(temp);
	}

	@Override
	public void writeJSONbounds(JSONObject json) throws JSONException {
		//get the position and size which looks like
		//	EDGE index:5
		//	EDGE x:2.50cm y:7.00cm index:2 or
		//	NODE x:1cm y:11.5cm w:.5cm h:.6cm index:8
		Double[] boundingRect = getBounds();
		
		JSONObject bounds = getJSONObject(json,"bounds");
		
		JSONObject temp = getJSONObject(bounds,"upperLeft");
		temp.put("x",boundingRect[0]);
		temp.put("y",boundingRect[1]);
		
		temp = getJSONObject(bounds,"lowerRight");
		temp.put("x",boundingRect[2]);
		temp.put("y",boundingRect[3]);	
		
	}

	@Override
	public void writeJSONoutgoing(JSONObject json) throws JSONException {
		JSONArray outgoing = getJSONArray(json,"outgoing");
		JSONObject temp = null;
		AdonisInstance instance = getAsInstance(getTo());
		
		temp = new JSONObject();
		temp.putOpt("resourceId", instance.getResourceId());
		outgoing.put(temp);
	}

	@Override
	public void writeJSONtarget(JSONObject json) throws JSONException {
		JSONObject target = getJSONObject(json,"target");		
		target.putOpt("resourceId", getAsInstance(getTo()).getResourceId());
	}
	

}
