package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Message")
public class XPDLMessage extends XMLConvertable {
	
	@Attribute("FaultName")
	protected String faultName;
	@Attribute("From")
	protected String from;
	@Attribute("Id")
	protected String id;
	@Attribute("Name")
	protected String name;
	@Attribute("To")
	protected String to;
	
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
