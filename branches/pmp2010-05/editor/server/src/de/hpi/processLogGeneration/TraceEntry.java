package de.hpi.processLogGeneration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Transition;
import de.hpi.processLogGeneration.petrinetTimeAndPropability.TransitionWithPropability;

/**
 * Represents a step of a process-instance.
 * 
 * @author Thomas Milde
 * */
public class TraceEntry {
	/**
	 * The transition, which has been fired in this step
	 * */
	private LabeledTransition transition;
	
	/**
	 * The time, when this step was started.
	 * */
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
	
	/**
	 * serializes this TraceEntry to MXML-format.
	 * */
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		builder.append(entryOpenTag);
		serializeTransition(builder);
		serializeTimestamp(builder);
		builder.append(eventType);
		builder.append(entryCloseTag);
		return builder.toString();
	}
	
	/**
	 * serializes the name of the transition
	 * @param builder the StringBuilder, on which to serialize the transition.
	 * */
	private void serializeTransition(StringBuilder builder) {
		builder.append(transitionOpenTag);
		builder.append(transition.getLabel());
		builder.append(transitionCloseTag);
	}
	
	/**
	 * serializes the timestamp of this TraceEntry to a StringBuilder
	 * */
	private void serializeTimestamp(StringBuilder builder) {
		builder.append(timestampOpenTag);
		//2008-10-13T20:51:42.000+00:00
		builder.append(
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).
				format(timestamp));
		builder.append(timestampCloseTag);
	}
	
	/**
	 * @return a copy of this TraceEntry.
	 * */
	public TraceEntry copy() {
		return new TraceEntry(transition, timestamp);
	}
	
	/**
	 * @return the probability of this TraceEntry's transition or 1, if no 
	 * probability is specified.
	 * */
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
	
	/**
	 * @return how long this step takes (in seconds)
	 * */
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
