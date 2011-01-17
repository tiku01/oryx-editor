package de.hpi.olc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.PTnet.PTNet;
import de.hpi.cpn.converter.CPNDiagram;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;

public class CPNGenerator {
	public enum ColorSet {
		State, StateList, SyncState
	};

	private Diagram cpn = null;
	private Shape gate = null;
	private Shape or = null;
	private Shape init = null;
	private Shape sync = null;
	private Shape syncStates = null;
	private RessourceIDGenerator ressourceIdGenerator = null;
	private static String arcState = "i";
	private static String arcNextState = "l";
	private static String arcStateList = "a";
	private static String arcSync = "(j,k,l)";
	private static String guardEnter = "[contains(i,a)]";
	private static String guardContinue = "[not(contains(i,a))]";
	private static String guardFirst = "[i=j]";
	private static String guardSecond = "[i=k]";
	private String guardExit;
	private String guardResume;
	private List<String> synchronizationStates = new ArrayList<String>();
	private List<SyncSet> synchronizationSets = new ArrayList<SyncSet>();
	
	public CPNGenerator() {
		this.ressourceIdGenerator = new RessourceIDGenerator();
	}

	/**
	 * Generates a workflow model for a given Object Life Cycle
	 * @return the workflow model
	 */
	public Diagram generate(PTNet olc) {
		
		//TODO: Name outgoing arcs instead of places
		
		// Create new colored Petri net
		this.cpn = CPNDiagram.newColoredPetriNetDiagram();
		cpn.getProperties().put("declarations", CPNDeclarations.getDeclarations());
		
		//Preprocessing
		Preprocessing preprocessing = new Preprocessing();
		PTNet normalizedOLC = preprocessing.decomposeJoinTransitions(olc);
		normalizedOLC = preprocessing.extractXors(normalizedOLC);
		
		namePlaces(normalizedOLC);
		extractSyncStates(normalizedOLC);
		
		// Calculate GuardConditions for process termination/continuation
		guardResume = "[i<>" + normalizedOLC.getFinalPlace().getId() + "]";
		guardExit = "[i=" + normalizedOLC.getFinalPlace().getId() + "]";
		
		// Generate static part of workflow model
		generateInitOrAndGate();
		generateProcessTermination();
		generateTokenMatcher();
		
		// Generate Tokens
		Shape initialToken = getAToken(normalizedOLC.getInitialPlace().getId());
		init.getChildShapes().add(initialToken);
		generateSyncTokens();
		
		// Generate Block of Transitions
		generateTransitionBlock(normalizedOLC);
		
		// funny, isn't it ...
		cpn.setChildShapes(cpn.getShapes());

		return cpn;
	}



	/**
	 * Gives each place of the Object Life Cycle a Name.
	 * Outgoing places of a transition get equal names
	 */
	//TODO: Special case - double names - solved by arcs determining state values
	private void namePlaces(PTNet net) {
		int placeId = 0;
		net.getInitialPlace().setId("p_" + placeId);
		for(Node transition : net.getTransitions()) {
			placeId ++;
			for(Node place : transition.getSucceedingNodes()) {
				place.setId("p_" + placeId);
			}
		}
	}
	
	/**
	 * Adds all incoming places of transitions with more than one incoming place
	 * to the list of SynchronizationStates
	 * Adds tuple (firstState,secondState,succeedingState) to the list of SyncSets.
	 */
	private void extractSyncStates(PTNet net) {
		for(Node transition : net.getTransitions()) {
			if(transition.getPrecedingNodes().size() == 2 && transition.getSucceedingNodes().size() == 1) {
				String first = transition.getPrecedingNodes().get(0).getId();
				String second = transition.getPrecedingNodes().get(1).getId();
				String succeeder = transition.getSucceedingNodes().get(0).getId();
				synchronizationStates.add(first);
				synchronizationStates.add(second);
				synchronizationSets.add(new SyncSet(first, second, succeeder));
			}
		}
	}
	
	/**
	 * This process part is the connection between OR and GATE
	 * Generates place and transition for each transition of the Object Life Cycle.
	 * Transitions with equal labels are merged.
	 */
	private void generateTransitionBlock(PTNet net) {
		
		HashMap<String, TempTransition> transitions = mergeTransitions(net);
		
		// Generate Transition + incoming place and arcs
		int index = 0; // keeps track of the number of transitions for layouting
		for (TempTransition transition : transitions.values()) {
			// generate cond. arc, place, arc, transition, arc
			Shape toPlace = getAnArc(transition.getArcCondition());
			Shape p = getAPlace("p_" + transition.getLabel(), ColorSet.State);
			p.setBounds(Layout.getBoundsForPlace(930, 60+(index*80)));
			Shape toTransition = getAnArc(arcState);
			Shape t = getATransition(transition.getLabel());
			t.setBounds(Layout.getBoundsForTransition(1080, 60+(index*80)));
			Shape toGate = getAnArc(transition.getCodeForTransition());
			// connect them and connect to "or" and "gate"
			connect(or, toPlace, p);
			connect(p, toTransition, t);
			connect(t, toGate, gate, false);
			index ++;
		}
	}

