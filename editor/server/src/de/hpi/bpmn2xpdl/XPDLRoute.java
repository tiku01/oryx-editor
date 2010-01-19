package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLRoute extends XMLConvertable {

	protected String exclusiveType;
	protected String gatewayType;
	protected String incomingCondition;
	protected String instantiate;
	protected String markerVisible;
	protected String outgoingCondition;
	protected String xorType;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Route", XPDLRoute.class);
		
		xstream.useAttributeFor(XPDLRoute.class, "exclusiveType");
		xstream.aliasField("ExclusiveType", XPDLRoute.class, "exclusiveType");
		xstream.useAttributeFor(XPDLRoute.class, "gatewayType");
		xstream.aliasField("GatewayType", XPDLRoute.class, "gatewayType");
		xstream.useAttributeFor(XPDLRoute.class, "incomingCondition");
		xstream.aliasField("IncomingCondition", XPDLRoute.class, "incomingCondition");
		xstream.useAttributeFor(XPDLRoute.class, "instantiate");
		xstream.aliasField("Instantiate", XPDLRoute.class, "instantiate");
		xstream.useAttributeFor(XPDLRoute.class, "markerVisible");
		xstream.aliasField("MarkerVisible", XPDLRoute.class, "markervisible");
		xstream.useAttributeFor(XPDLRoute.class, "outgoingCondition");
		xstream.aliasField("OutgoingCondition", XPDLRoute.class, "outgoingCondition");
		xstream.useAttributeFor(XPDLRoute.class, "xorType");
		xstream.aliasField("XORType", XPDLRoute.class, "xorType");
	}
	
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
	
	public String getXORType() {
		return xorType;
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
	
	public void setXORType(String type) {
		xorType = type;
	}
}
