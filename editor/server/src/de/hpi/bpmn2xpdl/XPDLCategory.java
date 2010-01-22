package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Category")
public class XPDLCategory extends XMLConvertable {
	
	@Attribute("Id")
	protected String id;
	@Text
	protected String content;
	
	public String getId() {
		return id;
	}
	
	public String getContent() {
		return content;
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-category");
	}
	
	public void readJSONcontent(JSONObject modelElement) {
		setContent(modelElement.optString("content"));
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setContent(String contentValue) {
		content = contentValue;
	}
}
