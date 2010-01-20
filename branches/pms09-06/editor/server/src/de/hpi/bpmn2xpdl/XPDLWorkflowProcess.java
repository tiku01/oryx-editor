package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLWorkflowProcess extends XPDLThing {
	
	protected static String ID_SUFFIX = "-process";
	
	protected String accessLevel;
	protected String adhoc;
	protected String adhocOrdering;
	protected String adhocCompletionCondition;
	protected String enableInstanceCompensation;
	protected String processType;
	protected String status;
	protected String suppressJoinFailure;
	
	protected ArrayList<XPDLActivity>  activities;
	protected ArrayList<XPDLTransition> transitions;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:WorkflowProcess", XPDLWorkflowProcess.class);
		
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "accessLevel");
		xstream.aliasField("AccessLevel", XPDLWorkflowProcess.class, "accessLevel");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "adhoc");
		xstream.aliasField("Adhoc", XPDLWorkflowProcess.class, "adhoc");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "adhocOrdering");
		xstream.aliasField("AdhocOrdering", XPDLWorkflowProcess.class, "adhocOrdering");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "adhocCompletionCondition");
		xstream.aliasField("AdhocCompletionCondition", XPDLWorkflowProcess.class, "adhocCompletionCondition");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "enableInstanceCompensation");
		xstream.aliasField("EnableInstanceCompensation", XPDLWorkflowProcess.class, "enableInstanceCompensation");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "processType");
		xstream.aliasField("ProcessType", XPDLWorkflowProcess.class, "processType");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "status");
		xstream.aliasField("Status", XPDLWorkflowProcess.class, "status");
		xstream.useAttributeFor(XPDLWorkflowProcess.class, "suppressJoinFailure");
		xstream.aliasField("SuppressJoinFailure", XPDLWorkflowProcess.class, "suppressJoinFailure");

		xstream.aliasField("xpdl2:Transitions", XPDLWorkflowProcess.class, "transitions");
		xstream.aliasField("xpdl2:Activities", XPDLWorkflowProcess.class, "activities");
	}
	
	public String getAccessLevel() {
		return accessLevel;
	}
	
	public ArrayList<XPDLActivity> getActivities() {
		return activities;
	}
	
	public String getAdhoc() {
		return adhoc;
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
	
	public ArrayList<XPDLTransition> getTransition() {
		return transitions;
	}
	
	public void readJSONadhoc(JSONObject modelElement) {
		setAdhoc(modelElement.optString("adhoc"));
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
		setAdhocOrdering(modelElement.optString("adhocordering"));
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
		setAdhocOrdering(modelElement.optString("adhocCompletionCondition"));
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
				
				if (XPDLTransition.handlesStencil(stencil)) {
					createTransition(childShape);
				} else if (XPDLActivity.handlesStencil(stencil)) {
					createActivity(childShape);
				}
				readJSONchildShapes(childShape);
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
	
	public void setAccessLevel(String level) {
		accessLevel = level;
	}
	
	public void setActivities(ArrayList<XPDLActivity> activitiesList) {
		activities = activitiesList;
	}
	
	public void setAdhoc(String adhocValue) {
		adhoc = adhocValue;
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
	
	public void setTransitions(ArrayList<XPDLTransition> transitionsValue) {
		transitions = transitionsValue;
	}
	
	protected void createActivity(JSONObject modelElement) {
		initializeActivities();
		
		XPDLActivity nextActivity = new XPDLActivity();
		nextActivity.parse(modelElement);
		getActivities().add(nextActivity);
	}
	
	protected void createTransition(JSONObject modelElement) {
		initializeTransitions();
		
		XPDLTransition nextTranistion = new XPDLTransition();
		nextTranistion.parse(modelElement);
		getTransition().add(nextTranistion);
	}
	
	protected void initializeActivities() {
		if (getActivities() == null) {
			setActivities(new ArrayList<XPDLActivity>());
		}
	}
	
	protected void initializeTransitions() {
		if (getTransition() == null) {
			setTransitions(new ArrayList<XPDLTransition>());
		}
	}
}
