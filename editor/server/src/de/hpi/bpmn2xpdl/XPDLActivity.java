package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLActivity extends XPDLThingNodeGraphics {

	protected String completionQuantity;
	protected String isATransaction;
	protected String isForCompensation;
	protected String startActivity;
	protected String startQuantity;
	
	protected XPDLEvent event;
	protected XPDLLoop loop;
	protected XPDLRoute route;
	
	protected ArrayList<XPDLAssignment> assignments;
	
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
				
				//Activities
				"CollapsedSubprocess",
				"Subprocess",
				"Task"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Actitivity", XPDLActivity.class);
		
		xstream.useAttributeFor(XPDLActivity.class, "completionQuantity");
		xstream.aliasField("CompletionQuantity", XPDLActivity.class, "completionQuantity");
		xstream.useAttributeFor(XPDLActivity.class, "isATransaction");
		xstream.aliasField("IsATransaction", XPDLActivity.class, "isATransaction");
		xstream.useAttributeFor(XPDLActivity.class, "isForCompensation");
		xstream.aliasField("IsForCompensation", XPDLActivity.class, "isForCompensation");
		xstream.useAttributeFor(XPDLActivity.class, "startActivity");
		xstream.aliasField("StartActivity", XPDLActivity.class, "startActivity");
		xstream.useAttributeFor(XPDLActivity.class, "startQuantity");
		xstream.aliasField("StartQuantity", XPDLActivity.class, "startQuantity");
		
		xstream.aliasField("xpdl2:Route", XPDLActivity.class, "route");
		xstream.aliasField("xpdl2:Loop", XPDLActivity.class, "loop");
		xstream.aliasField("xpdl2:Event", XPDLActivity.class, "event");
		xstream.aliasField("xpdl2:Assignments", XPDLActivity.class, "assignments");
	}
	
	public ArrayList<XPDLAssignment> getAssignments() {
		return assignments;
	}
	
	public String getCompletionQuantity() {
		return completionQuantity;
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
	
	public void readJSONactivityref(JSONObject modelElement) {
		createExtendedAttribute("activityref", modelElement.optString("activityref"));
	}
	
	public void readJSONcompletionquantity(JSONObject modelElement) {
		setCompletionQuantity(modelElement.optString("completionquantity"));
	}
	
	public void readJSONcomplexmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "complex_micondition");
	}
	
	public void readJSONconditionref(JSONObject modelElement) {
		createExtendedAttribute("conditionref", modelElement.optString("conditionref"));
	}
	
	public void readJSONdiagramref(JSONObject modelElement) {
		createExtendedAttribute("diagramref", modelElement.optString("diagramref"));
	}
	
	public void readJSONeventtype(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "eventtype");
	}
	
	public void readJSONgatewaytype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "gatewaytype");
	}
	
	public void readJSONimplementation(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "implementation");
	}
		
	public void readJSONincomingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "incomingcondition");
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
		
	public void readJSONiscompensation(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "iscompensation");
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
		passInformationToLoop(modelElement, "loopmaxmimum");
	}
	
	public void readJSONmessageref(JSONObject modelElement) {
		createExtendedAttribute("messageref", modelElement.optString("messageref"));
	}
	
	public void readJSONmi_ordering(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_ordering");
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
	
	public void readJSONoutgoingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "outgoingcondition");
	}
	
	public void readJSONoutputsets(JSONObject modelElement) {
		createExtendedAttribute("outputsets", modelElement.optString("outputsets"));
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
		createExtendedAttribute("processref", modelElement.optString("processref"));
	}
	
	public void readJSONresult(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "result");
	}
	
	public void readJSONstartquantity(JSONObject modelElement) {
		setStartQuantity(modelElement.optString("startquantity"));
	}
	
	public void readJSONsignalref(JSONObject modelElement) {
		createExtendedAttribute("signalref", modelElement.optString("signalref"));
	}

	public void readJSONtarget(JSONObject modelElement) {
		createExtendedAttribute("target", modelElement.optString("target"));
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
		createExtendedAttribute("taskref", modelElement.optString("taskref"));
	}
	
	public void readJSONtesttime(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "testtime");
	}
	
	public void readJSONtrigger(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "trigger");
	}
	
	public void readJSONxortype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "xortype");
	}
	
	public void setAssignments(ArrayList<XPDLAssignment> assignmentsValue) {
		assignments = assignmentsValue;
	}
	
	public void setCompletionQuantity(String quantity) {
		completionQuantity = quantity;
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
			setAssignments(new ArrayList<XPDLAssignment>());
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
		initializeEvent();
		
		JSONObject eventPassObject = new JSONObject();
		eventPassObject.put(key, modelElement.optString(key));
		eventPassObject.put("eventtype", modelElement.optString("eventtype"));
		
		getEvent().parse(eventPassObject);
	}
	
	protected void passInformationToLoop(JSONObject modelElement, String key) throws JSONException {
		if (!modelElement.optString("looptype").equals("None")) {
			initializeLoop();
		
			JSONObject loopPassObject = new JSONObject();
			loopPassObject.put(key, modelElement.optString(key));
		
			getLoop().parse(loopPassObject);
		}
	}
	
	protected void passInformationToRoute(JSONObject modelElement, String key) throws JSONException {
		initializeRoute();
		
		JSONObject routePassObject = new JSONObject();
		routePassObject.put(key, modelElement.optString(key));

		getRoute().parse(routePassObject);
	}
}
