package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLPool extends XPDLThingNodeGraphics {

	protected boolean boundaryVisible = true;
	protected boolean mainPool = false;
	protected String process;
	protected String orientation = "HORIZONTAL";
	
	protected ArrayList<XPDLLane> lanes;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"Pool",
				"CollapsedPool"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Pool", XPDLPool.class);
		
		xstream.useAttributeFor(XPDLPool.class, "boundaryVisible");
		xstream.aliasField("BoundaryVisible", XPDLPool.class, "boundaryVisible");
		xstream.useAttributeFor(XPDLPool.class, "orientation");
		xstream.aliasField("Orientation", XPDLPool.class, "orientation");
		xstream.useAttributeFor(XPDLPool.class, "mainPool");
		xstream.aliasField("MainPool", XPDLPool.class, "mainPool");
		xstream.useAttributeFor(XPDLPool.class, "process");
		xstream.aliasField("Process", XPDLPool.class, "process");
		
		xstream.aliasField("xpdl2:Lanes", XPDLPool.class, "lanes");
	}
	
	public boolean getBoundaryVisible() {
		return boundaryVisible;
	}
	
	public ArrayList<XPDLLane> getLanes() {
		return lanes;
	}
	
	public boolean getMainPool() {
		return mainPool;
	}
	
	public String getProcess() {
		return process;
	}
	
	public String getOrientation() {
		return orientation;
	}
	
	public void readJSONboundaryvisible(JSONObject modelElement) {
		setBoundaryVisible(modelElement.optBoolean("boundaryvisible"));
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if(childShapes != null) {
			for(int i = 0; i < childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if(XPDLLane.handlesStencil(stencil)) {
					readJSONresourceId(modelElement);
					childShape.put("parentpool", getProperId(modelElement));
					createLane(childShape);
				} else {
					readJSONchildShapes(modelElement);
				}
			}
		}
	}
	
	public void readJSONmainpool(JSONObject modelElement) {
		setMainPool(modelElement.optBoolean("mainpool"));
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
		setProcess(modelElement.optString("processref"));
	}
	
	public void setBoundaryVisible(boolean visibility) {
		boundaryVisible = visibility;
	}
	
	public void setLanes(ArrayList<XPDLLane> lanesValue) {
		lanes = lanesValue;
	}
	
	public void setMainPool(boolean isMainPool) {
		mainPool = isMainPool;
	}
	
	public void setProcess(String processId) {
		process = processId;
	}
	
	public void setOrientation(String orientationValue) {
		orientation = orientationValue;
	}
	
	protected void createLane(JSONObject modelElement) {
		initializeLanes();
		
		XPDLLane nextLane = new XPDLLane();
		nextLane.parse(modelElement);
		getLanes().add(nextLane);
	}
	
	protected void initializeLanes() {
		if(getLanes() == null) {
			setLanes(new ArrayList<XPDLLane>());
		}
	}
}