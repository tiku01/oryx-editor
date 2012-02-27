package de.hpi.netgraph2xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.xmappr.Xmappr;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.bpmn2xpdl.XPDLPackage;
import de.hpi.netgraph2xml.netgraph.Scenario;

public class NetgraphXmlExporter {
    public static void main(String[] args) throws FileNotFoundException {
	Xmappr xmappr = new Xmappr(Scenario.class);
	Scenario fromXML = (Scenario) xmappr.fromXML(new FileReader("editor/server/src/de/hpi/netgraph2xml/Seclab.xml"));
	System.out.println(fromXML);
	JSONObject modelElement = new JSONObject();
	fromXML.write(modelElement );
	System.out.println(modelElement.toString());

    }
    
	protected XMLConvertible convertObject;

	public XMLConvertible getConvertObject() {
		return convertObject;
	}

	public String exportNetGraphXml(String json) throws JSONException {
		JSONObject model = new JSONObject(json);
		HashMap<String, JSONObject> mapping = new HashMap<String, JSONObject>();
		constructResourceIdShapeMapping(model, mapping);
		
		Scenario scenario = new Scenario();
		scenario.setResourceIdToShape(mapping);
		scenario.parse(model);
		
		StringWriter writer = new StringWriter();
		
		Xmappr xmappr = new Xmappr(Scenario.class);
		xmappr.setPrettyPrint(true);
		xmappr.toXML(scenario, writer);
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();
	}
	
	public String importNetGraphXml(String xml) {
		String parseXML = filterXMLString(xml);
		
		StringReader reader = new StringReader(parseXML);
		
		Xmappr xmappr = new Xmappr(Scenario.class);
		Scenario newPackage = (Scenario) xmappr.fromXML(reader);
		
		JSONObject importObject = new JSONObject();
		newPackage.write(importObject);
		
		return importObject.toString();
	}

	public void setConvertObject(XMLConvertible toConvert) {
		convertObject = toConvert;
	}
	
	private void constructResourceIdShapeMapping(JSONObject model, HashMap<String, JSONObject> mapping) {
		JSONArray childShapes = model.optJSONArray("childShapes");
		
		if (childShapes != null) {
			for (int i = 0; i < childShapes.length(); i++) {
				JSONObject childShape = childShapes.optJSONObject(i);
				if (childShape == null) {
					continue;
				}
				mapping.put(childShape.optString("resourceId"), childShape);
				constructResourceIdShapeMapping(childShape, mapping);
			}
		}
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
