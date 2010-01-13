package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLConformanceClass extends XMLConvertable {
	
	protected String graphConformance = "NON-BLOCKED";
	protected String bpmnConformance = "STANDARD";
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ConformanceClass", XPDLConformanceClass.class);

		xstream.useAttributeFor(XPDLConformanceClass.class, "graphConformance");
		xstream.aliasField("GraphConformance", XPDLConformanceClass.class, "graphConformance");
		xstream.useAttributeFor(XPDLConformanceClass.class, "bpmnConformance");
		xstream.aliasField("BPMNModelPortabilityConformance", XPDLConformanceClass.class, "bpmnConformance");
	}
	
	public String getBPMNConformance() {
		return bpmnConformance;
	}
	
	public String getGraphConformance() {
		return graphConformance;
	}
}
