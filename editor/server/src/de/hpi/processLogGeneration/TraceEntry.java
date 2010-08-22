package de.hpi.processLogGeneration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Transition;
import de.hpi.processLogGeneration.petrinetTimeAndPropability.TransitionWithPropability;

public class TraceEntry {
	private LabeledTransition transition;
	private Date timestamp;
	
	//constants for serialization:
	private final String entryOpenTag = "<AuditTrailEntry>";
	private final String entryCloseTag = "</AuditTrailEntry>";
	private final String transitionOpenTag = "<WorkflowModelElement>";
	private final String transitionCloseTag = "</WorkflowModelElement>";
	private final String timestampOpenTag = "<Timestamp>";
	private final String timestampCloseTag = "</Timestamp>";
	private final String eventType = "<EventType unknowntype=\"Task_Execution\">unknown</EventType>";
	
	public TraceEntry(LabeledTransition transition, Date timestamp) {
		this.transition = transition;
		this.timestamp = timestamp;
	}
	
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		builder.append(entryOpenTag);
		serializeTransition(builder);
		serializeTimestamp(builder);
		builder.append(eventType);
		builder.append(entryCloseTag);
		return builder.toString();
	}
	
	public void serializeTransition(StringBuilder builder) {
		builder.append(transitionOpenTag);
		builder.append(transition.getLabel());
		builder.append(transitionCloseTag);
	}
	
	public void serializeTimestamp(StringBuilder builder) {
		builder.append(timestampOpenTag);
		//2008-10-13T20:51:42.000+00:00
		builder.append(
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).
				format(timestamp));
		builder.append(timestampCloseTag);
	}
	
	public TraceEntry copy() {
		return new TraceEntry(transition, timestamp);
	}
	
	public double getPropability() {
		if(transition instanceof TransitionWithPropability &&
				((TransitionWithPropability)transition).getPropability() != -1) {
			return ((TransitionWithPropability)transition).getPropability();
		} else {
			return 1;
		}
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getExecutionTime() {
		if (transition instanceof de.hpi.processLogGeneration.petrinetTimeAndPropability.LabeledTransition) {
			return ((de.hpi.processLogGeneration.petrinetTimeAndPropability.
					LabeledTransition)transition).getTime();
		} else {
			return 0;
		}
	}
	
	public Transition getTransition() {
		return transition;
	}
}
