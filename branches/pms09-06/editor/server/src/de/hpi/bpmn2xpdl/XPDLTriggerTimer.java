package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;

public class XPDLTriggerTimer extends XMLConvertable {
	
	@Attribute("TimerCycle")
	protected String timerCycle;
	@Attribute("TimerDate")
	protected String timerDate;
	
	public String getTimerCycle() {
		return timerCycle;
	}
	
	public String getTimerDate() {
		return timerDate;
	}
	
	public void readJSONtimecycle(JSONObject modelElement) {
		setTimerCycle(modelElement.optString("timecycle"));
	}
	
	public void readJSONtimedate(JSONObject modelElement) {
		setTimerDate(modelElement.optString("timedate"));
	}
	
	public void setTimerCycle(String timerCycle) {
		this.timerCycle = timerCycle;
	}
	
	public void setTimerDate(String timerDate) {
		this.timerDate = timerDate;
	}

}