	private HashMap<String, TempTransition> mergeTransitions(PTNet net) {
		// Merge transitions with equal label
		HashMap<String, TempTransition> transitions = new HashMap<String,TempTransition>();
		for (Node node : net.getLabeledTransitions()) {
			LabeledTransition transition = (LabeledTransition) node;
			String label = transition.getLabel();
			
			// skip join transitions
			if(label.equals("join")) continue;

			// merge transitions if there is another one with the same label
			if(transitions.containsKey(label)) {
				TempTransition original = transitions.get(label);
				original.addTransformation(transition);
			} else
				transitions.put(label, new TempTransition(transition));
		}
		return transitions;
	}

	/**
	 * This process part is connected to GATE
	 * Generates Exit-Transition and Final Place.
	 */
	private void generateProcessTermination() {
		// generate arc, exit-transition, arc and final place
		Shape gateToTransition = getAnArc(arcState);
		Shape transition = getATransition("exit", guardExit);
		transition.setBounds(Layout.getBoundsForTransition(1340, 60));
		Shape transitionToEnd = getAnArc(arcState);
		Shape finalPlace = getAPlace("end", ColorSet.State);
		finalPlace.setBounds(Layout.getBoundsForPlace(1440, 60));
		// connect shapes
		connect(gate, gateToTransition, transition);
		connect(transition, transitionToEnd, finalPlace);
	}

	/**
	 * Generates the basic skeleton of the workflow net
	 * Inital place, Or-Transition and place named gate.
	 * Connects init and or.
	 */
	private void generateInitOrAndGate() {
		init = getAPlace("init", ColorSet.State);
		init.setBounds(Layout.getBoundsForPlace(620, 60));
		or = getATransition("Or");
		or.setBounds(Layout.getBoundsForTransition(740, 60));
		gate = getAPlace("gate", ColorSet.State);
		gate.setBounds(Layout.getBoundsForPlace(1200, 60));
		Shape initToOr = getAnArc(arcState);
		connect(init, initToOr, or);
	}
	
	/**
	 * This process part is the connection between Gate and INIT
	 * Generates the static structure of the token matcher
	 */
	private void generateTokenMatcher() {
		Shape gateToResume = getAnArc(arcState);
		Shape resume = getATransition("resume", guardResume);
		resume.setBounds(Layout.getBoundsForTransition(1200, 500));
		Shape resumeToEntry = getAnArc(arcState);
		Shape entry = getAPlace("entry", ColorSet.State);
		entry.setBounds(Layout.getBoundsForPlace(500, 175));
		Shape entryToCont = getAnArc(arcState);
		Shape cont = getATransition("continue", guardContinue);
		cont.setBounds(Layout.getBoundsForTransition(500, 60));
		Shape contToInit = getAnArc(arcState);
		// connect
		connect(gate, gateToResume, resume);
		connect(resume, resumeToEntry, entry, false);
		connect(entry, entryToCont, cont);
		connect(cont, contToInit, init);

		// generate shapes for entry
		Shape enter = getATransition("enter", guardEnter);
		enter.setBounds(Layout.getBoundsForTransition(340, 175));
		syncStates = getAPlace("syncStates", ColorSet.StateList);
		syncStates.setBounds(Layout.getBoundsForPlace(340, 60));
		Shape entryToEnter = getAnArc(arcState);
		Shape enterToSyncStates = getAnArc(arcStateList);
		Shape syncStatesToEnter = getAnArc(arcStateList);
		Shape contToSyncStates = getAnArc(arcStateList);
		Shape syncStatesToCont = getAnArc(arcStateList);
		// connect
		connect(entry, entryToEnter, enter);
		connect(enter, enterToSyncStates, syncStates);
		connect(syncStates, syncStatesToEnter, enter);
		connect(syncStates, syncStatesToCont, cont);
		connect(cont, contToSyncStates, syncStates);

		// generate shapes for matching
		Shape first = getATransition("first", guardFirst);
		first.setBounds(Layout.getBoundsForTransition(235, 100));
		Shape second = getATransition("second", guardSecond);
		second.setBounds(Layout.getBoundsForTransition(235, 240));
		Shape matcher = getAPlace("matcher", ColorSet.State);
		matcher.setBounds(Layout.getBoundsForPlace(235, 175));
		Shape wait = getAPlace("wait", ColorSet.SyncState);
		wait.setBounds(Layout.getBoundsForPlace(130, 175));
		sync = getAPlace("sync", ColorSet.SyncState);
		sync.setBounds(Layout.getBoundsForPlace(50, 175));
		Shape enterToMatcher = getAnArc(arcState);
		Shape matcherToFirst = getAnArc(arcState);
		Shape matcherToSecond = getAnArc(arcState);
		Shape firstToWait = getAnArc(arcSync);
		Shape waitToSecond = getAnArc(arcSync);
		Shape syncToFirst = getAnArc(arcSync);
		Shape secondToSync = getAnArc(arcSync);
		Shape secondToEntry = getAnArc(arcNextState);
		// connect
		connect(enter, enterToMatcher, matcher);
		connect(matcher, matcherToFirst, first);
		connect(matcher, matcherToSecond, second);
		connect(first, firstToWait, wait, false);
		connect(wait, waitToSecond, second);
		connect(sync, syncToFirst, first);
		connect(second, secondToSync, sync, false);
		connect(second, secondToEntry, entry, false);
	}
	
