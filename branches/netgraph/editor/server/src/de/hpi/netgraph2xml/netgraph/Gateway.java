package de.hpi.netgraph2xml.netgraph;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;
//TODO no representation in stencilset
@RootElement
public class Gateway extends XMLConvertible{
    @Element
    String destination;
    @Element
    String subnet;
    @Element
    ComponentLink component;
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getSubnet() {
        return subnet;
    }
    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }
    public ComponentLink getComponent() {
        return component;
    }
    public void setComponent(ComponentLink component) {
        this.component = component;
    }
    
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "gateway");

	modelElement.put("stencil", stencil);
    }
    
    public void writeJSONdestination(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("destination", getDestination());
    }
    public void readJSONdestination(JSONObject modelElement) throws JSONException {
	setDestination(modelElement.optString("destination"));
    }
    public void writeJSONsubnet(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("subnet", getSubnet());
    }
    public void readJSONsubnet(JSONObject modelElement) throws JSONException {
	setSubnet(modelElement.optString("subnet"));
    }
    public void readJSONproperties(JSONObject modelElement) throws JSONException {
	modelElement = modelElement.optJSONObject("properties");
	if(modelElement==null){
	    return;
	}
	parse(modelElement);
    }
    public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", XMLConvertibleUtils.generateResourceId());
    }
    public void readJSONoutgoing(JSONObject modelElement) throws JSONException {
	JSONArray array = modelElement.optJSONArray("outgoing");
	if(array.length()>0){
	    String resourceIdEdge = array.getJSONObject(0).getString("resourceId");
	    JSONObject edge = getResourceIdToShape().get(resourceIdEdge);
	    String resourceIdTarget = edge.getJSONObject("target").getString("resourceId");
	    JSONObject host = getResourceIdToShape().get(resourceIdTarget);
	    String hostId = XMLConvertibleUtils.switchToProperties(host).optString("id");
	    if(getComponent() == null){
		setComponent(new ComponentLink());
	    }

	    getComponent().setId(hostId);
	   
	    }
    }
    public void writeJSONoutgoing(JSONObject modelElement) throws JSONException {
	JSONArray outgoings = new JSONArray();
	JSONObject arcRef = new JSONObject();
	String rId = arcRef.hashCode() + "connection";
	arcRef.put("resourceId", rId);
	outgoings.put(arcRef);
	modelElement.put("outgoing", outgoings);
	
	JSONObject arc = new JSONObject();
	JSONObject stencil = new JSONObject();
	stencil.put("id", "connection");
	arc.put("stencil", stencil);
	arc.put("resourceId", rId);
	JSONObject host = new JSONObject();
	host.put("resourceId", ""+getComponent().getId().hashCode());
	arc.put("target", host);
	JSONArray arcOutgoings = new JSONArray();
	arcOutgoings.put(host);
	arc.put("outgoing", arcOutgoings);
	arc.put("childShapes", new JSONArray());
	if(modelElement.optJSONArray("childShapes")==null){
	    modelElement.put("childShapes", new JSONArray());
	}
	modelElement.getJSONArray("childShapes").put(arc);

	
    }
}
