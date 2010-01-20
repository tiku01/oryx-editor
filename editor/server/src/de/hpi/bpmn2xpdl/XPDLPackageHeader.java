package de.hpi.bpmn2xpdl;

import java.util.HashMap;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLPackageHeader extends XMLConvertable {

	protected String created;
	protected String description;
	protected String documentation;
	protected String modificationDate;
	protected String vendor = "Hasso Plattner Institute";
	protected String xpdlVersion = "2.1";
	

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:PackageHeader", XPDLPackageHeader.class);
		
		xstream.aliasField("xpdl2:XPDLVersion", XPDLPackageHeader.class, "xpdlVersion");
		xstream.aliasField("xpdl2:Vendor", XPDLPackageHeader.class, "vendor");
		xstream.aliasField("xpdl2:Documentation", XPDLPackageHeader.class, "documentation");
		xstream.aliasField("xpdl2:Created", XPDLPackageHeader.class, "created");
		xstream.aliasField("xpdl2:ModificationDate", XPDLPackageHeader.class, "modificationDate");
		xstream.aliasField("xpdl2:Description", XPDLPackageHeader.class, "description");
	}
	
	public String getCreated() {
		return created;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public String getModificationDate() {
		return modificationDate;
	}
	
	public String getVendor() {
		return vendor;
	}
	
	public String getXPDLVersion() {
		return xpdlVersion;
	}
	
	public void readJSONcreationdate(JSONObject modelElement) {
		setCreated(formatDate(modelElement.optString("creationdate")));
	}
	
	public void readJSONdocumentation(JSONObject modelElement) {
		setDocumentation(modelElement.optString("documentation"));
	}
	
	public void readJSONmodificationdate(JSONObject modelElement) {
		setModificationDate(formatDate(modelElement.optString("modificationdate")));
	}
	
	public void setCreated(String date) {
		created = date;
	}
	
	public void setDescription(String descriptionValue) {
		description = descriptionValue;
	}
	
	public void setDocumentation(String documentationValue) {
		documentation = documentationValue;
	}
	
	public void setModificationDate(String date) {
		modificationDate = date;
	}

	public void setVendor(String vendorValue) {
		vendor = vendorValue;
	}
	
	public void setXPDLVersion(String version) {
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
