package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Assignments")
public class XPDLAssignments extends XMLConvertable {

	@Element("Assignment")
	protected ArrayList<XPDLAssignment> assignments;

	public void add(XPDLAssignment newAssignment) {
		initializeAssignments();
		
		getAssignments().add(newAssignment);
	}
	
	public ArrayList<XPDLAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(ArrayList<XPDLAssignment> assignments) {
		this.assignments = assignments;
	}
	
	protected void initializeAssignments() {
		if (getAssignments() == null) {
			setAssignments(new ArrayList<XPDLAssignment>());
		}
	}
}
