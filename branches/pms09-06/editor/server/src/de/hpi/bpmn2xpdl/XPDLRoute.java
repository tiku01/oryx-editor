package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Route")
public class XPDLRoute extends XMLConvertable {

	@Attribute("ExclusiveType")
	protected String exclusiveType;
	@Attribute("GatewayType")
	protected String gatewayType;
	@Attribute("IncomingCondition")
	protected String incomingCondition;
	@Attribute("Instantiate")
	protected String instantiate;
	@Attribute("MarkerVisible")
	protected String markerVisible;
	@Attribute("OutgoingCondition")
	protected String outgoingCondition;
	
	public String getExclusiveType() {
		return exclusiveType;
	}
	
	public String getGatewayType() {
		return gatewayType;
	}
	
	public String getIncomingCondition() {
		return incomingCondition;
	}
	
	public String getInstantiate() {
		return instantiate;
	}
	
	public String getMarkerVisible() {
		return markerVisible;
	}
	
	public String getOutgoingCondition() {
		return outgoingCondition;
	}
	
	public void readJSONgatewaytype(JSONObject modelElement) {
		setGatewayType(modelElement.optString("gatewaytype"));
	}
	
	public void readJSONincomingcondition(JSONObject modelElement) {
		setIncomingCondition(modelElement.optString("incomingcondition"));
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
		setInstantiate(modelElement.optString("instantiate"));
	}
	
	public void readJSONmarkervisible(JSONObject modelElement) {
		setMarkerVisible(modelElement.optString("markervisible"));
	}
	
	public void readJSONoutgoingcondition(JSONObject modelElement) {
		setOutgoingCondition(modelElement.optString("outgoingcondition"));
	}
	
	public void readJSONxortype(JSONObject modelElement) {
		setExclusiveType(modelElement.optString("xortype"));
	}
	
	public void setExclusiveType(String type) {
		exclusiveType = type;
	}
	
	public void setGatewayType(String type) {
		gatewayType = type;
	}
	
	public void setIncomingCondition(String condition) {
		incomingCondition = condition;
	}
	
	public void setInstantiate(String instantiateValue) {
		instantiate = instantiateValue;
	}
	
	public void setMarkerVisible(String isVisible) {
		markerVisible = isVisible;
	}
	
	public void setOutgoingCondition(String condition) {
		outgoingCondition = condition;
	}
}
