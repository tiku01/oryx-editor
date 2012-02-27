package de.hpi.netgraph2xml.netgraph;
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
}
