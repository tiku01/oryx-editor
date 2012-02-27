package de.hpi.netgraph2xml.netgraph;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;
//TODO no representation in stencilset
@RootElement
public class Forwarding extends XMLConvertible{
    @Element
    String from;
    @Element
    String to;
    @Element
    String input;
    @Element
    String output;
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getInput() {
        return input;
    }
    public void setInput(String input) {
        this.input = input;
    }
    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output = output;
    }
    
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "forwarding");

	modelElement.put("stencil", stencil);
    }
    
    public void writeJSONfrom(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("from", getFrom());
    }
    public void readJSONfrom(JSONObject modelElement) throws JSONException {
	setFrom(modelElement.optString("from"));
    }
    public void writeJSONto(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("to", getTo());
    }
    public void readJSONto(JSONObject modelElement) throws JSONException {
	setTo(modelElement.optString("to"));
    }
    public void writeJSONinput(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("input", getInput());
    }
    public void readJSONinput(JSONObject modelElement) throws JSONException {
	setInput(modelElement.optString("input"));
    }
    public void writeJSONoutput(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("output", getOutput());
    }
    public void readJSONoutput(JSONObject modelElement) throws JSONException {
	setOutput(modelElement.optString("output"));
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
