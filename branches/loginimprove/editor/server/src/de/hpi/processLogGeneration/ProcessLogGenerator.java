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
 * Generates a ProcessLog in MXML-format from a Petrinet.
 * The following options can be chosen:
 * completeness: None (no completeness required), Ordering (if A may directly succeed
 * B, there must be a trace, where A directly succeeds B), Trace (all possible traces
 * must be represented). Note, that Trace-completeness is not possible, if the 
 * net contains loops. Trace-completeness and Ordering-completenss may lead to
 * a very large set of traces
 * noise: the percentage (number from 0 to 100) of noise, which the log should
 * contain.
 * number of logs (when Trace- or Ordering-completeness is selected, this represents
 * the minimum number of logs)
 * 
 * @author Thomas Milde
 * */
public class ProcessLogGenerator {
	
	/**
	 * the PetriNet, from which a log should be generated
	 * */
	private PetriNet net;
	
	/**
	 * the completeness-option, which the user has selected (None, Trace or 
	 * Ordering completenss) 
	 * */
	private CompletenessOption completeness;
	
	/**
	 * the degree of noise selected by the user (percentage from 0 to 100)
	 * */
	private int noise;
	
	/**
	 * the number of traces, which the user likes to be generated. If Ordering-
	 * completenss or Trace-completeness is selected, this acts as the minimum
	 * number of traces to be generated.
	 * */
	private int traceCount;

	/**
	 * a set of states, which are currently being processes, i.e. objects
	 * containing the traces, which are currently being built along with their 
	 * current markings and their history of markings.
	 * */
	private Collection<GenerationState> activeStates;
	
	/**
	 * the interpreter, that is used to get to know, which transitions are
	 * enabled and in what marking the firing of a transition results
	 * */
	private final PTNetInterpreter interpreter = new PTNetInterpreter();
	
	/**
	 * constant number, that defines the maximum size of a trace (in order to
	 * avoid endless loops)
	 * */
	private final int maxTraceSize = 500;	
	
	/**
	 * constructor, that initializes all the fields of ProcessLogGenerator with 
	 * the desired values.
	 * @param net the PetriNet, from which a log should be generated
	 * @param completeness the completeness-option, that is required
	 * (None, Trace or Ordering completenss)
	 * @param noise  the desired degree of noise (percentage from 0 to 100)
	 * @param traceCount the number of traces, that should be generated. If
	 * Ordering-completenss or Trace-completeness is selected, this acts as the
	 * minimum number of traces to be generated.
	 * */
	public ProcessLogGenerator(PetriNet net, CompletenessOption completeness,
			int noise, int traceCount) {
		this.net = net;
		this.completeness = completeness;
		this.noise = noise;
		this.traceCount = traceCount;
	}

	/**
	 * genrates a log and returns it in its serialized form, i.e. as an MXML-
	 * document
	 * 
	 * @return the MXML-representation of the generated log
	 * */
	public String getSerializedLog() {
		return generateLog().serialize();
	}
	
	/**
	 * generates a log from the net with respect to the options specified in the
	 * fields completeness, noise and traceCount
	 * 
	 * @return the generated log
	 * */
	public ProcessLog generateLog() {
		ProcessLog log = generateNoiseFreeLog();
		if(noise != 0) {
			log.generateNoise(noise);
		}
		log.calculateTimes();
		return log;
	}
	
	/**
	 * generates a log with respect to completeness and traceCount, but without
	 * generating any noise.
	 * 
	 * @return the generated log
	 * */
	public ProcessLog generateNoiseFreeLog() {
		if(completeness.equals(CompletenessOption.Trace)){
			return generateTraceCompleteLog();
		} else if (completeness.equals(CompletenessOption.Ordering)) {
			return generateOrderingCompleteLog();
		} else {
			return generateNotCompleteLog();
		}
	}
	
	/**
	 * Generates a trace-complete log from the petrinet (field net). If the net
	 * contains loops, the log cannot be trace-complete and therefore every loop
	 * will only be executed once.
	 * 
	 * @return the generated log
	 * */
	public ProcessLog generateTraceCompleteLog() {
		ProcessLog log = new ProcessLog();
		initializeActiveStates(log);//=> there is one active state: an empty
		//trace starting at the initial marking of the net.
		
		while (!activeStates.isEmpty()) {
			LinkedList<GenerationState> oldStates =
					new LinkedList<GenerationState>();
			oldStates.addAll(activeStates);
			activeStates.clear();
			proceedAll(oldStates);
		}
		log.duplicateToMinimumTraceCount(traceCount);//if the number of generated
		//traces is less than traceCount, some traces will be duplicated (with
		//respect to the probabilities of transitions)
		return log;
	}

	/**
	 * proceeds all of the states in such a manner, that a trace-complete log 
	 * can be achieved if the net contains no loops.
	 * 
	 * @param states the GenerationStates, from which to proceed generating steps
	 * */
	private void proceedAll(LinkedList<GenerationState> states) {
		for (GenerationState state : states) {
			if (state.getTrace().size() >= maxTraceSize ||//"emergency break"
					state.occurenceCount(state.getMarking()) > 2) {//execute every loop only once
				state.getTrace().remove();
			} else {
				proceed(state);
			}
		}
	}

