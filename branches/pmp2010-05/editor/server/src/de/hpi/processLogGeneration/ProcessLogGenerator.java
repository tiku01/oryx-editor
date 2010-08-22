package de.hpi.processLogGeneration;


import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;
import de.hpi.processLogGeneration.petrinetTimeAndPropability.TransitionWithPropability;

/**
 * @author Thomas Milde
 * Generates a ProcessLog in MXML-format from a Petrinet.
 * The following options can be chosen:
 * completeness: None (no completeness required), Ordering (if A may directly succeed
 * B, there must be a log, where A directly succeeds B), Trace (all possible traces
 * must be represented). Note, that Trace-completeness is not possible, if the 
 * net contains loops. Trace-completeness and Ordering-completenss may lead to
 * a very large set of traces
 * noise: the percentage (number from 0 to 100) of noise, which the log should
 * contain.
 * propabilities: should propabilities of edges be paid attention to?
 * */
public class ProcessLogGenerator {
	
	private PetriNet net;
	private CompletenessOption completeness;
	private int noise;
	private int traceCount;
	private final int maxTraceSize = 500;
	private Collection<GenerationState> activeStates;
	private final PTNetInterpreter interpreter = new PTNetInterpreter();
	
	
	public ProcessLogGenerator(PetriNet net, CompletenessOption completeness, int noise, int traceCount) {
		this.net = net;
		this.completeness = completeness;
		this.noise = noise;
		this.traceCount = traceCount;
	}

	public String getSerializedLog() {
		return generateLog().serialize();
	}
	
	public ProcessLog generateLog() {
		ProcessLog log = generateNoiseFreeLog();
		if(noise != 0) {
			log.generateNoise(noise);
		}
		log.calculateTimes();
		return log;
	}
	
	public ProcessLog generateNoiseFreeLog() {
		if(completeness.equals(CompletenessOption.Trace)){
			return generateTraceCompleteLog();
		} else if (completeness.equals(CompletenessOption.Ordering)) {
			return generateOrderingCompleteLog();
		} else {
			return generateNotCompleteLog();
		}
	}
	
	public ProcessLog generateTraceCompleteLog() {
		ProcessLog log = new ProcessLog();
		initializeActiveStates(log);
		
		while (!activeStates.isEmpty()) {
			LinkedList<GenerationState> oldStates = new LinkedList<GenerationState>();
			oldStates.addAll(activeStates);
			activeStates.clear();
			proceedAll(oldStates);
		}
		log.duplicateToMinimumTraceCount(traceCount);
		return log;
	}

	private void proceedAll(LinkedList<GenerationState> states) {
		for (GenerationState state : states) {
			if (state.getTrace().size() >= maxTraceSize ||
					state.occurenceCount(state.getMarking()) > 1) {
				state.getTrace().remove();
			} else {
				proceed(state);
			}
		}
	}

	private void proceed(GenerationState state) {
		List<Transition> enabledTransitions =
			interpreter.getEnabledTransitions(net, state.getMarking());
		if (! enabledTransitions.isEmpty()) {
			Trace copy = null;
			if(enabledTransitions.size() > 1) {
				copy = new Trace(state.getTrace());
				//need to copy this before adding a step, because not every copy
				//should contain the step
			}
			addStep(enabledTransitions.remove(0), state.getTrace(),
					state.getMarking(), state.getOccuredMarkings());
			//special treatment for the first option, because it is
			//not necessary to copy the log when having just one option
			for (Transition transition : enabledTransitions) {
				addStep(transition, copy.duplicate(), state.getMarking(),
						state.getOccuredMarkings());
			}
		}
	}
	
	private void addStep(Transition transition, Trace trace, Marking oldMarking,
				List<Marking> occuredMarkings) {
		addStep(transition, trace, oldMarking, 1, occuredMarkings);
	}
	
	private void addStep(Transition transition, Trace trace,
		   Marking oldMarking, int multiplicity, List<Marking> occuredMarkings) {
		if(transition instanceof LabeledTransition) {
			trace.addStep(new TraceEntry((LabeledTransition)transition, new Date()));
		}
		occuredMarkings.add(oldMarking);
		activeStates.add(
				new GenerationState(
						interpreter.fireTransition(net,oldMarking, transition),
						trace,
						multiplicity,
						occuredMarkings));
	}
	
