package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLDataField extends XPDLThing {

	protected String correlation;
	protected XPDLExpression initialValue;
	protected String isArray;
	protected String isReadOnly;
	protected String length;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:DataField", XPDLDataField.class);
		
		xstream.useAttributeFor(XPDLDataField.class, "correlation");
		xstream.aliasField("Correlation", XPDLDataField.class, "correlation");
		xstream.useAttributeFor(XPDLDataField.class, "isArray");
		xstream.aliasField("IsArray", XPDLDataField.class, "isArray");
		xstream.useAttributeFor(XPDLDataField.class, "isReadOnly");
		xstream.aliasField("IsReadOnly", XPDLDataField.class, "isReadOnly");
		
		xstream.aliasField("xpdl2:InitialValue", XPDLDataField.class, "initialValue");
		xstream.aliasField("xpdl2:Length", XPDLDataField.class, "length");
	}
	
	public String getCorrelation() {
		return correlation;
	}
	
	public XPDLExpression getInitialValue() {
		return initialValue;
	}
	
	public String getIsArray() {
		return isArray;
	}
	
	public String getIsReadOnly() {
		return isReadOnly;
	}
	
	public void setCorrelation(String correlationValue) {
		correlation = correlationValue;
	}
	
	public void setInitialValue(XPDLExpression value) {
		initialValue = value;
	}
	
	public void setIsArray(String isArrayValue) {
		isArray = isArrayValue;
	}
	
	public void setIsReadOnly(String readOnly) {
		isReadOnly = readOnly;
	}
}