	/**
	 * Proceeds generating steps from one GenerationState by executing all enabled
	 * transitions.
	 * */
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
	
	/**
	 * adds a new step to the trace by executing transition, starting from
	 * oldMarking and adding the resulting new Marking along with the trace in 
	 * a GenerationState-object to the activeStates. The multiplicity of the 
	 * newly added GenerationState will be one.
	 * 
	 * @param transition the transition to execute
	 * @param trace the trace, to which the step should be appended
	 * @param oldMarking the current marking
	 * @param occuredMarkings the list of markings that occured in the trace
	 * */
	private void addStep(Transition transition, Trace trace, Marking oldMarking,
				List<Marking> occuredMarkings) {
		addStep(transition, trace, oldMarking, 1, occuredMarkings);
	}
	
	/**
	 * adds a new step to the trace by executing transition, starting from
	 * oldMarking and adding the resulting new Marking along with the trace in 
	 * a GenerationState-object to the activeStates.
	 * 
	 * @param transition the transition to execute
	 * @param trace the trace, to which the step should be appended
	 * @param oldMarking the current marking
	 * @param multiplicity the multiplicity of the trace after firing the transition
	 * @param occuredMarkings the list of markings that occured in the trace
	 * */
	private void addStep(Transition transition, Trace trace,
		   Marking oldMarking, int multiplicity, List<Marking> occuredMarkings) {
		if(transition instanceof LabeledTransition) {
			trace.addStep(new TraceEntry((LabeledTransition)transition, new Date()));
		}
		//SilentTransitions must not be represented in the log, but must be
		//taken into consideration for the calculation of the next step.
		occuredMarkings.add(oldMarking);
		activeStates.add(
				new GenerationState(
						interpreter.fireTransition(net,oldMarking, transition),
						trace,
						multiplicity,
						occuredMarkings));
	}
	
	/**
	 * initializes the field activeStates to contain only one state: an empty
	 * trace with the current marking being the initial marking of the petrinet.
	 * 
	 * @param log the log, which the empty trace should belong to
	 * */
	private void initializeActiveStates(ProcessLog log) {
		activeStates = new HashSet<GenerationState>();
		activeStates.add(new GenerationState(
				net.getInitialMarking(), log.newTrace(), traceCount));
	}
	
	/**
	 * generates a log, that is ordering-complete and contains no noise.
	 * 
	 * @return the generated log
	 * */
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
	
	/**
	 * proceeds with generating the traces of all GenerationStates in states
	 * in order to achieve an ordering-complete log
	 * 
	 * @param states the GenerationStates, which should be continued from
	 * */
	private void proceedAllForOrderingCompleteness(List<GenerationState> states) {
		for (GenerationState state : states) {
			if (state.getTrace().size() >= maxTraceSize) {//"emergency break"
				state.getTrace().remove();
			} else {
				proceedForOrderingCompleteness(state);
			}
		}
	}
	