	private void initializeActiveStates(ProcessLog log) {
		activeStates = new HashSet<GenerationState>();
		activeStates.add(new GenerationState(net.getInitialMarking(), log.newTrace(), traceCount));
	}
	
	private ProcessLog generateOrderingCompleteLog() {
		ProcessLog log = new ProcessLog();
		initializeActiveStates(log);
		
		while (!activeStates.isEmpty()) {
			LinkedList<GenerationState> oldStates = new LinkedList<GenerationState>();
			oldStates.addAll(activeStates);
			activeStates.clear();
			proceedAllForOrderingCompleteness(oldStates);
		}
		
		log.duplicateToMinimumTraceCount(traceCount);
		return log;
	}
	
	private void proceedAllForOrderingCompleteness(List<GenerationState> states) {
		for (GenerationState state : states) {
			if (state.getTrace().size() >= maxTraceSize) {
				state.getTrace().remove();
			} else {
				proceedForOrderingCompleteness(state);
			}
		}
	}
	
	private void proceedForOrderingCompleteness(GenerationState state) {
		List<Transition> enabledTransitions =
			interpreter.getEnabledTransitions(net, state.getMarking());
		if (enabledTransitions.size() == 1) {
			addStep(enabledTransitions.get(0), state.getTrace(),
					state.getMarking(), state.getOccuredMarkings());
		} else if (!enabledTransitions.isEmpty()) {
			boolean continuedOne = false;
			Transition lastTransition = 
					state.getTrace().getLastStep() == null ? null :
						state.getTrace().getLastStep().getTransition();
			for (Transition transition : enabledTransitions) {
				if (!state.getTrace().getLog().
						containsDirectSuccession(lastTransition, transition)) {
					continuedOne = true;
					addStep(transition, state.getTrace().duplicate(),
							state.getMarking(), state.getOccuredMarkings());
				}
			}
			if (continuedOne) {
				state.getTrace().remove(); //every continuation used a copy
			} else {
				continueWithMostProbableTransition(state, enabledTransitions);
			}
		}
	}
	
	private void continueWithMostProbableTransition(GenerationState state,
			List<Transition> enabledTransitions) {
		Transition mostProbable = null;
		int highestProbability = -1;
		for (Transition transition : enabledTransitions) {
			int probability =
				propabilityOfTransitionIn(transition, enabledTransitions);
			if (probability > highestProbability) {
				mostProbable = transition;
				highestProbability = probability;
			}
		}
		addStep(mostProbable, state.getTrace(),
				state.getMarking(), state.getOccuredMarkings());
	}
	
	private ProcessLog generateNotCompleteLog() {
		ProcessLog log = new ProcessLog();
		initializeActiveStates(log);
		
		while (!activeStates.isEmpty()) {
			LinkedList<GenerationState> oldStates = new LinkedList<GenerationState>();
			oldStates.addAll(activeStates);
			activeStates.clear();
			proceedAllAccordingToPropability(oldStates);
		}
		
		return log;
	}
	
	private void proceedAllAccordingToPropability(LinkedList<GenerationState> states) {
		for (GenerationState state : states) {
			if (state.getTrace().size() >= maxTraceSize) {
				state.getTrace().remove();
			} else {
				proceedAccordingToPropability(state);
			}
		}
	}
	
	private void proceedAccordingToPropability(GenerationState state) {
		List<Transition> enabledTransitions =
			interpreter.getEnabledTransitions(net, state.getMarking());
		if(enabledTransitions.size() == 1) {
			addStep(enabledTransitions.get(0), state.getTrace(),
					state.getMarking(), state.getMultiplicity(),
					state.getOccuredMarkings());
		}
		else if (! enabledTransitions.isEmpty()) {
			state.getTrace().remove();//remove the original trace from the log,
			//because we will create a copy for each enabled transition
			for (Map.Entry<Transition, Integer> entry :
				calculateMultiplicities(state, enabledTransitions).entrySet()) {
				if(entry.getValue() >= 1) {
					addStep(entry.getKey(), state.getTrace().duplicate(),
							state.getMarking(), entry.getValue(),
							state.getOccuredMarkings());
				}
			}
		} else { //Trace ends here - we have to duplicate it as its multiplicity says
			for (int i = 1; i < state.getMultiplicity(); ++i) {
				//starting at 1, because state.getTrace() is one duplicate itself
				state.getTrace().duplicate();
			}
		}
	}

