package de.hpi.olc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.PTnet.PTNet;
import de.hpi.cpn.converter.CPNDiagram;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Node;

public class CPNGenerator {
	public enum ColorSet {
		State, StateList, SyncState
	};

	private PTNet olc = null;
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
	
	public CPNGenerator(PTNet olc) {
		this.cpn = CPNDiagram.newColoredPetriNetDiagram();
		this.olc = olc;
		this.ressourceIdGenerator = new RessourceIDGenerator();
	}

	/**
	 * Generates a workflow model for a given Object Life Cycle
	 * @return the workflow model
	 */
	public Diagram generate() {

		//Preprocessing
		//TODO: decompose complex joins and extract exclusive decisions
		namePlaces();
		extractSyncStates();
		
		// Calculate GuardConditions for process termination/continuation
		guardResume = "[i<>" + olc.getFinalPlace().getLabel() + "]";
		guardExit = "[i=" + olc.getFinalPlace().getLabel() + "]";
		
		// Add Declarations
		cpn.getProperties().put("declarations", CPNDeclarations.getDeclarations());
		
		// Generate Model
		init = getAPlace("init", ColorSet.State);
		Shape initToOr = getAnArc(arcState);
		or = getATransition("Or");
		connect(init, initToOr, or);
		
		gate = getAPlace("gate", ColorSet.State);
		generateTransitionBlock();
		generateProcessTermination();
		generateTokenMatcher();
		
		// Generate Initial Token
		Shape initialToken = getAToken(olc.getInitialPlace().getLabel(), 1);
		init.getChildShapes().add(initialToken);
		
		// Generate Synchronization Tokens
		generateSyncTokens();
		
		// Layout Model
		CpnLayouter layouter = new CpnLayouter(cpn);
		layouter.layout();

		return cpn;
	}

	/**
	 * Gives each place of the Object Life Cycle a Name.
	 * Places that a target of the same transition get equal names
	 */
	private void namePlaces() {
		int placeId = 0;
		olc.getInitialPlace().setId("p_" + placeId);
		for(Node transition : olc.getTransitions()) {
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
	private void extractSyncStates() {
		for(Node transition : olc.getTransitions()) {
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
	private void generateTransitionBlock() {
		// TODO: Merge transitions with equal label
		for (Node transition : olc.getLabeledTransitions()) {
			// generate cond. arc, place, arc, transition, arc
			Shape toPlace = getAnArc(getArcCondition(transition));
			Shape p = getAPlace("p_" + transition.getId(), ColorSet.State);
			Shape toTransition = getAnArc(arcState);
			Shape t = getATransition(transition.getId());
			// TODO: Verify Property_Name
			t.getProperties().put("code", getCodeForTransition(transition));
			Shape toGate = getAnArc(arcNextState);
			// connect them and connect to or and gate
			connect(or, toPlace, p);
			connect(p, toTransition, t);
			connect(t, toGate, gate);
		}
	}

	/**
	 * This process part is connected to GATE
	 * Generates Exit-Transition and Final Place.
	 */
	private void generateProcessTermination() {
		// generate arc, exit-transition, arc and final place
		Shape gateToTransition = getAnArc(arcState);
		Shape transition = getATransition("exit", guardExit);
		Shape transitionToEnd = getAnArc(arcState);
		Shape finalPlace = getAPlace("end", ColorSet.State);
		// connect shapes
		connect(gate, gateToTransition, transition);
		connect(transition, transitionToEnd, finalPlace);
	}

	/**
	 * This process part is the connection between Gate and INIT
	 * Generates the static structure of the token matcher
	 */
	private void generateTokenMatcher() {
		Shape gateToResume = getAnArc(arcState);
		Shape resume = getATransition("resume", guardResume);
		Shape resumeToEntry = getAnArc(arcState);
		Shape entry = getAPlace("entry", ColorSet.State);
		Shape entryToCont = getAnArc(arcState);
		Shape cont = getATransition("continue", guardContinue);
		Shape contToInit = getAnArc(arcState);
		// connect
		connect(gate, gateToResume, resume);
		connect(resume, resumeToEntry, entry);
		connect(entry, entryToCont, cont);
		connect(cont, contToInit, init);

		// generate shapes for entry
		Shape enter = getATransition("enter", guardEnter);
		syncStates = getAPlace("syncStates", ColorSet.StateList);
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
		Shape second = getATransition("second", guardSecond);
		Shape matcher = getAPlace("matcher", ColorSet.State);
		Shape wait = getAPlace("wait", ColorSet.SyncState);
		sync = getAPlace("sync", ColorSet.SyncState);
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
		connect(first, firstToWait, wait);
		connect(wait, waitToSecond, second);
		connect(sync, syncToFirst, first);
		connect(second, secondToSync, sync);
		connect(second, secondToEntry, entry);
	}
	
	/**
	 * Generates one token with the list of all SyncStates and puts it in the place 'syncStates'
	 * Generates a token (firstState,secondState,succeedingState) for each SyncSet
	 */
	private void generateSyncTokens() {
		Shape syncStatesToken = getAToken(synchronizationStates.toString(),1);
		syncStates.getChildShapes().add(syncStatesToken);
		int index = 0;
		for(SyncSet syncSet : synchronizationSets) {
			Shape token = getAToken(syncSet.toString(), index);
			sync.getChildShapes().add(token);
			index ++;
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
	private Shape getAToken(String state, int i) {
		Shape token = CPNDiagram.getaToken(ressourceIdGenerator.getNewId());
		CPNDiagram.setTokenBounds(token, i);
		token.getProperties().put("initialmarking", state);
		cpn.addShapes(token);
		return token;
	}

	/**
	 * Generates the arc condition for a transition in the block of transitions
	 * @param transition: the transition in the Object Life Cycle
	 * @return Condition for the arc from "Or" to transition's incoming place
	 */
	private String getArcCondition(Node transition) {
		String arcCondition = "if";
		boolean first = true;
		for (FlowRelationship fr : transition.getIncomingFlowRelationships()) {
			if (first) {
				arcCondition += " i=";
				first = false;
			} else {
				arcCondition += " || i=";
			}
			arcCondition += fr.getSource().getId();
		}

		arcCondition += "then 1`i else empty";
		return arcCondition;
	}

	/**
	 * Generates the code of the transition in the block of transitions
	 * @param transition: the transition in the Object Life Cycle
	 * @return Code for the transition in the workflow model
	 */
	private String getCodeForTransition(Node transition) {
		// TODO: XOR Expression for alternative outcomes
		String code = "input (i); output(l); action let in (";
		boolean first = true;
		for (FlowRelationship fr : transition.getOutgoingFlowRelationships()) {
			if (first) {
				code += fr.getSource().getId();
				;
				first = false;
			}
		}
		code += ") end;";
		return "";
	}

	/**
	 * Connects two shapes with an arc
	 * @param source: start node (either a place or a transition)
	 * @param arc: the arc that should connect both nodes
	 * @param target: target node (either a transition or a place)
	 */
	private void connect(Shape source, Shape arc, Shape target) {
		source.addOutgoing(arc);
		arc.setTarget(target);
		arc.addOutgoing(target);
		target.addIncoming(arc);
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
