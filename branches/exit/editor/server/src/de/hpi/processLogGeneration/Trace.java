package de.hpi.processLogGeneration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import de.hpi.petrinet.Transition;

/**
 * Represents a Trace, i.e. the list of events of one ProcessInstance.
 * @author Thomas Milde
 * */
class Trace {
	/**
	 * the steps, of which this trace consists.
	 * */
	private List<TraceEntry> steps = new LinkedList<TraceEntry>();
	
	/**
	 * The ProcessLog, which this trace belongs to
	 * */
	private ProcessLog log;
	
	//constants for serialization:
	private final String traceOpenTagBegin = "<ProcessInstance id=\"";
	private final String traceOpenTagEnd = "\">";
	//has to be splitted in two parts because of a variable parameter
	private final String traceCloseTag = "</ProcessInstance>";
	
	/**
	 * Creates a new Trace
	 * 
	 * @param log The ProcessLog, which this trace belongs to
	 * */
	public Trace(ProcessLog log) {
		this.log = log;
	}
	
	/**
	 * Creates a copy of another trace
	 * 
	 * @param copyFrom the original, from which to copy all attributes.
	 * */
	public Trace(Trace copyFrom) {
		this(copyFrom.getLog());
		for(TraceEntry step : copyFrom.getSteps()) {
			this.steps.add(step.copy());
		}
	}
	
	public void addStep(TraceEntry transition) {
		steps.add(transition);
	}
	
	public List<TraceEntry> getSteps() {
		return steps;
	}
	
	public ProcessLog getLog() {
		return log;
	}
	
	/**
	 * checks, whether in this trace the transition second fires in direct 
	 * succession to the transition first.
	 * */
	public boolean containsDirectSuccession(Transition first, Transition second) {
		for (int i = 0; i < steps.size() - 1; ++i) {
			if (steps.get(i).getTransition().equals(first) &&
					steps.get(i + 1).getTransition().equals(second)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns the last entry of this trace.
	 * */
	public TraceEntry getLastStep() {
		if (steps.isEmpty()) return null;
		else return steps.get(steps.size() - 1);
	}
	
	/**
	 * Duplicates this trace, i.e. creates a copy and adds it to the log, which
	 * this trace belongs to.
	 * 
	 * @return the created copy
	 * */
	public Trace duplicate() {
		Trace duplicate = new Trace(this);
		log.addTrace(duplicate);
		return duplicate;
	}
	
	/**
	 * serializes this Trace to MXML-format.
	 * */
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(traceOpenTagBegin + UUID.randomUUID() + traceOpenTagEnd);
		for (TraceEntry entry : steps) {
			builder.append(entry.serialize());
		}
		builder.append(traceCloseTag);
		return builder.toString();
	}
	
	/**
	 * @return the number of steps in this trace.
	 * */
	public int size() {
		return steps.size();
	}
	
	/**
	 * removes this trace from the log it belongs to.
	 * */
	public void remove() {
		log.remove(this);
	}
	
	/**
	 * generates the desired amount of noise
	 * @param degreeOfNoise percentage of noise, from 0 to 100
	 * */
	public void generateNoise(int degreeOfNoise) {
		if (random(degreeOfNoise)) {
			removeHead(degreeOfNoise);
		}
		if (random(degreeOfNoise)) {
			removeTail(degreeOfNoise);
		}
		if (random(degreeOfNoise)) {
			removeRandomElements(degreeOfNoise);
		}
		if (random(degreeOfNoise)) {
			duplicate(degreeOfNoise);
		}
		if (steps.size() > 1 && random(degreeOfNoise)) {
			swapSteps(degreeOfNoise);
		}
	}
	
	/**
	 * returns true with a probability of degreeOfNoise %
	 * */
	private boolean random(int degreeOfNoise) {
		return Math.random() * 100 <= degreeOfNoise;
	}
	
	/**
	 * removes the head of this trace (at least one step)
	 * */
	private void removeHead(int degreeOfNoise) {
		int toBeRemovedCount = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < toBeRemovedCount; ++i) {
			steps.remove(0);
		}
	}
	
	/**
	 * returns a random-number from 1 to the size of this trace powered by
	 * degreeOfNoise % with an exponential distribution
	 * */
	private int chooseCountExponentially(int degreeOfNoise) {
		return (int)Math.round(Math.pow(steps.size(),
				Math.random() * degreeOfNoise / 100));
	}
	
	/**
	 * removes some elements from the tail of this trace (remvoes at least one step)
	 * */
	private void removeTail(int degreeOfNoise) {
		int toBeRemovedCount = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < toBeRemovedCount; ++i) {
			steps.remove(steps.size() - 1);
		}
	}
	
	/**
	 * removes randomly chosen elements from this trace.
	 * */
	private void removeRandomElements(int degreeOfNoise) {
		int numberOfRemoves = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < numberOfRemoves; ++i) {
			steps.remove((int)Math.floor(Math.random() * steps.size()));
		}
	}

	/**
	 * duplicates randomly chosen elements in this trace.
	 * */
	private void duplicate(int degreeOfNoise) {
		int numberOfDuplicates = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < numberOfDuplicates; ++i) {
			int chosenElement = (int)Math.floor(Math.random() * steps.size());
			steps.add(chosenElement, steps.get(chosenElement).copy());
		}
	}
	
	/**
	 * exchanges two randomly chosen steps of this trace. It is more likely, that
	 * two elements, which are close to each other get exchanged, than that 
	 * two elements, which are far away from each other get exchanged.
	 * */
	private void swapSteps(int degreeOfNoise) {
		int numberOfSwaps = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < numberOfSwaps; ++i) {
			int firstSwapee = (int)Math.floor(Math.random() * steps.size());
			int secondSwapee = (int)Math.floor((steps.size() - 1) *
										Math.pow(Math.random() * 2 -1, 1.0 / 3));
			//uses a third-root-function to determine the second partner of the swap
			//this ensures, that it is more likely to exchange steps close to each other
			if (secondSwapee >= firstSwapee) ++ secondSwapee;
			TraceEntry firstEntry = steps.get(firstSwapee);
			steps.set(firstSwapee, steps.get(secondSwapee));
			steps.set(secondSwapee, firstEntry);
		}
	}
	
	/**
	 * calculates the probability of this trace (i.e. the product of the 
	 * probabilities of it's steps) 
	 * */
	public int getPropability() {
		int propability = 100;
		for (TraceEntry entry : steps) {
			propability *= entry.getPropability();
		}
		return propability;
	}
	
	/**
	 * creates timestamps for the steps.
	 * @param startDate the time, when this instance starts.
	 * */
	public void calculateTimes(Date startDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		for (TraceEntry entry : steps) {
			entry.setTimestamp(calendar.getTime());
			calendar.add(Calendar.SECOND, entry.getExecutionTime());
		}
	}
}
