package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLCondition extends XMLConvertable {
	
	protected String conditionType;
	protected String conditionExpression;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Condition", XPDLCondition.class);
	}
	
	public String getConditionType() {
		return conditionType;
	}
	public String getConditionExpression() {
		return conditionExpression;
	}
	
	public void readJSONconditionexpression(JSONObject modelElement) {
		setConditionExpression(modelElement.optString("conditionexpression"));
	}
	
	public void readJSONconditiontype(JSONObject modelElement) {
		setConditionType(modelElement.optString("conditiontype"));
	}
	
	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}	
}
