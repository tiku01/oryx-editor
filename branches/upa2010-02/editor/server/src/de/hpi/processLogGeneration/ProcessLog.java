package de.hpi.processLogGeneration;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import de.hpi.petrinet.Transition;

/**
 * Represents a ProcessLog, i.e. a collection of multiple traces.
 * 
 * @author Thomas Milde
 * */
public class ProcessLog {
	/**
	 * the traces, which this log consists of.
	 * */
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
	
	/**
	 * @return a new trace, that already has been added to this log.
	 * */
	public Trace newTrace() {
		Trace trace = new Trace(this);
		traces.add(trace);
		return trace;
	}
	
	/**
	 * serializes this log to MXML-format
	 * */
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
	
	/**
	 * removes a trace from this log.
	 * */
	public void remove(Trace trace) {
		traces.remove(trace);
	}
	
	/**
	 * generates noise of the desired degree.
	 * 
	 * @param degreeOfNoise a percentage from 0 to 100 
	 * */
	public void generateNoise(int degreeOfNoise) {
		for(Trace trace : traces) {
			trace.generateNoise(degreeOfNoise);
		}
	}
	
	/**
	 * duplicates a number of traces, so that this log contains at least the
	 * desired number of traces. The traces, which should be duplicated will be
	 * chosen according to their probabilities.
	 * */
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
	
	/**
	 * calculates timestamps for the traces, such that every minute one trace 
	 * starts, beginning at the current time.
	 * */
	public void calculateTimes() {
		Calendar calendar = new GregorianCalendar();
		for (Trace trace : traces) {
			trace.calculateTimes(calendar.getTime());
			calendar.add(Calendar.MINUTE, 1);
		}
	}
	
	/**
	 * checks, whether there is at least one trace, in which second directly
	 * succeeds first.
	 * */
	public boolean containsDirectSuccession(Transition first, Transition second) {
		for (Trace trace : traces) {
			if (trace.containsDirectSuccession(first, second)) {
				return true;
			}
		}
		return false;
	}
}
