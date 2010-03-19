package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class BPMN2XPDLConverter {
	protected XMLConvertible convertObject;

	public XMLConvertible getConvertObject() {
		return convertObject;
	}

	public String exportXPDL(String json) throws JSONException {		
		XPDLPackage newPackage = new XPDLPackage();
		newPackage.parse(new JSONObject(json));
		
		StringWriter writer = new StringWriter();
		
		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		xmappr.setPrettyPrint(true);
		xmappr.toXML(newPackage, writer);
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();
	}
	
	public String importXPDL(String xml) {
		String parseXML = filterXMLString(xml);
		
		StringReader reader = new StringReader(parseXML);
		
		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		XPDLPackage newPackage = (XPDLPackage) xmappr.fromXML(reader);
		
		JSONObject importObject = new JSONObject();
		newPackage.write(importObject);
		
		return importObject.toString();
	}

	public void setConvertObject(XMLConvertible toConvert) {
		convertObject = toConvert;
	}
	
	private String filterXMLString(String xml) {
		//Remove xpdl2: from tags
		String firstTagFiltered = xml.replace("<xpdl2:", "<");
		firstTagFiltered = firstTagFiltered.replace("</xpdl2:", "</");
		
		//Remove xpdl: from tags
		String secondTagFiltered = firstTagFiltered.replace("<xpdl:", "<");
		secondTagFiltered = secondTagFiltered.replace("</xpdl:", "</");
		
		//Remove namespaces
		String nameSpaceFiltered = secondTagFiltered.replaceAll(" xmlns=\"[^\"]*\"", "");
		//Remove xml namespace lookalikes
		nameSpaceFiltered = nameSpaceFiltered.replaceAll(" \\w+:\\w+=\"[^\"]*\"", "");
		//Remove schemas
		String schemaFiltered = nameSpaceFiltered.replaceAll(" xsi=\"[^\"]*\"", "");
		//Remove starting xml tag
		String xmlTagFiltered = schemaFiltered.replaceAll("<\\?xml[^\\?]*\\?>\n?", "");
		return xmlTagFiltered;
	}
}
