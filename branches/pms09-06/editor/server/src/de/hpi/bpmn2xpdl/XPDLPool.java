package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Pool")
public class XPDLPool extends XPDLThingNodeGraphics {
	
	protected XPDLWorkflowProcess accordingProcess;
	
	@Attribute("BoundaryVisible")
	protected boolean boundaryVisible = true;
	@Attribute("MainPool")
	protected boolean mainPool = false;
	@Attribute("Process")
	protected String process;
	@Attribute("Orientation")
	protected String orientation = "HORIZONTAL";
	
	@Element("Lanes")
	protected ArrayList<XPDLLane> lanes;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"Pool",
				"CollapsedPool"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public XPDLWorkflowProcess getAccordingProcess() {
		return accordingProcess;
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
	
	public void readJSONadhoc(JSONObject modelElement) {
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
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

	public void readJSONenableinstancecompensation(JSONObject modelElement) {
	}
	
	public void readJSONmainpool(JSONObject modelElement) {
		setMainPool(modelElement.optBoolean("mainpool"));
	}
	
	public void readJSONparticipantref(JSONObject modelElement) {
	}
	
	public void readJSONpoolcategories(JSONObject modelElement) throws JSONException {
		JSONObject categorieObject = new JSONObject();
		categorieObject.put("categories", modelElement.optString("poolcategories"));
		categorieObject.put("id", getProperId(modelElement));
		
		parse(categorieObject);
	}
	
	public void readJSONpooldocumentation(JSONObject modelElement) throws JSONException {
		JSONObject documentationObject = new JSONObject();
		documentationObject.put("documentation", modelElement.optString("pooldocumentation"));
		documentationObject.put("id", getProperId(modelElement));
		
		parse(documentationObject);
	}
	
	
	public void readJSONpoolid(JSONObject modelElement) {
		setId(getProperId(modelElement));
	}
	
	public void readJSONprocesscategories(JSONObject modelElement) {
	}
	
	public void readJSONprocessdocumentation(JSONObject modelElement) {
	}
	
	public void readJSONprocessname(JSONObject modelElement) {
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
	}
	
	public void readJSONprocesstype(JSONObject modelElement) {
	}
	
	public void readJSONstatus(JSONObject modelElement) {
	}
	
	public void readJSONsuppressjoinfailure(JSONObject modelElement) {
	}
	
	public void setAccordingProcess(XPDLWorkflowProcess processValue) {
		accordingProcess = processValue;
		setProcess(processValue.getId());
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
	
	public void setOrientation(String orientationValue) {
		orientation = orientationValue;
	}
	
	public void setProcess(String processValue) {
		process = processValue;
	}
	
	protected void createLane(JSONObject modelElement) {
		initializeLanes();
		
		XPDLLane nextLane = new XPDLLane();
		nextLane.parse(modelElement);
		getLanes().add(nextLane);
	}
	
	protected String getProperId(JSONObject modelElement) {
		String idValue = modelElement.optString("poolid");
		if (!idValue.equals("")) {
			return idValue;
		}
		return modelElement.optString("resourceId");
	}
	
	protected void initializeLanes() {
		if(getLanes() == null) {
			setLanes(new ArrayList<XPDLLane>());
		}
	}
}