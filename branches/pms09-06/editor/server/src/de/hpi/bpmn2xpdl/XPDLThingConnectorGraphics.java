package de.hpi.bpmn2xpdl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ConnectorThing")
public abstract class XPDLThingConnectorGraphics extends XPDLThing {

	@Element("ConnectorGraphicsInfos")
	protected XPDLConnectorGraphicsInfos connectorGraphics;
	
	public XPDLConnectorGraphicsInfos getConnectorGraphics() {
		return connectorGraphics;
	}
	
	public void readJSONbgcolor(JSONObject modelElement) throws JSONException {
			initializeGraphics();
			
			JSONObject color = new JSONObject();
			color.put("bgcolor", modelElement.optString("bgcolor"));
			getFirstGraphicsInfo().parse(color);
	}
	
	public void readJSONbounds(JSONObject modelElement) throws JSONException {
		initializeGraphics();
	
		JSONObject bounds = new JSONObject();
		bounds.put("bounds", modelElement.optJSONObject("bounds"));
		getFirstGraphicsInfo().parse(bounds);
	}
	
	public void readJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		
		if (dockers != null) {
			if (dockers.length() >= 2) {
				JSONObject firstDocker = dockers.optJSONObject(0);
				createExtendedAttribute("docker" + 0 + "X", firstDocker.optString("x"));
				createExtendedAttribute("docker" + 0 + "Y", firstDocker.optString("y"));
				
				JSONObject lastDocker = dockers.optJSONObject(dockers.length()-1);
				createExtendedAttribute("docker" + String.valueOf(dockers.length()-1) + "X", lastDocker.optString("x"));
				createExtendedAttribute("docker" + String.valueOf(dockers.length()-1) + "Y", lastDocker.optString("y"));
			}
			
			JSONArray parseDockers = new JSONArray();
			for (int i = 1; i < dockers.length()-1; i++) {
				parseDockers.put(dockers.optJSONObject(i));				
			}
			
			if (parseDockers.length() > 0) {
				initializeGraphics();
				JSONObject passObject = new JSONObject();
				passObject.put("dockers", parseDockers);
				getFirstGraphicsInfo().parse(passObject);
			}
		}
	}
	
	public void setConnectorGraphics(XPDLConnectorGraphicsInfos graphics) {
		connectorGraphics = graphics;
	}
	
	protected XPDLConnectorGraphicsInfo getFirstGraphicsInfo() {
		return getConnectorGraphics().get(0);
	}
	
	protected void initializeGraphics() {
		if (getConnectorGraphics() == null) {
			setConnectorGraphics(new XPDLConnectorGraphicsInfos());
			getConnectorGraphics().add(new XPDLConnectorGraphicsInfo());
		}
	}
}
