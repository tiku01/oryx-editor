package de.hpi.processLogGeneration;

import java.util.LinkedList;
import java.util.List;

import de.hpi.petrinet.Marking;

/**
 * represents a pair of a Petrinet-marking and a Trace and therefore is the
 * current state of creating a trace
 * */
class GenerationState {
	private Marking marking;
	private Trace trace;
	private int multiplicity;
	private List<Marking> occuredMarkings;
	
	public GenerationState(Marking marking, Trace trace) {
		this(marking, trace,1);
	}
	
	public GenerationState(Marking marking, Trace trace, int multiplicity) {
		this(marking, trace, multiplicity, new LinkedList<Marking>());
	}
	
	public GenerationState(Marking marking, Trace trace, int multiplicity,
			List<Marking> occuredMarkings) {
		this.marking = marking;
		this.trace = trace;
		this.multiplicity = multiplicity;
		this.occuredMarkings = occuredMarkings;
	}

	public Marking getMarking() {
		return marking;
	}

	public void setMarking(Marking marking) {
		this.marking = marking;
	}

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
	}
	
	public int getMultiplicity() {
		return multiplicity;
	}
	
	public void setMultiplicity(int multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	public void addOccuredMarking(Marking marking) {
		occuredMarkings.add(marking);
	}
	
	public int occurenceCount(Marking marking) {
		int count = 0;
		for (Marking occuredMarking : occuredMarkings) {
			if (marking.equals(occuredMarking)) ++count;
		}
		return count;
	}
	
	public void setOccuredMarkings(List<Marking> occuredMarkings) {
		this.occuredMarkings = occuredMarkings;
	}
	
	public List<Marking> getOccuredMarkings() {
		return new LinkedList<Marking>(occuredMarkings);
	}
}
