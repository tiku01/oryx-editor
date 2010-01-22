package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("LoopStandard")
public class XPDLLoopStandard extends XMLConvertable {

	@Attribute("LoopCondition")
	protected String loopCondition;
	@Attribute("LoopCounter")
	protected String loopCounter;
	@Attribute("LoopMaximum")
	protected String loopMaximum;
	@Attribute("TestTime")
	protected String testTime;
	
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
