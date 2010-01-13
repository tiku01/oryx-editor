package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLWorkflowProcess extends XPDLThing {
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:WorkflowProcess", XPDLWorkflowProcess.class);
	}
}
