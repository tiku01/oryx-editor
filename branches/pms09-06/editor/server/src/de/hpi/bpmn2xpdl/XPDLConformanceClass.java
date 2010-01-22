package de.hpi.bpmn2xpdl;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("ConformanceClass")
public class XPDLConformanceClass extends XMLConvertable {
	
	@Attribute("GraphConformance")
	protected String graphConformance = "NON-BLOCKED";
	@Attribute("BPMNModelPortabilityConformance")
	protected String bpmnConformance = "STANDARD";
	
	public String getBpmnConformance() {
		return bpmnConformance;
	}
	
	public String getGraphConformance() {
		return graphConformance;
	}
	
	public void setBpmnConformance(String conformance) {
		bpmnConformance = conformance;
	}
	
	public void setGraphConformance(String conformance) {
		graphConformance = conformance;
	}
}
