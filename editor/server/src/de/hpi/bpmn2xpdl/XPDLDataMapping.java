package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLDataMapping extends XMLConvertable {
	
	protected String formal;
	protected String direction;
	
	protected XPDLExpression actual;
	protected XPDLExpression testValue;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:DataMapping", XPDLDataMapping.class);
		
		xstream.useAttributeFor(XPDLDataMapping.class, "formal");
		xstream.aliasField("Formal", XPDLDataMapping.class, "formal");
		xstream.useAttributeFor(XPDLDataMapping.class, "direction");
		xstream.aliasField("Direction", XPDLDataMapping.class, "direction");
		
		xstream.aliasField("xpdl2:Actual", XPDLDataMapping.class, "actual");
		xstream.aliasField("xpdl2:TestValue", XPDLDataMapping.class, "testValue");
	}
	
	public String getFormal() {
		return formal;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public XPDLExpression getActual() {
		return actual;
	}
	
	public XPDLExpression getTestValue() {
		return testValue;
	}
	
	public void setFormal(String formalValue) {
		formal = formalValue;
	}
	
	public void setDirection(String directionValue) {
		direction = directionValue;
	}
	
	public void setActual(XPDLExpression actualValue) {
		actual = actualValue;
	}
	
	public void setTestValue(XPDLExpression test) {
		testValue = test;
	}
}