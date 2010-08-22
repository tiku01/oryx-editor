package de.hpi.processLogGeneration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import de.hpi.petrinet.Transition;


class Trace {
	private List<TraceEntry> steps = new LinkedList<TraceEntry>();
	private ProcessLog log;
	
	//constants for serialization:
	private final String traceOpenTagBegin = "<ProcessInstance id=\"";
	private final String traceOpenTagEnd = "\">";
	//has to be splitted in two parts because of a variable parameter
	private final String traceCloseTag = "</ProcessInstance>";
	
	public Trace(ProcessLog log) {
		this.log = log;
	}
	
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
	
	public boolean containsDirectSuccession(Transition first, Transition second) {
		for (int i = 0; i < steps.size() - 1; ++i) {
			if (steps.get(i).getTransition().equals(first) &&
					steps.get(i + 1).getTransition().equals(second)) {
				return true;
			}
		}
		return false;
	}
	
	public TraceEntry getLastStep() {
		if (steps.isEmpty()) return null;
		else return steps.get(steps.size() - 1);
	}
	
	public Trace duplicate() {
		Trace duplicate = new Trace(this);
		log.addTrace(duplicate);
		return duplicate;
	}
	
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(traceOpenTagBegin + UUID.randomUUID() + traceOpenTagEnd);
		for (TraceEntry entry : steps) {
			builder.append(entry.serialize());
		}
		builder.append(traceCloseTag);
		return builder.toString();
	}
	
	public int size() {
		return steps.size();
	}
	
	public void remove() {
		log.remove(this);
	}
	
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
	
	private boolean random(int degreeOfNoise) {
		return Math.random() * 100 <= degreeOfNoise;
	}
	
	private void removeHead(int degreeOfNoise) {
		int toBeRemovedCount = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < toBeRemovedCount; ++i) {
			steps.remove(0);
		}
	}
	
	private int chooseCountExponentially(int degreeOfNoise) {
		return (int)Math.round(Math.pow(steps.size(),
				Math.random() * degreeOfNoise / 100));
	}
	
	private void removeTail(int degreeOfNoise) {
		int toBeRemovedCount = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < toBeRemovedCount; ++i) {
			steps.remove(steps.size() - 1);
		}
	}
	
	private void removeRandomElements(int degreeOfNoise) {
		int numberOfRemoves = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < numberOfRemoves; ++i) {
			steps.remove((int)Math.floor(Math.random() * steps.size()));
		}
	}

	private void duplicate(int degreeOfNoise) {
		int numberOfDuplicates = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < numberOfDuplicates; ++i) {
			int chosenElement = (int)Math.floor(Math.random() * steps.size());
			steps.add(chosenElement, steps.get(chosenElement).copy());
		}
	}
	
	private void swapSteps(int degreeOfNoise) {
		int numberOfSwaps = chooseCountExponentially(degreeOfNoise);
		for (int i = 0; i < numberOfSwaps; ++i) {
			int firstSwapee = (int)Math.floor(Math.random() * steps.size());
			int secondSwapee = (int)Math.floor((steps.size() - 1) *
										Math.pow(Math.random() * 2 -1, 1.0 / 3));
			if (secondSwapee >= firstSwapee) ++ secondSwapee;
			TraceEntry firstEntry = steps.get(firstSwapee);
			steps.set(firstSwapee, steps.get(secondSwapee));
			steps.set(secondSwapee, firstEntry);
		}
	}
	
	public int getPropability() {
		int propability = 100;
		for (TraceEntry entry : steps) {
			propability *= entry.getPropability();
		}
		return propability;
	}
	
	public void calculateTimes(Date startDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		for (TraceEntry entry : steps) {
			entry.setTimestamp(calendar.getTime());
			calendar.add(Calendar.SECOND, entry.getExecutionTime());
		}
	}
}
