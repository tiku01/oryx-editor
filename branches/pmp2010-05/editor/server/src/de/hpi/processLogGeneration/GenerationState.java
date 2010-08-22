package de.hpi.processLogGeneration;

import java.util.LinkedList;
import java.util.List;

import de.hpi.petrinet.Marking;

/**
 * represents a state in generating a Process-Log
 * 
 * @author Thomas Milde
 * */
class GenerationState {
	/**
	 * The current marking
	 * */
	private Marking marking;
	
	/**
	 * The trace, that is being generated.
	 * */
	private Trace trace;
	
	/**
	 * The multiplicity of the trace (i.e. the number of traces it actually 
	 * represents)
	 * */
	private int multiplicity;
	
	/**
	 * The history of this state, i.e. the markings, that occured on the way from
	 * the beginning to the current marking.
	 * */
	private List<Marking> occuredMarkings;
	
	/**
	 * Creates a new instance of GenerationState.
	 * 
	 * @param marking The current marking
	 * @param trace The trace, that is being generated.
	 * */
	public GenerationState(Marking marking, Trace trace) {
		this(marking, trace,1);
	}
	
	/**
	 * Creates a new instance of GenerationState.
	 * 
	 * @param marking The current marking
	 * @param trace The trace, that is being generated.
	 * @param multiplicity The multiplicity of the trace (i.e. the number of 
	 * traces it actually represents)
	 * */
	public GenerationState(Marking marking, Trace trace, int multiplicity) {
		this(marking, trace, multiplicity, new LinkedList<Marking>());
	}
	
	/**
	 * Creates a new instance of GenerationState.
	 * 
	 * @param marking The current marking
	 * @param trace The trace, that is being generated.
	 * @param multiplicity The multiplicity of the trace (i.e. the number of 
	 * traces it actually represents)
	 * @param occuredMarkings The history of this state, i.e. the markings, that
	 * occured on the way from the beginning to the current marking.
	 * */
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
	
	/**
	 * counts, how often a marking occured in the history of this state.
	 * 
	 * @param marking the marking, which's occurences should be counted
	 * @return the number of occurences of marking in occuredMarkings
	 * */
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
