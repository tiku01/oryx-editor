package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("WorkflowProcesses")
public class XPDLWorkflowProcesses extends XMLConvertable {

	@Element("WorkflowProcess")
	protected ArrayList<XPDLWorkflowProcess> workflowProcesses;

	public void add(XPDLWorkflowProcess newProcess) {
		initializeWorkflowProcesses();
		
		getWorkflowProcesses().add(newProcess);
	}
	
	public ArrayList<XPDLWorkflowProcess> getWorkflowProcesses() {
		return workflowProcesses;
	}

	public void setWorkflowProcesses(ArrayList<XPDLWorkflowProcess> newProcess) {
		this.workflowProcesses = newProcess;
	}
	
	protected void initializeWorkflowProcesses() {
		if (getWorkflowProcesses() == null) {
			setWorkflowProcesses(new ArrayList<XPDLWorkflowProcess>());
		}
	}
}
