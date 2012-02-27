package de.hpi.netgraph2xml.netgraph;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;
@RootElement
public class Scenario extends XMLConvertible{
    @Element("component")
    Collection<Component> components;
    @Element("network")
    Collection<Network> networks;
    @Attribute
    String name;
    @Attribute 
    String description;
    public Collection<Component> getComponents() {
	return components;
    }
    public void setComponents(Collection<Component> components) {
	this.components = components;
    }
    public Collection<Network> getNetworks() {
	return networks;
    }
    public void setNetworks(Collection<Network> networks) {
	this.networks = networks;
    }
    public String getName() {
	return name;
    }
    public void setName(String name) {
	this.name = name;
    }
    public String getDescription() {
	return description;
    }
    public void setDescription(String description) {
	this.description = description;
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
	XMLConvertibleUtils.writeChildren(modelElement, getComponents());
	XMLConvertibleUtils.writeChildren(modelElement, getNetworks());
    }
    public void writeJSONdocumentation (JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("description", getDescription());
    }
    public void writeJSONname(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("name", getName());
    }
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "Diagram");

	modelElement.put("stencil", stencil);
    }

    public void writeJSONstencilset(JSONObject modelElement) throws JSONException {
	JSONObject stencilset = new JSONObject();
	stencilset.put("url", org.oryxeditor.server.EditorHandler.oryx_path + "/stencilsets/networkGraph/networkGraph.json");
	stencilset.put("namespace", "http://b3mn.org/stencilset/networkGraph#");

	modelElement.put("stencilset", stencilset);
    }

    public void writeJSONssextensions(JSONObject modelElement) throws JSONException {
	modelElement.put("ssextensions", new JSONArray());
    }

    public void readJSONdescription (JSONObject modelElement) throws JSONException {
	setDescription(modelElement.optString("description"));
    }
    public void readJSONname (JSONObject modelElement) throws JSONException {
	setName(modelElement.optString("name"));
    }


    public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
	JSONArray childShapes = modelElement.optJSONArray("childShapes");

	if (childShapes != null) {
	    for (int i = 0; i<childShapes.length(); i++) {
		JSONObject childShape = childShapes.getJSONObject(i);
		String stencil = childShape.getJSONObject("stencil").getString("id");
		if("switch".equals(stencil)){
		    Network n = new Network();
		    n.setResourceIdToShape(getResourceIdToShape());
		    if(getNetworks()==null){
			setNetworks(new ArrayList<Network>());
		    }
		    n.parse(childShape);
		    getNetworks().add(n);
		}else if("Host".equals(stencil)){
		    Component n = new Component();
		    n.setResourceIdToShape(getResourceIdToShape());
		    if(getComponents()==null){
			setComponents(new ArrayList<Component>());
		    }
		    n.parse(childShape);
		    getComponents().add(n);
		}
	    }
	}
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
