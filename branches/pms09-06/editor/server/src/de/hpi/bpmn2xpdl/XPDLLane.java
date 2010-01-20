package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLLane extends XPDLThingNodeGraphics {

	protected String parentLane;
	protected String parentPool;

	protected ArrayList<XPDLLane> nestedLanes;

	public static boolean handlesStencil(String stencil) {
		String[] types = { "Lane" };
		return Arrays.asList(types).contains(stencil);
	}

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Lane", XPDLLane.class);

		xstream.useAttributeFor(XPDLLane.class, "parentLane");
		xstream.aliasField("ParentLane", XPDLLane.class, "parentLane");
		xstream.useAttributeFor(XPDLLane.class, "parentPool");
		xstream.aliasField("ParentPool", XPDLLane.class, "parentPool");
		xstream.aliasField("xpdl2:NestedLanes", XPDLLane.class, "nestedLanes");
	}

	public ArrayList<XPDLLane> getNestedLanes() {
		return nestedLanes;
	}

	public String getParentLane() {
		return parentLane;
	}

	public String getParentPool() {
		return parentPool;
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if(childShapes != null) {
			for(int i = 0; i < childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if(XPDLLane.handlesStencil(stencil)) {
					readJSONresourceId(modelElement);
					childShape.put("parentlane", getProperId(modelElement));
					createNestedLane(childShape);
				} else {
					readJSONchildShapes(childShape);
				}
			}
		}
	}

	public void readJSONparentlane(JSONObject modelElement) {
		setParentLane(modelElement.optString("parentlane"));
	}

	public void readJSONparentpool(JSONObject modelElement) {
		setParentPool(modelElement.optString("parentpool"));
	}
	
	public void readJSONshowcaption(JSONObject modelElement) {
		createExtendedAttribute("showcaption", modelElement.optString("showcaption"));
	}

	public void setNestedLanes(ArrayList<XPDLLane> lanes) {
		nestedLanes = lanes;
	}

	public void setParentLane(String laneId) {
		parentLane = laneId;
	}

	public void setParentPool(String poolId) {
		parentPool = poolId;
	}

	protected void createNestedLane(JSONObject modelElement) {
		initializeNestedLanes();
		
		XPDLLane nextLane = new XPDLLane();
		nextLane.parse(modelElement);
		getNestedLanes().add(nextLane);
	}
	
	protected void initializeNestedLanes() {
		if (getNestedLanes() == null) {
			setNestedLanes(new ArrayList<XPDLLane>());
		}
	}
}