package de.hpi.olc;

import java.util.ArrayList;
import java.util.List;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class Preprocessing {

	private PTNetFactory factory;
	private static final String JOIN = "join";
	private static final String XOR = "xor_";
	private static final String NOP = "nop_";
	
	public Preprocessing() {
		factory = new PTNetFactory();
	}

	/**
	 * Creates unique names for places. Places with the same source transition
	 * get equal names
	 */
	public void createStateNames(PTNet net) {
		net.getInitialPlace().setId("0");
		int stateId = 0;
		for (Transition transition : net.getTransitions()) {
			stateId++;
			for (Node place : transition.getSucceedingNodes())
					place.setId(""+stateId);
		}
	}

	/**
	 * This transformation creates additional places and transitions to split
	 * each parallel join into pairwise joins independent from the original join
	 * transition
	 */
	public void decomposeJoinTransitions(PTNet net) {
		List<LabeledTransition> newTransitions = new ArrayList<LabeledTransition>();
		List<Place> newPlaces = new ArrayList<Place>();
		List<FlowRelationship> newArcs = new ArrayList<FlowRelationship>();

		for (Transition transition : net.getTransitions())
			if (transition.getPrecedingNodes().size() > 1)
				decomposeJoinTransition(transition, newTransitions, newPlaces, newArcs);

		// Add all new Nodes and Arcs to the lists of the net
		net.getTransitions().addAll(newTransitions);
		net.getPlaces().addAll(newPlaces);
		net.getFlowRelationships().addAll(newArcs);
	}

	/**
	 * This transformation creates additional places and transitions to
	 * translate exclusive decisions into explicit tasks
	 */
	public void extractXors(PTNet net) {
		List<LabeledTransition> newTransitions = new ArrayList<LabeledTransition>();
		List<Place> newPlaces = new ArrayList<Place>();
		List<FlowRelationship> newArcs = new ArrayList<FlowRelationship>();
		int index = 0;

		for (Place place : net.getPlaces())
			if (place.getSucceedingNodes().size() > 1)
				decomposeXor(place, newTransitions, newPlaces, newArcs, index++);

		// Add all new Nodes and Arcs to the lists of the net
		net.getTransitions().addAll(newTransitions);
		net.getPlaces().addAll(newPlaces);
		net.getFlowRelationships().addAll(newArcs);
	}
	
	/**
	 * This transformation creates additional nop transitions to
	 * introduce additional places
	 * for places with two names
	 * for token states with ambigous semantics
	 * to ensure transition in transition block that results in final state
	 */
	public void resolveConflicts(PTNet net) {
		List<LabeledTransition> newTransitions = new ArrayList<LabeledTransition>();
		List<Place> newPlaces = new ArrayList<Place>();
		List<FlowRelationship> newArcs = new ArrayList<FlowRelationship>();
		
		int index = 0;
		
		index = resolveDoubleNames(net, newTransitions, newPlaces, newArcs, index);
		
		resolveAmbiguity(net, newTransitions, newPlaces, newArcs, index);

		// Add all new Nodes and Arcs to the lists of the net
		net.getTransitions().addAll(newTransitions);
		net.getPlaces().addAll(newPlaces);
		net.getFlowRelationships().addAll(newArcs);
	}

	private int resolveDoubleNames(PTNet net, List<LabeledTransition> newTransitions, List<Place> newPlaces,
			List<FlowRelationship> newArcs, int index) {
		for(Place place : net.getPlaces()){
			if(place.getIncomingFlowRelationships().size() > 1) {
				for(Node precedingTransition : place.getPrecedingNodes()) {
					if(precedingTransition.getOutgoingFlowRelationships().size() > 1) {
						//add nop transition between preceding Transition and place
						addPlaceAndNopTransition(precedingTransition, place, newTransitions, newPlaces, newArcs, index);
						index++;
					}
				}
			}
		}
		return index;
	}

	private void resolveAmbiguity(PTNet net, List<LabeledTransition> newTransitions, List<Place> newPlaces,
			List<FlowRelationship> newArcs, int index) {
		for(LabeledTransition transition : net.getLabeledTransitionsAsLabeledTransitions()) {
			if(transition.getLabel().equals(JOIN)) {
				for(Node precedingPlace : transition.getPrecedingNodes()) {
					for(Node criticalTransition : precedingPlace.getPrecedingNodes()) {
						if(criticalTransition.getOutgoingFlowRelationships().size() > 1) {
							// add nop transition between precedingPlace and join
							addNopTransitionAndPlace(precedingPlace, transition, newTransitions, newPlaces, newArcs, index);
							index++;
						}
					}
				}
			}
		}
	}

	private void decomposeJoinTransition(Transition transition, List<LabeledTransition> newTransitions,
			List<Place> newPlaces, List<FlowRelationship> newArcs) {
		List<Node> nodes = transition.getPrecedingNodes();
		Node first = nodes.get(0);
		for (int i = 1; i < nodes.size(); i++) {
			// create join-transition
			LabeledTransition join = factory.createLabeledTransition();
			newTransitions.add(join);
			join.setLabel(JOIN);

			// change targets of both incoming places to the join-transition
			changeTarget(first, transition, join);
			changeTarget(nodes.get(i), transition, join);

			// new outgoing place of the join-transition
			Place target = factory.createPlace();
			newPlaces.add(target);

			connectNodes(newArcs, join, target);

			FlowRelationship arc1 = factory.createFlowRelationship();
			newArcs.add(arc1);
			arc1.setSource(target);

			if (i == nodes.size() - 1) {
				arc1.setTarget(transition);
			} else {
				first = target;
			}
		}
	}

	private void decomposeXor(Place place, List<LabeledTransition> newTransitions, List<Place> newPlaces,
			List<FlowRelationship> newArcs, int index) {

		for (FlowRelationship fr : place.getOutgoingFlowRelationships()) {

			// create xor
			LabeledTransition xor = factory.createLabeledTransition();
			newTransitions.add(xor);
			xor.setLabel(XOR + index);

			// create new sub-state
			Place state = factory.createPlace();
			newPlaces.add(state);

			connectNodes(newArcs, xor, state);

			// connect state and target of original arc
			connectNodes(newArcs, state, fr.getTarget());

			// change target of original arc to xor
			fr.setTarget(xor);
		}
	}
	private void addPlaceAndNopTransition(Node precedingTransition, Node suceedingPlace, List<LabeledTransition> newTransitions, List<Place> newPlaces,
			List<FlowRelationship> newArcs, int index) {
		// create NopTransition
		LabeledTransition nop = factory.createLabeledTransition();
		newTransitions.add(nop);
		nop.setLabel(NOP + index);
		
		// create new sub-state
		Place state = factory.createPlace();
		newPlaces.add(state);
		
		// connect state and nop
		connectNodes(newArcs, state, nop);
		
		//connect nop and suceedingPlace
		connectNodes(newArcs, nop, suceedingPlace);
		
		// change target of precedingTransition
		changeTarget(precedingTransition, suceedingPlace, state);
	}
	
	private void addNopTransitionAndPlace(Node precedingPlace, Node suceedingTransition, List<LabeledTransition> newTransitions, List<Place> newPlaces,
			List<FlowRelationship> newArcs, int index) {
		// create NopTransition
		LabeledTransition nop = factory.createLabeledTransition();
		newTransitions.add(nop);
		nop.setLabel(NOP + index);
		
		// create new sub-state
		Place state = factory.createPlace();
		newPlaces.add(state);
		
		// connect nop and state
		connectNodes(newArcs, nop, state);
		
		//connect state and suceedingTransition
		connectNodes(newArcs, state, suceedingTransition);
		
		// change target of precedingPlace
		changeTarget(precedingPlace, suceedingTransition, nop);
	}

	private void connectNodes(List<FlowRelationship> newArcs, Node source, Node target) {
		FlowRelationship arc = factory.createFlowRelationship();
		newArcs.add(arc);
		arc.setSource(source);
		arc.setTarget(target);
	}

	private void changeTarget(Node node, Node oldTarget, Node newTarget) {
		for (FlowRelationship arc : node.getOutgoingFlowRelationships()) {
			if (arc.getTarget().equals(oldTarget)) {
				arc.setTarget(newTarget);
				return;
			}
		}
	}
	
	
}
