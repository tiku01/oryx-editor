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

	public Preprocessing() {
		factory = new PTNetFactory();
	}

	/**
	 * This transformation creates additional places and transitions to split
	 * each parallel join into pairwise joins independent from the original join
	 * transition
	 */
	public PTNet decomposeJoinTransitions(PTNet net) {
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
		return net;
	}

	/**
	 * This transformation creates additional places and transitions to
	 * translate exclusive decisions into explicit tasks
	 */
	public PTNet extractXors(PTNet net) {
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
		return net;
	}

	private void decomposeJoinTransition(Transition transition, List<LabeledTransition> newTransitions,
			List<Place> newPlaces, List<FlowRelationship> newArcs) {
		List<Node> nodes = transition.getPrecedingNodes();
		Node first = nodes.get(0);
		for (int i = 1; i < nodes.size(); i++) {
			// create join-transition
			LabeledTransition join = factory.createLabeledTransition();
			newTransitions.add(join);
			join.setLabel("join");

			// change targets of both incoming places to the join-transition
			changeTarget(first, transition, join);
			changeTarget(nodes.get(i), transition, join);

			// new outgoing place of the join-transition
			Place target = factory.createPlace();
			newPlaces.add(target);

			FlowRelationship arc = factory.createFlowRelationship();
			newArcs.add(arc);
			arc.setSource(join);
			arc.setTarget(target);

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
			xor.setLabel("xor_" + index);
			
			// create new sub-state
			Place state = factory.createPlace();
			newPlaces.add(state);
			
			// connect xor and state
			FlowRelationship arc = factory.createFlowRelationship();
			newArcs.add(arc);
			arc.setSource(xor);
			arc.setTarget(state);

			// connect state and target of original arc
			FlowRelationship arc1 = factory.createFlowRelationship();
			newArcs.add(arc1);
			arc1.setSource(state);
			arc1.setTarget(fr.getTarget());

			// change target of original arc to xor
			fr.setTarget(xor);
		}
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
