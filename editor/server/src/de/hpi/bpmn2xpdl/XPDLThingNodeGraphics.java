package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public abstract class XPDLThingNodeGraphics extends XPDLThing {

	protected ArrayList<XPDLNodeGraphicsInfo> nodeGraphics;

	public static void registerMapping(XStream xstream) {
		xstream.alias("Thing", XPDLThingNodeGraphics.class);
		
		xstream.aliasField("xpdl2:NodeGraphicsInfos", XPDLThingNodeGraphics.class, "nodeGraphics");
	}

	public ArrayList<XPDLNodeGraphicsInfo> getNodeGraphicsInfos() {
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

	public void setNodeGraphicsInfos(ArrayList<XPDLNodeGraphicsInfo> graphics) {
		nodeGraphics = graphics;
	}
	
	protected XPDLNodeGraphicsInfo getFirstGraphicsInfo() {
		return getNodeGraphicsInfos().get(0);
	}

	protected void initializeGraphics() {
		if (getNodeGraphicsInfos() == null) {
			setNodeGraphicsInfos(new ArrayList<XPDLNodeGraphicsInfo>());
			getNodeGraphicsInfos().add(new XPDLNodeGraphicsInfo());
		}
	}
}