	/**
	 * calculates the multiplicity of each enabled transition in the given state
	 * as a Double
	 * @param state the current GenerationState, especially reflecting the current
	 * multiplicity
	 * @param transitions the list of transitions, for which multiplicities should
	 * be calculated.
	 * @return a mapping from each transition of transitions to its multiplicity
	 * */
	private Map<Transition,Integer> calculateMultiplicities(GenerationState state,
			List<Transition> transitions) {
		int propabilitySum = propabilitySumOf(transitions);
		Map<Transition, Double> multiplicities = 
			calculateUnroundedMultiplicities(state, transitions, propabilitySum);
		return roundMultiplicities(state, multiplicities);
	}

	private Map<Transition, Double> calculateUnroundedMultiplicities(
			GenerationState state, List<Transition> transitions,
			int propabilitySum) {
		Map<Transition, Double> multiplicities =
			new HashMap<Transition, Double>();
		for (Transition transition : transitions) {
			multiplicities.put(transition,
					((double) state.getMultiplicity())*
					propabilityOfTransitionIn(transition, transitions)/
					propabilitySum);
		}
		return multiplicities;
	}
	
	private Map<Transition, Integer> roundMultiplicities(GenerationState state,
			Map<Transition, Double> unroundedMultiplicities) {
		Map<Transition, Integer> roundedMultiplicities =
			new HashMap<Transition, Integer>();
		int toBeDistributed = floorMultiplicities(state,
				unroundedMultiplicities, roundedMultiplicities);
		distributeLeftMultiplicity(unroundedMultiplicities,
				roundedMultiplicities, toBeDistributed);
		return roundedMultiplicities;
	}

	private int floorMultiplicities(GenerationState state,
			Map<Transition, Double> unroundedMultiplicities,
			Map<Transition, Integer> roundedMultiplicities) {
		int toBeDistributed = state.getMultiplicity();
		for (Map.Entry<Transition, Double> entry : 
				asValueSortedEntrySet(unroundedMultiplicities)) {
			int roundedMultiplicity = (int) Math.floor(entry.getValue());
			roundedMultiplicities.put(entry.getKey(), roundedMultiplicity);
			unroundedMultiplicities.put(entry.getKey(), entry.getValue() - roundedMultiplicity);
			//stores the decimals in the multiplicities-map. This is necessary
			//for the next step: When only flooring the numbers, the sum of the
			//assigned multiplicities will not be the same as the current multiplicity
			//and so the rest has to be distributed in the order of the decimals
			toBeDistributed -= roundedMultiplicity;
		}
		return toBeDistributed;
	}
	
	private void distributeLeftMultiplicity(
			Map<Transition, Double> unroundedMultiplicities,
			Map<Transition, Integer> roundedMultiplicities, int leftMultiplicity) {
		for (Map.Entry<Transition, Double> entry : 
			asValueSortedEntrySet(unroundedMultiplicities)) {
			if (leftMultiplicity > 0) {
				roundedMultiplicities.put(entry.getKey(), 
						roundedMultiplicities.get(entry.getKey()) + 1);
				-- leftMultiplicity;
			}
		}
	}

	private SortedSet<Map.Entry<Transition, Double>> asValueSortedEntrySet(
			Map<Transition, Double> map) {
		SortedSet<Map.Entry<Transition, Double>> sortedEntries = 
					new TreeSet<Map.Entry<Transition, Double>>(
							new Comparator<Map.Entry<Transition, Double>>() {

								@Override
								public int compare(
										Entry<Transition, Double> o1,
										Entry<Transition, Double> o2) {
									int comp = o2.getValue().compareTo(o1.getValue());
									if(comp == 0 && ! o1.equals(o2)) {
										return o1.getKey().getId().compareTo(o2.getKey().getId());
									} else {
										return comp;
									}
								}
					});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	private int propabilitySumOf(List<Transition> transitions) {
		int propabilitySum = 0;
		for (Transition transition : transitions) {
			propabilitySum += propabilityOfTransitionIn(transition, transitions);
		}
		return propabilitySum;
	}
	
	private int propabilityOfTransitionIn(Transition transition, List<Transition> transitions) {
		if (transition instanceof TransitionWithPropability &&
				((TransitionWithPropability) transition).getPropability() != -1) {
			return ((TransitionWithPropability)transition).getPropability();
		} else {
			return 100 / transitions.size();
		}
	}
}
