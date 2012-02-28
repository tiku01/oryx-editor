package de.hpi.netgraph2xml.netgraph;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.*;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;

@RootElement
public class Component extends XMLConvertible{
    @Attribute
    String id;
    @Element
    String desc;
    @Element
    Image image;
    @Element
    Hardware hardware;
    @Element("Interface")
    Collection<Interface> interfaces;

    public String getDesc() {
	return desc;
    }
    public Hardware getHardware() {
	return hardware;
    }
    public String getId() {
	return id;
    }
    public Image getImage() {
	return image;
    }
    public Collection<Interface> getInterfaces() {
	return interfaces;
    }
    public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
	JSONArray childShapes = modelElement.optJSONArray("childShapes");

	if (childShapes != null) {
	    for (int i = 0; i<childShapes.length(); i++) {
		JSONObject childShape = childShapes.getJSONObject(i);
		String stencil = childShape.getJSONObject("stencil").getString("id");
		if("program".equals(stencil)){
		    if(getImage()==null){
			setImage(new Image());
		    }
		    if(getImage().getPrograms()==null){
			getImage().setPrograms(new ArrayList<Program>());
		    }

		    Program program = new Program();
		    program.setResourceIdToShape(getResourceIdToShape());

		    program.parse(childShape);
		    getImage().getPrograms().add(program);

		}else if("user".equals(stencil)){
		    if(getImage()==null){
			setImage(new Image());
		    }
		    if(getImage().getUsers()==null){
			getImage().setUsers(new ArrayList<User>());
		    }

		    User user = new User();
		    user.setResourceIdToShape(getResourceIdToShape());
		    user.parse(childShape);
		    getImage().getUsers().add(user);
		}else if("file".equals(stencil)){
		    if(getImage()==null){
			setImage(new Image());
		    }
		    if(getImage().getFiles()==null){
			getImage().setFiles(new ArrayList<File>());
		    }

		    File file = new File();
		    file.setResourceIdToShape(getResourceIdToShape());
		    file.parse(childShape);
		    getImage().getFiles().add(file);
		}else if("interface".equals(stencil)){
		    if(getInterfaces()==null){
			setInterfaces(new ArrayList<Interface>());
		    }
		    Interface inter = new Interface();
		    inter.setResourceIdToShape(getResourceIdToShape());
		    inter.parse(childShape);
		    getInterfaces().add(inter);
		}else if("forwarding".equals(stencil)){
		    if(getImage()==null){
			setImage(new Image());
		    }
		    if(getImage().getForwardings()==null){
			getImage().setForwardings(new ArrayList<Forwarding>());
		    }

		    Forwarding forwarding = new Forwarding();
		    forwarding.setResourceIdToShape(getResourceIdToShape());
		    forwarding.parse(childShape);
		    getImage().getForwardings().add(forwarding);
		}else if("vulnerability".equals(stencil)){
		    if(getImage()==null){
			setImage(new Image());
		    }
		    if(getImage().getOs()==null){
			getImage().setOs(new OS());
		    }
		    if(getImage().getOs().getVulnerabilties()==null){
			getImage().getOs().setVulnerabilties(new ArrayList<Vulnerability>());
		    }

		    Vulnerability vulnerability = new Vulnerability();
		    vulnerability.setResourceIdToShape(getResourceIdToShape());
		    vulnerability.parse(childShape);
		    getImage().getOs().getVulnerabilties().add(vulnerability);
		}
	    }
	}
    }
    public void readJSONcpu_cores(JSONObject modelElement) throws JSONException {
	if(getHardware()==null){
	    setHardware(new Hardware());
	}
	getHardware().setCpu_cores(modelElement.optInt("cpu_cores"));
    }
    public void readJSONdesc(JSONObject modelElement) throws JSONException {
	setDesc(modelElement.optString("desc"));
    }
    public void readJSONid(JSONObject modelElement) throws JSONException {
	setId(modelElement.optString("id"));
    }
    public void readJSONproperties(JSONObject modelElement) throws JSONException {
	modelElement = modelElement.optJSONObject("properties");
	if(modelElement==null){
	    return;
	}
	parse(modelElement);
    }
    public void readJSONos(JSONObject modelElement) throws JSONException {
	if(getImage()==null){
	    setImage(new Image());
	}
	if(getImage().getOs()==null){
	    getImage().setOs(new OS());
	}

	getImage().getOs().setCpe_name(modelElement.optString("os"));
    }

    public void readJSONram(JSONObject modelElement) throws JSONException {
	if(getHardware()==null){
	    setHardware(new Hardware());
	}
	getHardware().setRam(modelElement.optInt("ram"));
    }

    public void setDesc(String desc) {
	this.desc = desc;
    }
    public void setHardware(Hardware hardware) {
	this.hardware = hardware;
    }
    public void setId(String id) {
	this.id = id;
    }
    public void setImage(Image image) {
	this.image = image;
    }
    public void setInterfaces(Collection<Interface> interfaces) {
	this.interfaces = interfaces;
    }
    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
	if(getImage()!=null){
	    XMLConvertibleUtils.writeChildren(modelElement, getImage().getPrograms());
	    XMLConvertibleUtils.writeChildren(modelElement, getImage().getUsers());
	    XMLConvertibleUtils.writeChildren(modelElement, getImage().getFiles());
	    XMLConvertibleUtils.writeChildren(modelElement, getImage().getForwardings());
	    if(getImage().getOs()!=null){
		XMLConvertibleUtils.writeChildren(modelElement, getImage().getOs().getVulnerabilties());
	    }
	}
	XMLConvertibleUtils.writeChildren(modelElement, getInterfaces());

    }
    public void writeJSONcpu_cores(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("cpu_cores", getHardware().getCpu_cores());
    }
    public void writeJSONdesc(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("desc", getDesc());
    }
    public void writeJSONid(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", ""+getId().hashCode());

	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("id", getId());
    }
    public void writeJSONos(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("os", getImage().getOs().getCpe_name());
    }

    public void writeJSONram(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("ram", getHardware().getRam());
    }
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "Host");

	modelElement.put("stencil", stencil);
    }
    public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", XMLConvertibleUtils.generateResourceId());
    }



}
