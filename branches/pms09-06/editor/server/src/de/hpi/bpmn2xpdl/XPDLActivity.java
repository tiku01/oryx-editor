package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;

public class XPDLActivity extends XPDLThingNodeGraphics {

	@Attribute("CompletionQuantity")
	protected String completionQuantity;
	@Attribute("IsATransaction")
	protected String isATransaction;
	@Attribute("IsForCompensation")
	protected String isForCompensation;
	@Attribute("StartActivity")
	protected String startActivity;
	@Attribute("StartQuantity")
	protected String startQuantity;
	@Attribute("Status")
	protected String status;
	
	@Element("Event")
	protected XPDLEvent event;
	@Element("Loop")
	protected XPDLLoop loop;
	@Element("Route")
	protected XPDLRoute route;
	@Element("BlockActivity")
	protected XPDLBlockActivity blockActivity;
	@Element("Documentation")
	protected XPDLDocumentation documentation;
	@Element("Assignments")
	protected XPDLAssignments assignments;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				//Gateways
				"Complex_Gateway",
				"OR_Gateway",
				"AND_Gateway",
				"Exclusive_Eventbased_Gateway",
				"Exclusive_Databased_Gateway",
				
				//Events
				"StartEvent",
				"StartConditionalEvent",
				"StartMessageEvent",
				"StartMultipleEvent",
				"StartSignalEvent",
				"StartTimerEvent",
				
				"IntermediateEvent",
				"IntermediateCancelEvent",
				"IntermediateCompensationEventCatching",
				"IntermediateConditionalEvent",
				"IntermediateErrorEvent",
				"IntermediateLinkEventCatching",
				"IntermediateMessageEventCatching",
				"IntermediateMultipleEventCatching",
				"IntermediateSignalEventCatching",
				"IntermediateTimerEvent",
				
				"IntermediateCompensationEventThrowing",
				"IntermediateLinkEventThrowing",
				"IntermediateMessageEventThrowing",
				"IntermediateMultipleEventThrowing",
				"IntermediateSignalEventThrowing",
				
				"EndEvent",
				"EndCancelEvent",
				"EndCompensationEvent",
				"EndErrorEvent",
				"EndMessageEvent",
				"EndMultipleEvent",
				"EndSignalEvent",
				"EndTerminateEvent",
				
				//Task
				"Task",
				
				//Subprocesses
				"CollapsedSubprocess",
				"Subprocess"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public XPDLAssignments getAssignments() {
		return assignments;
	}
	
	public XPDLBlockActivity getBlockActivity() {
		return blockActivity;
	}

	public String getCompletionQuantity() {
		return completionQuantity;
	}
	
	public XPDLDocumentation getDocumentation() {
		return documentation;
	}
	
	public XPDLEvent getEvent() {
		return event;
	}
	
	public String getIsATransaction() {
		return isATransaction;
	}
	
	public String getIsForCompensation() {
		return isForCompensation;
	}
	
	public XPDLLoop getLoop() {
		return loop;
	}
	
	public XPDLRoute getRoute() {
		return route;
	}
	
	public String getStartActivity() {
		return startActivity;
	}
	
	public String getStartQuantity() {
		return startQuantity;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void readJSONassignments(JSONObject modelElement) throws JSONException {
		JSONArray items = modelElement.optJSONObject("assignments").optJSONArray("items");
		
		if (items != null) {
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.optJSONObject(i);
				createAssignment(item);
				createExtendedAttribute("assignmentTo", item.optString("to"));
				createExtendedAttribute("assignmentFrom", item.optString("from"));
			}
		}
	}
	
