package de.hpi.olc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;

public class CPNGenerator {
	public enum ColorSet {
		State, StateList, SyncState
	};

	private CPNFactory factory = null;
	private WfMSkeleton skeleton = null;

	private List<String> synchronizationStates = new ArrayList<String>();
	private List<SyncSet> synchronizationSets = new ArrayList<SyncSet>();

	public CPNGenerator() {
		factory = new CPNFactory();
	}

	/**
	 * Generates a workflow model for a given Object Life Cycle
	 * 
	 * @return the workflow model
	 */
	public Diagram generate(PTNet olc) {
		// Preprocessing
		Preprocessing preprocessing = new Preprocessing();
		preprocessing.decomposeJoinTransitions(olc);
		preprocessing.extractXors(olc);
		preprocessing.createStateNames(olc);

		// Calculate GuardConditions for process termination/continuation
		String guardResume = "[i<>" + olc.getFinalPlace().getId() + "]";
		String guardExit = "[i=" + olc.getFinalPlace().getId() + "]";

		// Generate static part of Workflow Model
		skeleton = new WfMSkeleton(factory);
		skeleton.generate(guardExit, guardResume);

		// Generate Tokens
		generateTokens(olc);

		// Generate Block of Transitions
		generateTransitionBlock(olc);

		return factory.getCpn();
	}

	/**
	 * Generates an initial token and synchronization tokens for the given OLC
	 * and adds them to the workflow model
	 */
	private void generateTokens(PTNet net) {
		Shape initialToken = factory.getAToken(net.getInitialPlace().getId());
		skeleton.getInit().getChildShapes().add(initialToken);
		extractSyncStates(net);
		generateSyncTokens();
	}

	/**
	 * This process part is the connection between OR and GATE Generates place
	 * and transition for each transition of the Object Life Cycle. Transitions
	 * with equal labels are merged.
	 */
	private void generateTransitionBlock(PTNet net) {

		Collection<TempTransition> transitions = mergeTransitions(net);

		// Generate Transition + incoming place and arcs
		int index = 0; // keeps track of the number of transitions for layouting
		for (TempTransition transition : transitions) {
			// generate cond. arc, place, arc, transition, arc
			Shape toPlace = factory.getAnArc(transition.getArcCondition());
			Shape p = factory.getAPlace("p_" + transition.getLabel(), ColorSet.State);
			p.setBounds(Layout.getBoundsForPlace(930, 60 + (index * 80)));
			Shape toTransition = factory.getAnArc(Constants.arcState);
			Shape t = factory.getATransition(transition.getLabel());
			t.setBounds(Layout.getBoundsForTransition(1080, 60 + (index * 80)));
			Shape toGate = factory.getAnArc(transition.getCodeForTransition());
			// connect them and connect to "or" and "gate"
			factory.connect(skeleton.getOr(), toPlace, p);
			factory.connect(p, toTransition, t);
			factory.connect(t, toGate, skeleton.getGate(), false);
			index++;
		}
	}
	
	/**
	 * Generates one token with the list of all SyncStates and puts it in the
	 * place 'syncStates' Generates a token
	 * (firstState,secondState,succeedingState) for each SyncSet
	 */
	private void generateSyncTokens() {
		Shape syncStatesToken = factory.getAToken(synchronizationStates.toString());
		skeleton.getSyncStates().getChildShapes().add(syncStatesToken);
		for (SyncSet syncSet : synchronizationSets) {
			Shape token = factory.getAToken(syncSet.toString());
			skeleton.getSync().getChildShapes().add(token);
		}
	}

	/**
	 * Adds all incoming places of transitions with more than one incoming place
	 * to the list of SynchronizationStates Adds tuple
	 * (firstState,secondState,succeedingState) to the list of SyncSets.
	 */
	private void extractSyncStates(PTNet net) {
		for (Node transition : net.getTransitions()) {
			if (transition.getPrecedingNodes().size() == 2 && transition.getSucceedingNodes().size() == 1) {
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
	 * Creates containers for all transitions except "join" transitions.
	 * Transitions with equal labels get the same container
	 */
	private Collection<TempTransition> mergeTransitions(PTNet net) {
		// Merge transitions with equal label
		HashMap<String, TempTransition> transitions = new HashMap<String, TempTransition>();
		for (Node node : net.getLabeledTransitions()) {
			LabeledTransition transition = (LabeledTransition) node;
			String label = transition.getLabel();

			// skip join transitions
			if (label.equals("join"))
				continue;

			// merge transitions if there is another one with the same label
			if (transitions.containsKey(label)) {
				TempTransition original = transitions.get(label);
				original.addTransformation(transition);
			} else
				transitions.put(label, new TempTransition(transition));
		}
		return transitions.values();
	}

	public static String toJson(Diagram diagram) {
		try {
			return JSONBuilder.parseModeltoString(diagram);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "[]";
	}
}
