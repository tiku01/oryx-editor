package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLRedefinableHeader extends XMLConvertable {

	protected String author;
	protected String codepage;
	protected String countrykey;
	protected String publicationStatus;
	protected String version;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:RedefinableHeader", XPDLRedefinableHeader.class);
		
		xstream.useAttributeFor(XPDLRedefinableHeader.class, "publicationStatus");
		xstream.aliasField("PublicationStatus", XPDLRedefinableHeader.class, "publicationStatus");
		
		xstream.aliasField("xpdl2:Author", XPDLRedefinableHeader.class, "author");
		xstream.aliasField("xpdl2:Codepage", XPDLRedefinableHeader.class, "codepage");
		xstream.aliasField("xpdl2:Countrykey", XPDLRedefinableHeader.class, "countrykey");
		xstream.aliasField("xpdl2:Version", XPDLRedefinableHeader.class, "version");
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getCodepage() {
		return codepage;
	}
	
	public String getCountrykey() {
		return countrykey;
	}
	
	public String getPublicationStatus() {
		return publicationStatus;
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
	
	public void setCodepage(String page) {
		codepage = page;
	}
	
	public void setCountrykey(String key) {
		countrykey = key;
	}
	
	public void setPublicationStatus(String status) {
		publicationStatus = status;
	}
	 
	public void setVersion(String versionValue) {
		version = versionValue;
	}
}