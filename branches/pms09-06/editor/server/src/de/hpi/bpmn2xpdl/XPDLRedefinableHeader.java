package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("RedefinableHeader")
public class XPDLRedefinableHeader extends XMLConvertable {

	@Element("Author")
	protected XPDLAuthor author;
	@Element("Version")
	protected XPDLVersion version;
	
	public XPDLAuthor getAuthor() {
		return author;
	}
	
	public XPDLVersion getVersion() {
		return version;
	}
	
	public void readJSONauthor(JSONObject modelElement) {
		XPDLAuthor authorObject = new XPDLAuthor();
		authorObject.setContent(modelElement.optString("author"));
		
		setAuthor(authorObject);
	}
	
	public void readJSONversion(JSONObject modelElement) {
		XPDLVersion versionObject = new XPDLVersion();
		versionObject.setContent(modelElement.optString("version"));
		
		setVersion(versionObject);
	}
	
	public void setAuthor(XPDLAuthor authorValue) {
		author = authorValue;
	}
	 
	public void setVersion(XPDLVersion versionValue) {
		version = versionValue;
	}
}