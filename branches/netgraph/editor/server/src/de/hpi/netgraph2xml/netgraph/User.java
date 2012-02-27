package de.hpi.netgraph2xml.netgraph;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;

public class User extends XMLConvertible{
    @Element
    String login;
    @Element
    String password;
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "user");

	modelElement.put("stencil", stencil);
    }
    
    public void writeJSONusername(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("username", getLogin());
    }
    public void readJSONusername(JSONObject modelElement) throws JSONException {
	setLogin(modelElement.optString("username"));
    }
    public void writeJSONpassword(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("password", getPassword());
    }
    public void readJSONpassword(JSONObject modelElement) throws JSONException {
	setPassword(modelElement.optString("password"));
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