	/**
	 * Generates one token with the list of all SyncStates and puts it in the place 'syncStates'
	 * Generates a token (firstState,secondState,succeedingState) for each SyncSet
	 */
	private void generateSyncTokens() {
		Shape syncStatesToken = getAToken(synchronizationStates.toString());
		syncStates.getChildShapes().add(syncStatesToken);
		for(SyncSet syncSet : synchronizationSets) {
			Shape token = getAToken(syncSet.toString());
			sync.getChildShapes().add(token);
		}
	}
	
	/**
	 * Adds a place to the diagram
	 * @param title: Name of the place
	 * @param colorSet: ColorSet of the place
	 * @return the new place
	 */
	private Shape getAPlace(String title, ColorSet colorSet) {
		Shape place = CPNDiagram.getaPlace(ressourceIdGenerator.getNewId());
		cpn.addShapes(place);
		place.getProperties().put("title", title);
		place.getProperties().put("colorsettype", colorSet.toString());
		return place;
	}

	/**
	 * Adds a new transition with guard condition to the diagram
	 * @param title: Name of the transition
	 * @param guard: GuardCondition of the transition
	 * @return the new transition
	 */
	private Shape getATransition(String title, String guard) {
		Shape transition = getATransition(title);
		transition.getProperties().put("guard", guard);
		return transition;
	}

	/**
	 * Adds a new transition to the diagram
	 * @param title: Name of the transition
	 * @return the new transition
	 */
	private Shape getATransition(String title) {
		Shape transition = CPNDiagram.getaTransition(ressourceIdGenerator.getNewId());
		transition.getProperties().put("title", title);
		cpn.addShapes(transition);
		return transition;
	}
	
	/**
	 * Adds a new arc to the diagram
	 * @param condition: label of the arc
	 * @return the new arc
	 */
	private Shape getAnArc(String condition) {
		Shape arc = CPNDiagram.getanArc(ressourceIdGenerator.getNewId());
		arc.getProperties().put("label", condition);
		CPNDiagram.setArcBounds(arc);
		cpn.addShapes(arc);
		return arc;
	}

	/**
	 * Adds a new token to the diagram
	 * @param state: initial marking of the token
	 * @param i: A number to determine the bounds of the token in its place
	 * @return the new token
	 */
	private Shape getAToken(String state) {
		Shape token = CPNDiagram.getaToken(ressourceIdGenerator.getNewId());
		token.setBounds(Layout.getBoundsForToken());
		token.getProperties().put("initialmarking", state);
		return token;
	}

	/**
	 * Connects two shapes with an arc
	 * @param source: start node (either a place or a transition)
	 * @param arc: the arc that should connect both nodes
	 * @param target: target node (either a transition or a place)
	 */
	private void connect(Shape source, Shape arc, Shape target) {
		connect(source, arc, target, true);
	}
	
	private void connect(Shape source, Shape arc, Shape target, boolean mode) {
		source.addOutgoing(arc);
		arc.setTarget(target);
		arc.addOutgoing(target);
		target.addIncoming(arc);
		arc.setDockers(Layout.getDockersForArc(source, target, arc, mode));
	}
	
	public static String toJson(Diagram diagram) {
		try {
			return JSONBuilder.parseModeltoString(diagram);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "[]";
	}

	private class RessourceIDGenerator {
		private int id = 0;

		public String getNewId() {
			id++;
			return "oryx_" + id;
		}
	}
}
