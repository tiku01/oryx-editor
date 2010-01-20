package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLMultiInstance extends XMLConvertable {
	
	protected String mi_condition;
	protected String loopCounter;
	protected String mi_ordering;
	protected String mi_flowCondition;
	protected String complexMi_flowCondition;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:MultiInstance", XPDLMultiInstance.class);
		
		xstream.useAttributeFor(XPDLMultiInstance.class, "mi_condition");
		xstream.aliasField("MI_Condition", XPDLMultiInstance.class, "mi_condition");
		xstream.useAttributeFor(XPDLMultiInstance.class, "loopCounter");
		xstream.aliasField("LoopCounter", XPDLMultiInstance.class, "loopCounter");
		xstream.useAttributeFor(XPDLMultiInstance.class, "mi_ordering");
		xstream.aliasField("MI_Ordering", XPDLMultiInstance.class, "mi_ordering");
		xstream.useAttributeFor(XPDLMultiInstance.class, "mi_flowCondition");
		xstream.aliasField("MI_FlowCondition", XPDLMultiInstance.class, "mi_flowCondition");
		xstream.useAttributeFor(XPDLMultiInstance.class, "complexMi_flowCondition");
		xstream.aliasField("ComplexMI_FlowCondition", XPDLMultiInstance.class, "complexMi_flowCondition");
	}
	
	public String getMi_condition() {
		return mi_condition;
	}
	
	public String getLoopCounter() {
		return loopCounter;
	}
	
	public String getMi_ordering() {
		return mi_ordering;
	}
	
	public String getMi_flowCondition() {
		return mi_flowCondition;
	}
	
	public String getComplexMi_flowCondition() {
		return complexMi_flowCondition;
	}
	
	public void readJSONcomplex_micondition(JSONObject modelElement) {
		setComplexMi_flowCondition(modelElement.optString("complex_micondition"));
	}
	
	public void readJSONloopcounter(JSONObject modelElement) {
		setLoopCounter(modelElement.optString("loopcounter"));
	}
	
	public void readJSONmi_condition(JSONObject modelElement) {
		setMi_condition(modelElement.optString("mi_condition"));
	}
	
	public void readJSONmi_flowcondition(JSONObject modelElement) {
		setMi_flowCondition(modelElement.optString("mi_flowcondition"));
	}
	
	public void readJSONmi_ordering(JSONObject modelElement) {
		setMi_ordering(modelElement.optString("mi_ordering"));
	}
	
	public void setMi_condition(String miCondition) {
		mi_condition = miCondition;
	}
	
	public void setLoopCounter(String loopCounter) {
		this.loopCounter = loopCounter;
	}
	
	public void setMi_ordering(String miOrdering) {
		mi_ordering = miOrdering;
	}
	
	public void setMi_flowCondition(String miFlowCondition) {
		mi_flowCondition = miFlowCondition;
	}
	
	public void setComplexMi_flowCondition(String complexMiFlowCondition) {
		complexMi_flowCondition = complexMiFlowCondition;
	}
}
