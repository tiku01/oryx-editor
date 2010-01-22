package de.hpi.bpmn2xpdl;

import java.util.HashMap;

import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("PackageHeader")
public class XPDLPackageHeader extends XMLConvertable {

	@Element("Created")
	protected XPDLCreated created;
	@Element("Documentation")
	protected XPDLDocumentation documentation;
	@Element("ModificationDate")
	protected XPDLModificationDate modificationDate;
	@Element("Vendor")
	protected XPDLVendor vendor = new XPDLVendor();
	@Element("XPDLVersion")
	protected XPDLXPDLVersion xpdlVersion = new XPDLXPDLVersion();
	
	public XPDLCreated getCreated() {
		return created;
	}
	
	public XPDLDocumentation getDocumentation() {
		return documentation;
	}
	
	public XPDLModificationDate getModificationDate() {
		return modificationDate;
	}
	
	public XPDLVendor getVendor() {
		return vendor;
	}
	
	public XPDLXPDLVersion getXpdlVersion() {
		return xpdlVersion;
	}
	
	public void readJSONcreationdate(JSONObject modelElement) {
		XPDLCreated newCreated = new XPDLCreated();
		newCreated.setContent(formatDate(modelElement.optString("creationdate")));
		
		setCreated(newCreated);
	}
	
	public void readJSONdocumentation(JSONObject modelElement) {
		XPDLDocumentation newDocumentation = new XPDLDocumentation();
		newDocumentation.setContent(modelElement.optString("documentation"));
		
		setDocumentation(newDocumentation);
	}
	
	public void readJSONmodificationdate(JSONObject modelElement) {
		XPDLModificationDate date = new XPDLModificationDate();
		date.setContent(formatDate(modelElement.optString("modificationdate")));
		
		setModificationDate(date);
	}
	
	public void setCreated(XPDLCreated date) {
		created = date;
	}
	
	public void setDocumentation(XPDLDocumentation documentationValue) {
		documentation = documentationValue;
	}
	
	public void setModificationDate(XPDLModificationDate date) {
		modificationDate = date;
	}

	public void setVendor(XPDLVendor vendorValue) {
		vendor = vendorValue;
	}
	
	public void setXpdlVersion(XPDLXPDLVersion version) {
		xpdlVersion = version;
	}
	
	protected String formatDate(String date) {
		if (date != null) {
			HashMap<String, String> months = new HashMap<String, String>();
				months.put("Jan", "01");
				months.put("Feb", "02");
				months.put("Mar", "03");
				months.put("Apr", "04");
				months.put("May", "05");
				months.put("Jun", "06");
				months.put("Jul", "07");
				months.put("Aug", "08");
				months.put("Sep", "09");
				months.put("Oct", "10");
				months.put("Nov", "11");
				months.put("Dec", "12");
			String[] dateElements = date.split(" ");
			return dateElements[3]+"-"+months.get(dateElements[1])+"-"+dateElements[2];
		} else {
			return null;
		}
	}
}
