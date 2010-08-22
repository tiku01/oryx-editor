package de.hpi.processLogGeneration;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import de.hpi.petrinet.Transition;

public class ProcessLog {
	Collection<Trace> traces = new HashSet<Trace>();
	
	//constants for serialization:
	private final String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	private final String workflowOpenTag = "<WorkflowLog xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://is.tm.tue.nl/research/processmining/WorkflowLog.xsd\">";
	private final String workflowCloseTag = "</WorkflowLog>";
	private final String processOpenTag = "<Process id=\"1\">";
	private final String processCloseTag = "</Process>";

	public void addTrace(Trace trace) {
		traces.add(trace);
	}
	
	public Trace newTrace() {
		Trace trace = new Trace(this);
		traces.add(trace);
		return trace;
	}
	
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		builder.append(xmlDeclaration);
		builder.append(workflowOpenTag);
		builder.append(processOpenTag);
		for(Trace trace : traces) {
			builder.append(trace.serialize());
		}
		builder.append(processCloseTag);
		builder.append(workflowCloseTag);
		return builder.toString();
	}
	
	public void remove(Trace trace) {
		traces.remove(trace);
	}
	
	public void generateNoise(int degreeOfNoise) {
		for(Trace trace : traces) {
			trace.generateNoise(degreeOfNoise);
		}
	}
	
	public void duplicateToMinimumTraceCount(int traceCount) {
		Map<Trace, Double> propabilities = new HashMap<Trace, Double>();
		int propabilitySum = 0;
		for (Trace trace : traces) {
			int propability = trace.getPropability();
			propabilities.put(trace, (double)propability);
			propabilitySum += propability;
		}
		for (Map.Entry<Trace, Double> entry : propabilities.entrySet()) {
			for (int i = 1; i <
					Math.round(entry.getValue() / propabilitySum * traceCount);
					++i) {
				entry.getKey().duplicate();
			}
		}
	}
	
	public void calculateTimes() {
		Calendar calendar = new GregorianCalendar();
		for (Trace trace : traces) {
			trace.calculateTimes(calendar.getTime());
			calendar.add(Calendar.MINUTE, 1);
		}
	}
	
	public boolean containsDirectSuccession(Transition first, Transition second) {
		for (Trace trace : traces) {
			if (trace.containsDirectSuccession(first, second)) {
				return true;
			}
		}
		return false;
	}
}
