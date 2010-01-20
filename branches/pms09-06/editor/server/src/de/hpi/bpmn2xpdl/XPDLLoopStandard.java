package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLLoopStandard extends XMLConvertable {

	protected String loopCondition;
	protected String loopCounter;
	protected String loopMaximum;
	protected String testTime;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:LoopStandard", XPDLLoopStandard.class);
		
		xstream.useAttributeFor(XPDLLoopStandard.class, "loopCondition");
		xstream.aliasField("LoopCondition", XPDLLoopStandard.class, "loopCondition");
		xstream.useAttributeFor(XPDLLoopStandard.class, "loopCounter");
		xstream.aliasField("LoopCounter", XPDLLoopStandard.class, "loopCounter");
		xstream.useAttributeFor(XPDLLoopStandard.class, "loopMaximum");
		xstream.aliasField("LoopMaximum", XPDLLoopStandard.class, "loopMaximum");
		xstream.useAttributeFor(XPDLLoopStandard.class, "testTime");
		xstream.aliasField("TestTime", XPDLLoopStandard.class, "testTime");
	}
	
	public String getLoopCondition() {
		return loopCondition;
	}
	
	public String getLoopCounter() {
		return loopCounter;
	}
	
	public String getLoopMaximum() {
		return loopMaximum;
	}
	
	public String getTestTime() {
		return testTime;
	}
	
	public void readJSONloopcondition(JSONObject modelElement) {
		setLoopCondition(modelElement.optString("loopcondition"));
	}
	
	public void readJSONloopcounter(JSONObject modelElement) {
		setLoopCounter(modelElement.optString("loopcounter"));
	}
	
	public void readJSONloopmaximum(JSONObject modelElement) {
		setLoopMaximum(modelElement.optString("loopmaximum"));
	}
	
	public void readJSONtesttime(JSONObject modelElement) {
		setTestTime(modelElement.optString("testtime"));
	}
	
	public void setLoopCondition(String loopCondition) {
		this.loopCondition = loopCondition;
	}
	
	public void setLoopCounter(String loopCounter) {
		this.loopCounter = loopCounter;
	}
	
	public void setLoopMaximum(String loopMaximum) {
		this.loopMaximum = loopMaximum;
	}
	
	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}	
}
