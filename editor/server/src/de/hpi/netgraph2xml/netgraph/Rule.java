package de.hpi.netgraph2xml.netgraph;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;

@RootElement
public class Rule extends XMLConvertible{
    @Element
    String source_address;
    @Element
    Integer destination_port;
    @Element
    String state;
    public String getSource_address() {
        return source_address;
    }
    public void setSource_address(String source_address) {
        this.source_address = source_address;
    }
    public Integer getDestination_port() {
        return destination_port;
    }
    public void setDestination_port(Integer destination_pot) {
        this.destination_port = destination_pot;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "rule");

	modelElement.put("stencil", stencil);
    }
    
    public void writeJSONsource_address(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("source_address", getSource_address());
    }
    public void readJSONsource_address(JSONObject modelElement) throws JSONException {
	setSource_address(modelElement.optString("source_address"));
    }
    public void writeJSONdestination_port(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("destination_port", getDestination_port());
    }
    public void readJSONdestination_port(JSONObject modelElement) throws JSONException {
	setDestination_port(modelElement.optInt("destination_port"));
    }
    public void writeJSONstate(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("state", getState());
    }
    public void readJSONstate(JSONObject modelElement) throws JSONException {
	setState(modelElement.optString("state"));
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
