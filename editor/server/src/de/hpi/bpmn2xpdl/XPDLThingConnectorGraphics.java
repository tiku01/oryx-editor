package de.hpi.bpmn2xpdl;

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
