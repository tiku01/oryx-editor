package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLTimeEstimation extends XMLConvertable {
	
	protected String waitingTime;
	protected String workingTime;
	protected String duration;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:TimeEstimation", XPDLTimeEstimation.class);
		
		xstream.aliasField("xpdl2:WaitingTime", XPDLTimeEstimation.class, "waitingTime");
		xstream.aliasField("xpdl2:WorkingTime", XPDLTimeEstimation.class, "workingTime");
		xstream.aliasField("xpdl2:Duration", XPDLTimeEstimation.class, "duration");
	}
	
	public String getWaitingTime() {
		return waitingTime;
	}
	public String getWorkingTime() {
		return workingTime;
	}
	public String getDuration() {
		return duration;
	}
	public void setWaitingTime(String waitingTime) {
		this.waitingTime = waitingTime;
	}
	public void setWorkingTime(String workingTime) {
		this.workingTime = workingTime;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
}
