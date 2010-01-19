package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLCategory extends XMLConvertable {
	
	protected String id;
	protected String name;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Category", XPDLCategory.class);
		
		xstream.useAttributeFor(XPDLCategory.class, "id");
		xstream.aliasField("Id", XPDLCategory.class, "id");
		xstream.useAttributeFor(XPDLCategory.class, "name");
		xstream.aliasField("Name", XPDLCategory.class, "name");
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-category");
	}
	
	public void readJSONname(JSONObject modelElement) {
		setName(modelElement.optString("name"));
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setName(String nameValue) {
		name = nameValue;
	}
}
