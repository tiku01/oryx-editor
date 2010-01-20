package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLTransition extends XPDLThingConnectorGraphics {

	protected String from;
	protected String quantity;
	protected String to;
	
	protected XPDLCondition condition;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"SequenceFlow"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Transition", XPDLTransition.class);
		
		xstream.useAttributeFor(XPDLTransition.class, "from");
		xstream.aliasField("From", XPDLTransition.class, "from");
		xstream.useAttributeFor(XPDLTransition.class, "quantity");
		xstream.aliasField("Quantity", XPDLTransition.class, "quantity");
		xstream.useAttributeFor(XPDLTransition.class, "to");
		xstream.aliasField("To", XPDLTransition.class, "to");
	}
	
	public XPDLCondition getCondition() {
		return condition;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getQuantity() {
		return quantity;
	}
	
	public String getTo() {
		return to;
	}
	
	public void readJSONconditionexpression(JSONObject modelElement) throws JSONException {
		initializeCondition();
		
		JSONObject passCondition = new JSONObject();
		passCondition.put("conditionexpression", modelElement.optString("conditionexpression"));
		
		getCondition().parse(passCondition);
	}
	
	public void readJSONconditiontype(JSONObject modelElement) throws JSONException {
		initializeCondition();
		
		JSONObject passCondition = new JSONObject();
		passCondition.put("conditiontype", modelElement.optString("conditiontype"));
		
		getCondition().parse(passCondition);
	}
	
	public void readJSONquantity(JSONObject modelElement) {
		setQuantity(modelElement.optString("quantity"));
	}
	
	public void readJSONshowdiamondmarker(JSONObject modelElement) {
		createExtendedAttribute("showdiamondmarker", modelElement.optString("showdiamondmarker"));
	}
	
	public void readJSONsource(JSONObject modelElement) {
		setFrom(modelElement.optString("source"));	
	}
	
	public void readJSONsourceref(JSONObject modelElement) {
		createExtendedAttribute("sourceref", modelElement.optString("sourceref"));
	}
	
	public void readJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = modelElement.getJSONObject("target");
		setTo(target.optString("resourceId"));
	}
	
	public void readJSONtargetref(JSONObject modelElement) {
		createExtendedAttribute("targetref", modelElement.optString("targetref"));
	}
	
	public void setCondition(XPDLCondition conditionValue) {
		condition = conditionValue;
	}
	
	public void setFrom(String source) {
		from = source;
	}
	
	public void setQuantity(String quantityValue) {
		quantity = quantityValue;
	}
	
	public void setTo(String target) {
		to = target;
	}
	
	protected void initializeCondition() {
		if (getCondition() == null) {
			setCondition(new XPDLCondition());
		}
	}
}
