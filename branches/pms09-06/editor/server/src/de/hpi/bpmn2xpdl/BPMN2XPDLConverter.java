package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class BPMN2XPDLConverter {
	protected XMLConvertable convertObject;

	public XMLConvertable getConvertObject() {
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
		String firstFiltering = xml.replace("<xpdl2:", "<");
		String secondFiltering = firstFiltering.replace("<xpdl:", "<");
		
		StringReader reader = new StringReader(secondFiltering);
		
		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		XPDLPackage newPackage = (XPDLPackage) xmappr.fromXML(reader);
		
		JSONObject importObject = new JSONObject();
		newPackage.write(importObject);
		
		return importObject.toString();
	}

	public void setConvertObject(XMLConvertable toConvert) {
		convertObject = toConvert;
	}
}
