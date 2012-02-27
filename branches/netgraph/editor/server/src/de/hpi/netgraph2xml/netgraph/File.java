package de.hpi.netgraph2xml.netgraph;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;

@RootElement
public class File extends XMLConvertible{
    @Element
    String sourcePath;
    @Element
    String destinationPath;
    @Element
    User owner;
    @Element
    String rights;
    public String getSourcePath() {
        return sourcePath;
    }
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    public String getDestinationPath() {
        return destinationPath;
    }
    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public String getRights() {
        return rights;
    }
    public void setRights(String rights) {
        this.rights = rights;
    }
    
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "file");

	modelElement.put("stencil", stencil);
    }
    public void writeJSONname(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("name", getSourcePath());
    }
    public void readJSONname(JSONObject modelElement) throws JSONException {
	setSourcePath(modelElement.optString("name"));
    }
    
    public void writeJSONowner(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("owner", getOwner().getLogin());
    }
    public void readJSONowner(JSONObject modelElement) throws JSONException {
	if(getOwner()==null){
	    setOwner(new User());
	}
	getOwner().setLogin(modelElement.optString("owner"));
    }
    
    public void writeJSONdestPath(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("destPath", getDestinationPath());
    }
    public void readJSONdestPath(JSONObject modelElement) throws JSONException {
	setDestinationPath(modelElement.optString("destPath"));
    }
    
    public void writeJSONrights(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
   	modelElement.put("rights", getRights());
    }
    public void readJSONrights(JSONObject modelElement) throws JSONException {
	setRights(modelElement.optString("rights"));
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
