package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLRedefinableHeader extends XMLConvertable {

	protected String author;
	protected String version;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:RedefinableHeader", XPDLRedefinableHeader.class);
		
		xstream.aliasField("xpdl2:Author", XPDLRedefinableHeader.class, "author");
		xstream.aliasField("xpdl2:Version", XPDLRedefinableHeader.class, "version");
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void readJSONauthor(JSONObject modelElement) {
		setAuthor(modelElement.optString("author"));
	}
	
	public void readJSONversion(JSONObject modelElement) {
		setVersion(modelElement.optString("version"));
	}
	
	public void setAuthor(String authorValue) {
		author = authorValue;
	}
	 
	public void setVersion(String versionValue) {
		version = versionValue;
	}
}