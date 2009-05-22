package de.hpi.bpmn;

public class Assignment {

	public enum AssignTime {
		Start, End
	}
	
	//"to" should be of type Property instead of String, 
	//but the editor does not support the selection of defined properties
	private String to = "";
	private String from = "";
	private AssignTime assignTime = AssignTime.Start;
	
	public Assignment(String newTo, String newFrom, AssignTime newAssignTime){
		setTo(newTo);
		setFrom(newFrom);
		setAssignTime(newAssignTime);
	}
	
	public void setTo(String newTo){
		to = newTo;
	}
	
	public String getTo(){
		return to;
	}
	
	public void setFrom(String newFrom){
		from = newFrom;
	}
	
	public String getFrom(){
		return from;
	}
	
	public void setAssignTime(AssignTime newAssignTime){
		assignTime = newAssignTime;
	}
	
	public AssignTime getAssignTime(){
		return assignTime;
	}
}
