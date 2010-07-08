package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

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
	
	@Element(name="FROM", targetType=AdonisConnectionPoint.class)
	protected AdonisConnectionPoint from;
	
	@Element(name="TO", targetType=AdonisConnectionPoint.class)
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
		properties.put("id",getId());
		properties.put("class",getStencilClass());
		
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
		getJSONArray(json, "dockers");
		
	}

	@Override
	public void writeJSONbounds(JSONObject json) throws JSONException {
		//get the position and size which looks like 
		//	NODE x:2.50cm y:7.00cm index:2 or
		//	NODE x:1cm y:11.5cm w:.5cm h:.6cm index:8
		AdonisInstance source = null;
		AdonisInstance target = null;
		
		for (AdonisInstance aInstance : getModel().getInstance()){
			if (aInstance.getName().equals(getFrom().getInstance())){
				source = aInstance;
			}
			if (aInstance.getName().equals(getTo().getInstance())){
				target = aInstance;
			}
			if (source != null && target != null){
				break;
			}
		}
		Double[] boundingRect = new Double[4];
		if (source.getCenter()[0] > target.getCenter()[0]){
			boundingRect[0] = source.getCenter()[0];
			boundingRect[2] = target.getCenter()[0];
		} else {
			boundingRect[0] = target.getCenter()[0];
			boundingRect[2] = source.getCenter()[0];
		}
		if (source.getCenter()[1] > target.getCenter()[1]){
			boundingRect[1] = source.getCenter()[1];
			boundingRect[3] = target.getCenter()[1];
		} else {
			boundingRect[1] = target.getCenter()[1];
			boundingRect[3] = source.getCenter()[1];
		}
		JSONObject bounds = getJSONObject(json,"bounds");
		
		JSONObject temp = getJSONObject(bounds,"upperLeft");
		temp.put("x",boundingRect[0]);
		temp.put("y",boundingRect[1]);
		
		temp = getJSONObject(bounds,"lowerRight");
		temp.put("x",boundingRect[0]);
		temp.put("y",boundingRect[1]);	
		
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

	@Override
	public void writeJSONtarget(JSONObject json) throws JSONException {
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
	

}