	public void readJSONactivity(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "activity");
	}
	
	public void readJSONactivitytype(JSONObject modelElement) {
		createExtendedAttribute("activitytype", modelElement.optString("activitytype"));
	}
	
	public void readJSONactivityref(JSONObject modelElement) {
		createExtendedAttribute("activityref", modelElement.optString("activityref"));
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
	}
	
	public void readJSONcompletionquantity(JSONObject modelElement) {
		setCompletionQuantity(modelElement.optString("completionquantity"));
	}
	
	public void readJSONcomplexmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "complexmi_condition");
	}
	
	public void readJSONcondition(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "conditionref");
	}
	
	public void readJSONconditionref(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "conditionref");
	}
	
	public void readJSONdocumentation(JSONObject modelElement) {
		XPDLDocumentation doc = new XPDLDocumentation();
		doc.setContent(modelElement.optString("documentation"));
		
		setDocumentation(doc);
	}
	
	public void readJSONdefaultgate(JSONObject modelElement) {
		createExtendedAttribute("defaultgate", modelElement.optString("defaultgate"));
	}
	
	public void readJSONdiagramref(JSONObject modelElement) {
		createExtendedAttribute("diagramref", modelElement.optString("diagramref"));
	}
	
	public void readJSONentry(JSONObject modelElement) {
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "errorcode");
	}
	
	public void readJSONeventtype(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "eventtype");
	}
	
	public void readJSONgates(JSONObject modelElement) {
		createExtendedAttribute("gates", modelElement.optString("gates"));
	}
	
	public void readJSONgate_assignments(JSONObject modelElement) {
		createExtendedAttribute("gate_assignments", modelElement.optString("gate_assignments"));
	}
	
	public void readJSONgates_assignments(JSONObject modelElement) {
		createExtendedAttribute("gates_assignments", modelElement.optString("gates_assignments"));
	}
	
	public void readJSONgate_outgoingsequenceflow(JSONObject modelElement) {
		createExtendedAttribute("gate_outgoingsequenceflow", modelElement.optString("gate_outgoingsequenceflow"));
	}
	
	public void readJSONgates_outgoingsequenceflow(JSONObject modelElement) {
		createExtendedAttribute("gates_outgoingsequenceflow", modelElement.optString("gates_outgoingsequenceflow"));
	}
	
	public void readJSONgatewaytype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "gatewaytype");
	}
	
	public void readJSONimplementation(JSONObject modelElement) throws JSONException {
		if (!modelElement.optString("eventtype").equals("")) {
			passInformationToEvent(modelElement, "implementation");
		}
	}
		
	public void readJSONincomingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "incomingcondition");
	}
	
	public void readJSONinmessage(JSONObject modelElement) {
		createExtendedAttribute("inmessage", modelElement.optString("inmessage"));
	}
	
	public void readJSONinputmaps(JSONObject modelElement) {
	}
	
	public void readJSONinputs(JSONObject modelElement) {
		createExtendedAttribute("inputs", modelElement.optString("inputs"));
	}
	
	public void readJSONinputsets(JSONObject modelElement) {
		createExtendedAttribute("inputsets", modelElement.optString("inputsets"));
	}
	
	
	public void readJSONinstantiate(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "instantiate");
	}
	
	public void readJSONiorules(JSONObject modelElement) {
		createExtendedAttribute("iorules", modelElement.optString("iorules"));
	}
	
	public void readJSONisadhoc(JSONObject modelElement) {
	}
	
	public void readJSONisatransaction(JSONObject modelElement) {
	}
		
	public void readJSONiscompensation(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "iscompensation");
	}
	
	public void readJSONlinkid(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "linkid");
	}
	
	public void readJSONloopcondition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "loopcondition");
	}
	
	public void readJSONloopcounter(JSONObject modelElement) throws JSONException {
		if (!modelElement.optString("looptype").equals("None")) {
			initializeLoop();
		
			JSONObject loopPassObject = new JSONObject();
			loopPassObject.put("loopcounter", modelElement.optString("loopcounter"));
			loopPassObject.put("looptype", modelElement.optString("looptype"));
		
			getLoop().parse(loopPassObject);
		}
	}
	
	public void readJSONloopmaximum(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "loopmaximum");
	}
	
	public void readJSONlooptype(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "looptype");
	}
	
	public void readJSONmarkervisible(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "markervisible");
	}
	
	public void readJSONmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_condition");
	}
	
	public void readJSONmi_flowcondition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_flowcondition");
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "message");
	}
	
	public void readJSONmessageref(JSONObject modelElement) {
		createExtendedAttribute("messageref", modelElement.optString("messageref"));
	}
	
	public void readJSONmi_ordering(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_ordering");
	}
	
	public void readJSONoutgoingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "outgoingcondition");
	}
	
	public void readJSONoutmessage(JSONObject modelElement) {
		createExtendedAttribute("outmessage", modelElement.optString("outmessage"));
	}
	
	public void readJSONoutputs(JSONObject modelElement) {
		createExtendedAttribute("outputs", modelElement.optString("outputs"));
	}
	
	public void readJSONoutputmaps(JSONObject modelElement) {
	}
	
	public void readJSONoutputsets(JSONObject modelElement) {
		createExtendedAttribute("outputsets", modelElement.optString("outputsets"));
	}
	
	public void readJSONperformers(JSONObject modelElement) {
		createExtendedAttribute("performers", modelElement.optString("performers"));
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
		createExtendedAttribute("processref", modelElement.optString("processref"));
	}
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException {
		try{
			JSONObject properties = modelElement.optJSONObject("properties");
			properties.put("resourceId", getProperId(modelElement));
			properties.put("stencil", modelElement.optJSONObject("stencil").optString("id"));
			parse(properties);
		} catch (Exception e) {
			//dirty hack: Event could be MultiInstance and may have a different subkey properties
		}
	}
	
	public void readJSONresult(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "result");
	}
	
	public void readJSONscript(JSONObject modelElement) {
		createExtendedAttribute("script", modelElement.optString("script"));
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "signalref");
	}
	
	public void readJSONstartquantity(JSONObject modelElement) {
		setStartQuantity(modelElement.optString("startquantity"));
	}
	
	public void readJSONstatus(JSONObject modelElement) {
		setStatus(modelElement.optString("status"));
	}
	
	public void readJSONsubprocesstype(JSONObject modelElement) throws JSONException {
		setBlockActivity(new XPDLBlockActivity());
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put("subprocesstype", modelElement.optString("subprocesstype"));
		
		getBlockActivity().parse(passObject);
	}

	public void readJSONtarget(JSONObject modelElement) {
		createExtendedAttribute("target", modelElement.optString("target"));
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
		createExtendedAttribute("taskref", modelElement.optString("taskref"));
	}
	
	public void readJSONtasktype(JSONObject modelElement) {
	}
	
	public void readJSONtesttime(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "testtime");
	}
	
	public void readJSONtimecycle(JSONObject modelElement) throws JSONException {
		System.out.println();
		passInformationToEvent(modelElement, "timecycle");
	}
	
	public void readJSONtimedate(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "timedate");
	}
	
	public void readJSONtransaction(JSONObject modelElement) {
	}
	
	public void readJSONtrigger(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "trigger");
	}
	
	public void readJSONtriggers(JSONObject modelElement) {
		createExtendedAttribute("triggers", modelElement.optString("triggers"));
	}
	
	public void readJSONxortype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "xortype");
	}
	
	public void setAssignments(XPDLAssignments assignmentsValue) {
		assignments = assignmentsValue;
	}
	
	public void setBlockActivity(XPDLBlockActivity blockActivity) {
		this.blockActivity = blockActivity;
	}

	public void setCompletionQuantity(String quantity) {
		completionQuantity = quantity;
	}
	
	public void setDocumentation(XPDLDocumentation documentationValue) {
		documentation = documentationValue;
	}
	
	public void setEvent(XPDLEvent eventValue) {
		event = eventValue;
	}
	
	public void setIsATransaction(String status) {
		isATransaction = status;
	}
	
	public void setIsForCompensation(String status) {
		isForCompensation = status;
	}
	
	public void setLoop(XPDLLoop loopValue) {
		loop = loopValue;
	}
	
	public void setRoute(XPDLRoute routeValue) {
		route = routeValue;
	}
	
	public void setStartActivity(String isStart) {
		startActivity = isStart;
	}
	
	public void setStartQuantity(String quantity) {
		startQuantity = quantity;
	}
	
	public void setStatus(String statusValue) {
		status = statusValue;
	}
	
	protected void createAssignment(JSONObject modelElement) throws JSONException {
		initializeAssignments();
		
		JSONObject assignmentObject = new JSONObject();
		assignmentObject.put("assigntime", modelElement.optString("assigntime"));
		
		XPDLAssignment newAssignment = new XPDLAssignment();
		newAssignment.parse(assignmentObject);
		
		getAssignments().add(newAssignment);
	}
	
	protected void initializeAssignments() {
		if (getAssignments() == null) {
			setAssignments(new XPDLAssignments());
		}
	}
	
	protected void initializeEvent() {
		if (getEvent() == null) {
			setEvent(new XPDLEvent());
		}
	}
	
	protected void initializeLoop() {
		if (getLoop() == null) {
			setLoop(new XPDLLoop());
		}
	}
	
	protected void initializeRoute() {
		if (getRoute() == null) {
			setRoute(new XPDLRoute());
		}
	}
	
	protected void passInformationToEvent(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Event")) {
			initializeEvent();
		
			JSONObject eventPassObject = new JSONObject();
			eventPassObject.put(key, modelElement.optString(key));
			eventPassObject.put("eventtype", modelElement.optString("eventtype"));
			eventPassObject.put("stencil", modelElement.optString("stencil"));
		
			getEvent().parse(eventPassObject);
		}
	}
	
	protected void passInformationToLoop(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Task")) {
			if (!modelElement.optString("looptype").equals("None")) {
				initializeLoop();
		
				JSONObject loopPassObject = new JSONObject();
				loopPassObject.put(key, modelElement.optString(key));
				loopPassObject.put("looptype", modelElement.optString("looptype"));
		
				getLoop().parse(loopPassObject);
			}
		}
	}
	
	protected void passInformationToRoute(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Gateway")) {
			initializeRoute();
		
			JSONObject routePassObject = new JSONObject();
			routePassObject.put(key, modelElement.optString(key));

			getRoute().parse(routePassObject);
		}
	}
}
