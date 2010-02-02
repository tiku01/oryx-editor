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
		passInformationToFirstGraphics(modelElement, "bgcolor");
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
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		passInformationToFirstGraphics(modelElement, "graphicsinfounknowns");
	}
	
	public void readJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
		initializeGraphics();
		
		JSONObject passObject = new JSONObject();
		passObject.put("graphicsinfosunknowns", modelElement.optString("graphicsinfosunknowns"));
		getConnectorGraphics().parse(passObject);
	}
	
	public void setConnectorGraphics(XPDLConnectorGraphicsInfos graphics) {
		connectorGraphics = graphics;
	}
	
	public void writeJSONgraphicsinfos(JSONObject modelElement) {
		XPDLConnectorGraphicsInfos infos = getConnectorGraphics();
		if (infos != null) {
			infos.write(modelElement);
		}
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
	
	protected void passInformationToFirstGraphics(JSONObject modelElement, String key) throws JSONException {
		initializeGraphics();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getFirstGraphicsInfo().parse(passObject);
	}
}