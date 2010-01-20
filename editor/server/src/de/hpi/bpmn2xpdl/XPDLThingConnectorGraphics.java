package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public abstract class XPDLThingConnectorGraphics extends XPDLThing {

	protected ArrayList<XPDLConnectorGraphicsInfo> connectorGraphics;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("Thing", XPDLThingConnectorGraphics.class);
		
		xstream.aliasField("xpdl2:ConnectorGraphicsInfos", XPDLThingConnectorGraphics.class, "connectorGraphics");
	}
	
	public ArrayList<XPDLConnectorGraphicsInfo> getConnectorGraphicsInfos() {
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
	
	public void setConnectorGraphicsInfos(ArrayList<XPDLConnectorGraphicsInfo> graphics) {
		connectorGraphics = graphics;
	}
	
	protected XPDLConnectorGraphicsInfo getFirstGraphicsInfo() {
		return getConnectorGraphicsInfos().get(0);
	}
	
	protected void initializeGraphics() {
		if (getConnectorGraphicsInfos() == null) {
			setConnectorGraphicsInfos(new ArrayList<XPDLConnectorGraphicsInfo>());
			getConnectorGraphicsInfos().add(new XPDLConnectorGraphicsInfo());
		}
	}
}