	/**
	 * Proceeds generating the log at one GenerationState. This will use those
	 * transitions, that did not already follow the last fired transition (in
	 * any of the log's traces).
	 * 
	 *  @param state the GenerationState containing the marking at which to 
	 *  continue and the trace, to which continuation should be added.
	 * */
	private void proceedForOrderingCompleteness(GenerationState state) {
		List<Transition> enabledTransitions =
			interpreter.getEnabledTransitions(net, state.getMarking());
		if (enabledTransitions.size() == 1) {
			//avoid doing complex computations in the trivial case 
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
					//there is no trace in the log, in which transition directly
					//succeeds lastTransition
					continuedOne = true;
					addStep(transition, state.getTrace().duplicate(),
							state.getMarking(), state.getOccuredMarkings());
				}
			}
			if (continuedOne) {
				state.getTrace().remove(); //every continuation used a copy, so
				//the original has to be discarded.
			} else {
				continueWithMostProbableTransition(state, enabledTransitions);
				//all of the enabledTransitions follow lastTransition in some 
				//trace, so use the most probable transition
			}
		}//else: no transitions are enabled. the trace ends here.
	}
	
	/**
	 * determines, which of enabledTransitions is most probable to be executed
	 * and continues state by firing this transition.
	 * 
	 * @param state the GenerationState from which to continue
	 * @param enabledTransitions the transitions, of which the most probable
	 * one should be fired.
	 * */
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
		}//until here it was just the search for a maximum
		addStep(mostProbable, state.getTrace(),
				state.getMarking(), state.getOccuredMarkings());
	}
	
	/**
	 * Generates a log, that does not meet any completeness-requirements without
	 * noise, but meeting the number of requested traces.
	 * 
	 * @return the generated log.
	 * */
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
	
	/**
	 * Proceeds all the states by distributing their multiplicities to all
	 * enabled transitions according to their probabilities.
	 * */
	private void proceedAllAccordingToPropability(LinkedList<GenerationState> states) {
		for (GenerationState state : states) {
			if (state.getTrace().size() >= maxTraceSize) {
				state.getTrace().remove();
			} else {
				proceedAccordingToPropability(state);
			}
		}
	}
	
	/**
	 * Proceeds one state by distributing it's multiplicity to all
	 * enabled transitions according to their probabilities.
	 * */
	private void proceedAccordingToPropability(GenerationState state) {
		List<Transition> enabledTransitions =
			interpreter.getEnabledTransitions(net, state.getMarking());
		if(enabledTransitions.size() == 1) {//trivial case
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
	 * according to the probabilities of the transitions.
	 * 
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

	/**
	 * calculates the multiplicity of each enabled transition in the given state
	 * according to the probabilities of the transitions as a Double. This means,
	 * that the multiplicities are not directly usable, but must be rounded first.
	 * 
	 * @param state the current GenerationState, especially reflecting the current
	 * multiplicity
	 * @param transitions the list of transitions, for which multiplicities should
	 * be calculated.
	 * @param propabilitySum the sum of the probabilities of the transitions
	 * @return a mapping from each transition of transitions to its multiplicity
	 * */
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
	
	/**
	 * Rounds the multiplicities, that are given in unroundedMultiplicities 
	 * in such a way that the sum of the rounded multiplicities is exactly the
	 * same as the multiplicity given in state.
	 * */
	private Map<Transition, Integer> roundMultiplicities(GenerationState state,
			Map<Transition, Double> unroundedMultiplicities) {
		Map<Transition, Integer> roundedMultiplicities =
			new HashMap<Transition, Integer>();
		int toBeDistributed = floorMultiplicities(state,
				unroundedMultiplicities, roundedMultiplicities);
		//floor the multiplicities and store the difference between the sum of
		//the stored multiplicities and the multiplicity of state in toBeDistributed
		distributeLeftMultiplicity(unroundedMultiplicities,
				roundedMultiplicities, toBeDistributed);
		return roundedMultiplicities;
	}

	/**
	 * floors the values of unroundedMultiplicities and stores the results as
	 * Integers in roundedMultiplicities.
	 * 
	 * @param unroundedMultiplicities the multiplicities, which should be rounded.
	 * when this method returns, this map will contain the decimals of the values,
	 * that it contained on method invocation.
	 * @param roundedMultiplicities the map, where the floored values should be
	 * stored
	 * @return the difference between the sum of the floored multiplicities and
	 * the multiplicity specified in state. 
	 * */
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
	
	/**
	 * Distributes the multiplicity, that is left after flooring the unrounded
	 * values.
	 * */
	private void distributeLeftMultiplicity(
			Map<Transition, Double> multiplicitiesDecimals,
			Map<Transition, Integer> roundedMultiplicities, int leftMultiplicity) {
		for (Map.Entry<Transition, Double> entry : 
				asValueSortedEntrySet(multiplicitiesDecimals)) {
			if (leftMultiplicity > 0) {
				roundedMultiplicities.put(entry.getKey(), 
						roundedMultiplicities.get(entry.getKey()) + 1);
				-- leftMultiplicity;
			}
		}
	}

	/**
	 * Creates a SortedSet of the map's entries. The set will be sorted according
	 * to the value, not the key, of the map-entries.
	 * */
	private SortedSet<Map.Entry<Transition, Double>> asValueSortedEntrySet(
			Map<Transition, Double> map) {
		SortedSet<Map.Entry<Transition, Double>> sortedEntries = 
					new TreeSet<Map.Entry<Transition, Double>>(
							new Comparator<Map.Entry<Transition, Double>>() {

								@Override
								public int compare(
										Entry<Transition, Double> o1,
										Entry<Transition, Double> o2) {
									int comp = o2.getValue().
											compareTo(o1.getValue());
									if(comp == 0 && ! o1.equals(o2)) {
										return o1.getKey().getId().
												compareTo(o2.getKey().getId());
										//comparing two entries with the same
										//value, but different keys must not
										//return 0.
									} else {
										return comp;
									}
								}
					});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	/**
	 * calculates the sum of the probabilities of transitions.
	 * */
	private int propabilitySumOf(List<Transition> transitions) {
		int propabilitySum = 0;
		for (Transition transition : transitions) {
			propabilitySum += propabilityOfTransitionIn(transition, transitions);
		}
		return propabilitySum;
	}
	
	/**
	 * calculates the probability of a transition, when it is competing with
	 * the other transition contained in the parameter transitions. If the 
	 * transition has a value set for its probability, this will be returned.
	 * Otherwise 100 divided by the size of transitions will be returned.
	 * */
	private int propabilityOfTransitionIn(
			Transition transition, List<Transition> transitions) {
		if (transition instanceof TransitionWithPropability &&
				((TransitionWithPropability) transition).getPropability() != -1) {
			return ((TransitionWithPropability)transition).getPropability();
		} else {
			return 100 / transitions.size();
		}
	}
}
