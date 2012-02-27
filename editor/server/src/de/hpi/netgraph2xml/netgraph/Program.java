package de.hpi.netgraph2xml.netgraph;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;
@RootElement
public class Program extends XMLConvertible{
    @Element
    String cpe_name;
    @Element("user")
    Collection<User> users;
    @Element("port_number")
    Collection<Integer> port_numbers;
    @Element("vulnerability")
    Collection<Vulnerability> vulnerabilities;
    public String getCpe_name() {
	return cpe_name;
    }
    public void setCpe_name(String cpe_name) {
	this.cpe_name = cpe_name;
    }
    public Collection<User> getUsers() {
	return users;
    }
    public void setUsers(Collection<User> users) {
	this.users = users;
    }
    public Collection<Integer> getPort_numbers() {
	return port_numbers;
    }
    public void setPort_numbers(Collection<Integer> port_numbers) {
	this.port_numbers = port_numbers;
    }
    public Collection<Vulnerability> getVulnerabilities() {
	return vulnerabilities;
    }
    public void setVulnerabilities(Collection<Vulnerability> vulnerabilities) {
	this.vulnerabilities = vulnerabilities;
    }

    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "program");

	modelElement.put("stencil", stencil);
    }

    public void writeJSONname(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("cpe_name", getCpe_name());
    }
    public void readJSONname(JSONObject modelElement) throws JSONException {
	setCpe_name(modelElement.optString("cpe_name"));
    }
    public void writeJSONport_numbers(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	if(getPort_numbers()!=null){
	    StringBuilder bld = new StringBuilder();
	    for(Integer i : getPort_numbers()){
		bld.append(",");
		bld.append(i);
	    }
	    bld.deleteCharAt(0);
	    modelElement.put("port_numbers", bld.toString());
	}
    }
    public void readJSONport_numbers(JSONObject modelElement) throws JSONException {
	String optString = modelElement.optString("port_numbers");
	if(optString!=null){
	    if(getPort_numbers()==null){
		setPort_numbers(new ArrayList<Integer>());
	    }
	    for(String s: optString.split(",")){
		try{
		    getPort_numbers().add(Integer.parseInt(s));
		}catch (Exception e) {
		    // nothing to do
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
    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
	XMLConvertibleUtils.writeChildren(modelElement, getVulnerabilities());
	XMLConvertibleUtils.writeChildren(modelElement, getUsers());

    }
    public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
	JSONArray childShapes = modelElement.optJSONArray("childShapes");

	if (childShapes != null) {
	    for (int i = 0; i<childShapes.length(); i++) {
		JSONObject childShape = childShapes.getJSONObject(i);
		String stencil = childShape.getJSONObject("stencil").getString("id");
		if("vulnerability".equals(stencil)){
		    if(getVulnerabilities()==null){
			setVulnerabilities(new ArrayList<Vulnerability>());
		    }

		    Vulnerability vulnerability = new Vulnerability();
		    vulnerability.setResourceIdToShape(getResourceIdToShape());

		    vulnerability.parse(childShape);
		    getVulnerabilities().add(vulnerability);

		}else if("user".equals(stencil)){
		    if(getUsers()==null){
			setUsers(new ArrayList<User>());
		    }

		    User user = new User();
		    user.setResourceIdToShape(getResourceIdToShape());

		    user.parse(childShape);
		    getUsers().add(user);

		}
	    }

	}
    }
    public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", XMLConvertibleUtils.generateResourceId());
    }
}
