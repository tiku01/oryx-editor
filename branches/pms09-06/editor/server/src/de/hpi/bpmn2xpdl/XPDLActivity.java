package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLActivity extends XPDLThingNodeGraphics {

	protected String completionQuantity;
	protected String finishMode;
	protected String isATransaction;
	protected String isForCompensation;
	protected String startActivity;
	protected String startMode;
	protected String startQuantity;
	
	protected XPDLRoute route;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"Complex_Gateway",
				"OR_Gateway",
				"AND_Gateway",
				"Exclusive_Eventbased_Gateway",
				"Exclusive_Databased_Gateway",
				"Task"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Actitivity", XPDLActivity.class);
		
		xstream.useAttributeFor(XPDLActivity.class, "completionQuantity");
		xstream.aliasField("CompletionQuantity", XPDLAssociation.class, "completionQuantity");
		xstream.useAttributeFor(XPDLActivity.class, "finishMode");
		xstream.aliasField("FinishMode", XPDLAssociation.class, "finishMode");
		xstream.useAttributeFor(XPDLActivity.class, "isATransaction");
		xstream.aliasField("IsATransaction", XPDLAssociation.class, "isATransaction");
		xstream.useAttributeFor(XPDLActivity.class, "isForCompensation");
		xstream.aliasField("IsForCompensation", XPDLAssociation.class, "isForCompensation");
		xstream.useAttributeFor(XPDLActivity.class, "startActivity");
		xstream.aliasField("StartActivity", XPDLAssociation.class, "startActivity");
		xstream.useAttributeFor(XPDLActivity.class, "startMode");
		xstream.aliasField("StartMode", XPDLAssociation.class, "startMode");
		xstream.useAttributeFor(XPDLActivity.class, "startQuantity");
		xstream.aliasField("StartQuantity", XPDLAssociation.class, "startQuantity");
	}
	
	public String getCompletionQuantity() {
		return completionQuantity;
	}
	
	public String getFinishMode() {
		return finishMode;
	}
	
	public String getIsATransaction() {
		return isATransaction;
	}
	
	public String getIsForCompensation() {
		return isForCompensation;
	}
	
	public XPDLRoute getRoute() {
		return route;
	}
	
	public String getStartActivity() {
		return startActivity;
	}
	
	public String getStartMode() {
		return startMode;
	}
	
	public String getStartQuantity() {
		return startQuantity;
	}
	
	public void readJSONcompletionquantity(JSONObject modelElement) {
		setCompletionQuantity(modelElement.optString("completionquantity"));
	}
	
	public void readJSONgatewaytype(JSONObject modelElement) throws JSONException {
		initializeRoute();
		
		JSONObject routePassObject = new JSONObject();
		routePassObject.put("gatewaytype", modelElement.optString("gatewaytype"));

		getRoute().parse(routePassObject);
	}
	
	public void readJSONincomingcondition(JSONObject modelElement) throws JSONException {
		initializeRoute();
		
		JSONObject routePassObject = new JSONObject();
		routePassObject.put("incomingcondition", modelElement.optString("incomingcondition"));

		getRoute().parse(routePassObject);
	}
	
	public void readJSONinstantiate(JSONObject modelElement) throws JSONException {
		initializeRoute();

		JSONObject routePassObject = new JSONObject();
		routePassObject.put("instantiate", modelElement.optString("instantiate"));

		getRoute().parse(routePassObject);
	}
		
	public void readJSONiscompensation(JSONObject modelElement) throws JSONException {
		initializeRoute();

		JSONObject routePassObject = new JSONObject();
		routePassObject.put("iscompensation", modelElement.optString("iscompensation"));

		getRoute().parse(routePassObject);
	}
	
	public void readJSONmarkervisible(JSONObject modelElement) throws JSONException {
		initializeRoute();

		JSONObject routePassObject = new JSONObject();
		routePassObject.put("markervisible", modelElement.optString("markervisible"));

		getRoute().parse(routePassObject);
	}
	
	public void readJSONoutgoingcondition(JSONObject modelElement) throws JSONException {
		initializeRoute();

		JSONObject routePassObject = new JSONObject();
		routePassObject.put("outgoingcondition", modelElement.optString("outgoingcondition"));

		getRoute().parse(routePassObject);
	}
	
	public void readJSONstartquantity(JSONObject modelElement) {
		setStartQuantity(modelElement.optString("startquantity"));
	}
	
	public void readJSONxortype(JSONObject modelElement) throws JSONException {
		initializeRoute();
		
		JSONObject routePassObject = new JSONObject();
		routePassObject.put("xortype", modelElement.optString("xortype"));

		getRoute().parse(routePassObject);
	}
	
	public void setCompletionQuantity(String quantity) {
		completionQuantity = quantity;
	}
	
	public void setFinishMode(String mode) {
		finishMode = mode;
	}
	
	public void setIsATransaction(String status) {
		isATransaction = status;
	}
	
	public void setIsForCompensation(String status) {
		isForCompensation = status;
	}
	
	public void setRoute(XPDLRoute routeValue) {
		route = routeValue;
	}
	
	public void setStartActivity(String isStart) {
		startActivity = isStart;
	}
	
	public void setStartMode(String mode) {
		startMode = mode;
	}
	
	public void setStartQuantity(String quantity) {
		startQuantity = quantity;
	}
	
	protected void initializeRoute() {
		if (getRoute() == null) {
			setRoute(new XPDLRoute());
		}
	}
}
