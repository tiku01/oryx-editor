package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLActivity extends XPDLThingNodeGraphics {

	protected String completionQuantity;
	protected String isATransaction;
	protected String isForCompensation;
	protected String startActivity;
	protected String startQuantity;
	
	protected XPDLLoop loop;
	protected XPDLRoute route;
	
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
	}
	
	public String getCompletionQuantity() {
		return completionQuantity;
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
	
	public void readJSONgatewaytype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "gatewaytype");
	}
	
	public void readJSONincomingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "incomingcondition");
	}
	
	public void readJSONinstantiate(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "instantiate");
	}
		
	public void readJSONiscompensation(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "iscompensation");
	}
	
	public void readJSONloopcondition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "loopcondition");
	}
	
	public void readJSONloopcounter(JSONObject modelElement) throws JSONException {
		initializeLoop();
		
		JSONObject loopPassObject = new JSONObject();
		loopPassObject.put("loopcounter", modelElement.optString("loopcounter"));
		loopPassObject.put("looptype", modelElement.optString("looptype"));
		
		getLoop().parse(loopPassObject);
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
	
	public void readJSONprocessref(JSONObject modelElement) {
		createExtendedAttribute("processref", modelElement.optString("processref"));
	}
	
	public void readJSONstartquantity(JSONObject modelElement) {
		setStartQuantity(modelElement.optString("startquantity"));
	}
	
	public void readJSONsignalref(JSONObject modelElement) {
		createExtendedAttribute("signalref", modelElement.optString("signalref"));
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
		createExtendedAttribute("taskref", modelElement.optString("taskref"));
	}
	
	public void readJSONtesttime(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "testtime");
	}
	
	public void readJSONxortype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "xortype");
	}
	
	public void setCompletionQuantity(String quantity) {
		completionQuantity = quantity;
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
	
	protected void passInformationToLoop(JSONObject modelElement, String key) throws JSONException {
		initializeLoop();
		
		JSONObject loopPassObject = new JSONObject();
		loopPassObject.put(key, modelElement.optString(key));
		
		getLoop().parse(loopPassObject);
	}
	
	protected void passInformationToRoute(JSONObject modelElement, String key) throws JSONException {
		initializeRoute();
		
		JSONObject routePassObject = new JSONObject();
		routePassObject.put(key, modelElement.optString(key));

		getRoute().parse(routePassObject);
	}
}
