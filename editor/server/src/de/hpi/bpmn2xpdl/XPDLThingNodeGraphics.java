package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("NodeThing")
public abstract class XPDLThingNodeGraphics extends XPDLThing {

	@Element("NodeGraphicsInfos")
	protected XPDLNodeGraphicsInfos nodeGraphics;

	public XPDLNodeGraphicsInfos getNodeGraphics() {
		return nodeGraphics;
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

	public void setNodeGraphics(XPDLNodeGraphicsInfos graphics) {
		nodeGraphics = graphics;
	}
	
	protected XPDLNodeGraphicsInfo getFirstGraphicsInfo() {
		return getNodeGraphics().get(0);
	}

	protected void initializeGraphics() {
		if (getNodeGraphics() == null) {
			setNodeGraphics(new XPDLNodeGraphicsInfos());
			getNodeGraphics().add(new XPDLNodeGraphicsInfo());
		}
	}
}
