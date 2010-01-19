package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLMessage extends XMLConvertable {
	
	protected String faultName;
	protected String from;
	protected String id;
	protected String name;
	protected String to;
	
	protected ArrayList<XPDLActualParameter> actualParameters;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Message", XPDLMessage.class);
		
		xstream.useAttributeFor(XPDLMessage.class, "faultName");
		xstream.aliasField("FaultName", XPDLMessage.class, "faultName");
		xstream.useAttributeFor(XPDLMessage.class, "from");
		xstream.aliasField("From", XPDLMessage.class, "from");
		xstream.useAttributeFor(XPDLMessage.class, "id");
		xstream.aliasField("Id", XPDLMessage.class, "id");
		xstream.useAttributeFor(XPDLMessage.class, "name");
		xstream.aliasField("Name", XPDLMessage.class, "name");
		xstream.useAttributeFor(XPDLMessage.class, "to");
		xstream.aliasField("To", XPDLMessage.class, "to");
		
		xstream.aliasField("xpdl2:ActualParameters", XPDLMessage.class, "actualParameters");
	}
	
	public ArrayList<XPDLActualParameter> getActualParameters() {
		return actualParameters;
	}
	
	public String getFaultName() {
		return faultName;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTo() {
		return to;
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-message");
	}
	
	public void setActualParameters(ArrayList<XPDLActualParameter> parameterList) {
		actualParameters = parameterList;
	}
	
	public void setFaultName(String fault) {
		faultName = fault;
	}
	
	public void setFrom(String source) {
		from = source;
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setName(String nameValue) {
		name = nameValue;
	}
	
	public void setTo(String destination) {
		to = destination;
	}
}
