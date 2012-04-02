package de.hpi.netgraph2xml.netgraph;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;

@RootElement
public class Interface extends XMLConvertible{

    @Element
    String name;
    @Element
    String mac;
    @Element
    String ip;
    @Element
    String subnet;
    @Element
    Connection connection;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getSubnet() {
        return subnet;
    }
    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "interface");

	modelElement.put("stencil", stencil);
    }
    
    public void writeJSONname(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("name", getName());
    }
    
    public void readJSONname(JSONObject modelElement) throws JSONException {
	setName(modelElement.optString("name"));
    }
    public void writeJSONip(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("ip", getIp());
    }
    public void readJSONip(JSONObject modelElement) throws JSONException {
	setIp(modelElement.optString("ip"));
    }
    public void writeJSONmac(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("mac", getMac());
    }
    public void readJSONmac(JSONObject modelElement) throws JSONException {
	setMac(modelElement.optString("mac"));
    }
    public void readJSONoutgoing(JSONObject modelElement) throws JSONException {
	JSONArray array = modelElement.optJSONArray("outgoing");
	if(array.length()>0){
	    String resourceIdEdge = array.getJSONObject(0).getString("resourceId");
	    JSONObject edge = getResourceIdToShape().get(resourceIdEdge);
	    String resourceIdTarget = edge.getJSONObject("target").getString("resourceId");
	    JSONObject network = getResourceIdToShape().get(resourceIdTarget);
	    String networkId = XMLConvertibleUtils.switchToProperties(network).optString("id");
	    if(getConnection()==null){
		setConnection(new Connection());
	    }
	    if(getConnection().getNetwork()==null){
		getConnection().setNetwork(new NetworkLink());
	    }
	    getConnection().getNetwork().setId(networkId);
	   
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
	JSONObject network = new JSONObject();
	network.put("resourceId", ""+getConnection().getNetwork().getId().hashCode());
	arc.put("target", network);
	JSONArray arcOutgoings = new JSONArray();
	arcOutgoings.put(network);
	arc.put("outgoing", arcOutgoings);
	arc.put("childShapes", new JSONArray());
	if(modelElement.optJSONArray("childShapes")==null){
	    modelElement.put("childShapes", new JSONArray());
	}
	modelElement.getJSONArray("childShapes").put(arc);

	
    }
    public void readJSONproperties(JSONObject modelElement) throws JSONException {
	modelElement = modelElement.optJSONObject("properties");
	if(modelElement==null){
	    return;
	}
	parse(modelElement);
    }
    
    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
	if(getConnection()!=null){
		if(getConnection().getRules()!=null){
			XMLConvertibleUtils.writeChildren(modelElement, getConnection().getRules().getRules());
		}
		if(getConnection().getGateways()!=null){
			XMLConvertibleUtils.writeChildren(modelElement, getConnection().getGateways());
		}
	}
    }
    public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
	JSONArray childShapes = modelElement.optJSONArray("childShapes");

	if (childShapes != null) {
	    for (int i = 0; i<childShapes.length(); i++) {
		JSONObject childShape = childShapes.getJSONObject(i);
		String stencil = childShape.getJSONObject("stencil").getString("id");
		if("gateway".equals(stencil)){
		    Gateway n = new Gateway();
		    n.setResourceIdToShape(getResourceIdToShape());
		    if(getConnection()==null){
			setConnection(new Connection());
		    }
		    if(getConnection().getGateways()==null){
			getConnection().setGateways(new ArrayList<Gateway>());
		    }
		    n.parse(childShape);
		    getConnection().getGateways().add(n);
		}else if("rule".equals(stencil)){
		    Rule n = new Rule();
		    n.setResourceIdToShape(getResourceIdToShape());
		    if(getConnection()==null){
			setConnection(new Connection());
		    }
		    if(getConnection().getRules()==null){
			getConnection().setRules(new Rules());
		    }
		    n.parse(childShape);
		    getConnection().getRules().getRules().add(n);
		}
	    }
	}
    }
    public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", XMLConvertibleUtils.generateResourceId());
    }
}

