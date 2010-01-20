package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLArtifact extends XPDLThingNodeGraphics {
	
	protected String artifactType;
	protected String groupDeprecated;
	protected String textAnnotation;
	
	protected XPDLDataObject dataObject;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"DataObject",
				"TextAnnotation",
				"Group"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Artifact", XPDLArtifact.class);
		
		xstream.useAttributeFor(XPDLArtifact.class, "artifactType");
		xstream.aliasField("ArtifactType", XPDLArtifact.class, "artifactType");
		xstream.useAttributeFor(XPDLArtifact.class, "groupDeprecated");
		xstream.aliasField("Group", XPDLArtifact.class, "groupDeprecated");
		xstream.useAttributeFor(XPDLArtifact.class, "textAnnotation");
		xstream.aliasField("TextAnnotation", XPDLArtifact.class, "textAnnotation");
		
		xstream.aliasField("xpdl2:DataObject", XPDLArtifact.class, "dataObject");
	}
	
	public String getArtifactType() {
		return artifactType;
	}
	
	public XPDLDataObject getDataObject() {
		return dataObject;
	}
	
	public String getGroupDeprecated() {
		return groupDeprecated;
	}
	
	public String getTextAnnotation() {
		return textAnnotation;
	}
	
	public void readJSONartifacttype(JSONObject modelElement) {
		setArtifactType(modelElement.optString("artifacttype"));
	}
	
	public void readJSONitems(JSONObject modelElement) {
	}
	
	public void readJSONname(JSONObject modelElement) {
		if (modelElement.optString("artifacttype").equals("DataObject")) {
			initializeDataObject();
			getDataObject().setName(modelElement.optString("name"));
		} else {
			super.readJSONname(modelElement);
		}
	}
	
	public void readJSONproducedatcompletion(JSONObject modelElement) {
		handleDataObject(modelElement);
	}
	
	public void readJSONrequiredforstart(JSONObject modelElement) {
		handleDataObject(modelElement);
	}
	
	public void readJSONstate(JSONObject modelElement) {
		handleDataObject(modelElement);
	}
	
	public void readJSONtext(JSONObject modelElement) {
		setTextAnnotation(modelElement.optString("text"));
	}
	
	public void readJSONtotalCount(JSONObject modelElement) {
	}
	
	public void setArtifactType(String type) {
		artifactType = type;
	}
	
	public void setDataObject(XPDLDataObject dataObjectValue) {
		dataObject = dataObjectValue;
	}
	
	public void setGroupDeprecated(String groupValue) {
		groupDeprecated = groupValue;
	}
	
	public void setTextAnnotation(String annotation) {
		textAnnotation = annotation;
	}
	
	protected void initializeDataObject() {
		if (getDataObject() == null) {
			setDataObject(new XPDLDataObject());
		}
	}
	
	protected void handleDataObject(JSONObject modelElement) {
		initializeDataObject();
		getDataObject().parse(modelElement);
	}
}
