package de.hpi.bpmn2xpdl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("WorkflowProcess")
public class XPDLWorkflowProcess extends XPDLThing {
	
	protected static String ID_SUFFIX = "-process";
	
	@Attribute("Adhoc")
	protected String adhoc;
	@Attribute("AdhocOrdering")
	protected String adhocOrdering;
	@Attribute("AdhocCompletionCondition")
	protected String adhocCompletionCondition;
	@Attribute("EnableInstanceCompensation")
	protected String enableInstanceCompensation;
	@Attribute("ProcessType")
	protected String processType;
	@Attribute("Status")
	protected String status;
	@Attribute("SuppressJoinFailure")
	protected String suppressJoinFailure;
	
	@Element("ActivitySets")
	protected XPDLActivitySets activitySets;
	@Element("Activities")
	protected XPDLActivities  activities;
	@Element("Transitions")
	protected XPDLTransitions transitions;
	
	public XPDLActivities getActivities() {
		return activities;
	}
	
	public XPDLActivitySets getActivitySets() {
		return activitySets;
	}
	
	public String getAdhoc() {
		return adhoc;
	}
	
	public String getAdhocCompletionCondition() {
		return adhocCompletionCondition;
	}
	
	public String getAdhocOrdering() {
		return adhocOrdering;
	}
	
	public String getAdhocOrderingCondition() {
		return adhocCompletionCondition;
	}
	
	public String getEnableInstanceCompensation() {
		return enableInstanceCompensation;
	}

	public String getProcessType() {
		return processType;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getSuppressJoinFailure() {
		return suppressJoinFailure;
	}
	
	public XPDLTransitions getTransitions() {
		return transitions;
	}
	
	public void readJSONadhoc(JSONObject modelElement) {
		setAdhoc(modelElement.optString("adhoc"));
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
		setAdhocOrdering(modelElement.optString("adhocordering"));
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
		setAdhocCompletionCondition(modelElement.optString("adhocCompletionCondition"));
	}
	
	public void readJSONbounds(JSONObject modelElement) {
	}
	
	public void readJSONboundaryvisible(JSONObject modelElement) {
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null) {
			for (int i = 0; i<childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (XPDLActivitySet.handlesStencil(stencil)) {
					createActivitySet(childShape);
				} else if (XPDLTransition.handlesStencil(stencil)) {
					createTransition(childShape);
				} else if (XPDLActivity.handlesStencil(stencil)) {
					createActivity(childShape);
				}
			}
		}
	}
	
	public void readJSONenableinstancecompensation(JSONObject modelElemet) {
		setEnableInstanceCompensation(modelElemet.optString("enableinstancecompensation"));
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(getProperId(modelElement) + XPDLWorkflowProcess.ID_SUFFIX);
	}
	
	public void readJSONmainpool(JSONObject modelElement) {
	}
	
	public void readJSONname(JSONObject modelElement) {
	}
	
	public void readJSONparticipantref(JSONObject modelElement) {
		createExtendedAttribute("participantref", modelElement.optString("participantref"));
	}
	
	public void readJSONpoolcategories(JSONObject modelElement) {
	}
	
	public void readJSONpooldocumentation(JSONObject modelElement) {
	}
	
	public void readJSONpoolid(JSONObject modelElement) {
	}
	
	public void readJSONprocesscategories(JSONObject modelElement) throws JSONException {
		JSONObject categorieObject = new JSONObject();
		categorieObject.put("categories", modelElement.optString("processcategories"));
		categorieObject.put("id", getProperId(modelElement));
		
		parse(categorieObject);
	}
	
	public void readJSONprocessdocumentation(JSONObject modelElement) throws JSONException {
		JSONObject documentationObject = new JSONObject();
		documentationObject.put("documentation", modelElement.optString("processdocumentation"));
		documentationObject.put("id", getProperId(modelElement));
		
		parse(documentationObject);
	}
	
	public void readJSONprocessname(JSONObject modelElement) {
		setName(modelElement.optString("processname"));
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
		createExtendedAttribute("processref", modelElement.optString("processref"));
	}
	
	public void readJSONprocesstype(JSONObject modelElement) {
		setProcessType(modelElement.optString("processtype"));
	}
	
	public void readJSONresourceId(JSONObject modelElement) {
		setResourceId(modelElement.optString("resourceId"));
		
		setId(getProperId(modelElement) + XPDLWorkflowProcess.ID_SUFFIX);
	}
	
	public void readJSONstatus(JSONObject modelElement) {
		setStatus(modelElement.optString("status"));
	}
	
	public void readJSONsuppressjoinfailure(JSONObject modelElement) {
		setSuppressJoinFailure(modelElement.optString("suppressjoinfailure"));
	}
	
	public void setActivities(XPDLActivities activitiesList) {
		activities = activitiesList;
	}
	
	public void setActivitySets(XPDLActivitySets sets) {
		activitySets = sets;
	}
	
	public void setAdhoc(String adhocValue) {
		adhoc = adhocValue;
	}
	
	public void setAdhocCompletionCondition(String condition) {
		adhocCompletionCondition = condition;
	}
	
	public void setAdhocOrdering(String orderingValue) {
		adhocOrdering = orderingValue;
	}
	
	public void setAdhocOrderingCondition(String conditionValue) {
		adhocCompletionCondition = conditionValue;
	}
	
	public void setEnableInstanceCompensation(String compensation) {
		enableInstanceCompensation = compensation;
	}
	
	public void setProcessType(String typeValue) {
		processType = typeValue;
	}
	
	public void setStatus(String statusValue) {
		status = statusValue;
	}
	
	public void setSuppressJoinFailure(String joinFailure) {
		suppressJoinFailure = joinFailure;
	}
	
	public void setTransitions(XPDLTransitions transitionsValue) {
		transitions = transitionsValue;
	}
	
	protected void createActivity(JSONObject modelElement) {
		initializeActivities();
		
		XPDLActivity nextActivity = new XPDLActivity();
		nextActivity.parse(modelElement);
		getActivities().add(nextActivity);
	}
	
	protected void createActivitySet(JSONObject modelElement) throws JSONException {
		initializeActivitySets();
		
		XPDLActivitySet nextSet = new XPDLActivitySet();
		JSONObject passObject = new JSONObject();
		
		passObject.put("childShapes", modelElement.optJSONArray("childShapes"));
		passObject.put("adhoccompletioncondition", modelElement.optString("adhoccompletioncondition"));
		passObject.put("adhocordering", modelElement.optString("adhocordering"));
		passObject.put("isadhoc", modelElement.optString("isadhoc"));
		passObject.put("id", getProperId(modelElement));
		
		nextSet.parse(passObject);
		getActivitySets().add(nextSet);
		
		createActivity(modelElement);
	}
	
	protected void createTransition(JSONObject modelElement) {
		initializeTransitions();
		
		XPDLTransition nextTranistion = new XPDLTransition();
		nextTranistion.parse(modelElement);
		getTransitions().add(nextTranistion);
	}
	
	protected void initializeActivities() {
		if (getActivities() == null) {
			setActivities(new XPDLActivities());
		}
	}
	
	protected void initializeActivitySets() {
		if (getActivitySets() == null) {
			setActivitySets(new XPDLActivitySets());
		}
	}
	
	protected void initializeTransitions() {
		if (getTransitions() == null) {
			setTransitions(new XPDLTransitions());
		}
	}
}
