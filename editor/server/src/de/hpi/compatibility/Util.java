package de.hpi.compatibility;

import java.util.HashSet;
import java.util.Set;

import nl.tue.tm.is.graph.TwoTransitionSets;
import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class Util {
	public static PTNet deriveEncapNet(PTNet origNet, Set<Node> nodesToKeep) {
		PTNet net = null;

		Place i = null;
		Place o = null;

		try {
			net = (PTNet) origNet.clone();
			Set<Transition> tToRemove = new HashSet<Transition>();
			for (Node n : net.getNodes()) {
				if (n.getId().equals(origNet.getInitialPlace().getId()))
					i = (Place) n;
				if (n.getId().equals(origNet.getFinalPlace().getId()))
					o = (Place) n;

				boolean found = false;
				for (Node toKeep : nodesToKeep) {
					if (n.getId().equals(toKeep.getId()))
						found = true;
				}
				if ((!(found)) && (n instanceof LabeledTransition)) {
					if (!((LabeledTransition) n).getLabel().equals("tau")) {
						if (!((LabeledTransition) n).getLabel().matches("t\\d+")) {
							tToRemove.add((Transition) n);
						}
					}
				}
			}

			Set<FlowRelationship> fToRemove = new HashSet<FlowRelationship>();
			for (FlowRelationship f : net.getFlowRelationships()) {
				if (tToRemove.contains(f.getSource()) || tToRemove.contains(f.getTarget()))
					fToRemove.add(f);
			}

			net.getTransitions().removeAll(tToRemove);
			net.getFlowRelationships().removeAll(fToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}

		stripToWFNet(net, i, o);

		return net;
	}

	/**
	 * Remove everything that is not consistent with the definition of a
	 * workflow net
	 */
	private static void stripToWFNet(PTNet net, Place i, Place o) {

		Set<Transition> tToRemove = new HashSet<Transition>();
		Set<Place> pToRemove = new HashSet<Place>();
		for (Node n : net.getNodes()) {
			if (n.equals(i) || n.equals(o))
				continue;
			if ((!(net.getTransitiveClosure().isPath(i, n))) || (!(net.getTransitiveClosure().isPath(n, o)))) {
				if (n instanceof Transition) {
					tToRemove.add((Transition) n);
				} else {
					pToRemove.add((Place) n);
				}
			}
		}

		Set<FlowRelationship> fToRemove = new HashSet<FlowRelationship>();
		for (FlowRelationship f : net.getFlowRelationships()) {
			if (tToRemove.contains(f.getSource()) || tToRemove.contains(f.getTarget()))
				fToRemove.add(f);
			if (pToRemove.contains(f.getSource()) || pToRemove.contains(f.getTarget()))
				fToRemove.add(f);
		}

		net.getPlaces().removeAll(pToRemove);
		net.getTransitions().removeAll(tToRemove);
		net.getFlowRelationships().removeAll(fToRemove);

		net.setTransitiveClosure(null);
	}

	/**
	 * Clones the net and removes all nodes given for the original net from the
	 * clone.
	 */
	public static PTNet getSubnetOfClone(PTNet originalNet, Set<Node> nodesToRemove) {
		PTNet net = null;
		try {
			net = (PTNet) originalNet.clone();

			Set<Place> pToRemove = new HashSet<Place>();
			Set<Transition> tToRemove = new HashSet<Transition>();
			for (Node n : net.getNodes()) {
				for (Node toRemove : nodesToRemove) {
					if (n.getId().equals(toRemove.getId())) {
						if (n instanceof Place)
							pToRemove.add((Place) n);
						else
							tToRemove.add((Transition) n);
					}
				}
			}

			Set<FlowRelationship> fToRemove = new HashSet<FlowRelationship>();
			for (FlowRelationship f : net.getFlowRelationships()) {
				if (pToRemove.contains(f.getSource()) || pToRemove.contains(f.getTarget())
						|| tToRemove.contains(f.getSource()) || tToRemove.contains(f.getTarget()))
					fToRemove.add(f);
			}

			net.getPlaces().removeAll(pToRemove);
			net.getTransitions().removeAll(tToRemove);
			net.getFlowRelationships().removeAll(fToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return net;
	}

	public static boolean allTransitionsInNets(BehaviouralProfile profile1, BehaviouralProfile profile2,
			TwoTransitionSets c) {
		return profile1.getNet().getTransitions().containsAll(adaptSet(profile1.getNet(), c.s1))
				&& profile2.getNet().getTransitions().containsAll(adaptSet(profile2.getNet(), c.s2));
	}

	public static Set<Transition> adaptSet(PTNet newNet, Set<Transition> transitions) {
		Set<Transition> result = new HashSet<Transition>();
		for (Transition t : transitions) {
			for (Transition t2 : newNet.getTransitions()) {
				if (t.getId().equals(t2.getId()))
					result.add(t2);
			}
		}

		return result;
	}

	public static Set<Node> getPreProcessingNodes(BehaviouralProfile bp, Set<Transition> c1, Set<Transition> c2) {
		Set<Node> result = new HashSet<Node>();

		Set<Transition> cTransitions = new HashSet<Transition>(c1);
		cTransitions.addAll(c2);

		for (Node n : bp.getNet().getNodes()) {
			if (cTransitions.contains(n))
				continue;
			for (Transition t1 : cTransitions)
				if (bp.getConcurrencyMatrix().areTrueConcurrent(n, t1)) {
					boolean toRemove = true;
					for (Transition t2 : cTransitions)
						if (bp.getConcurrencyMatrix().areTrueConcurrent(t1, t2))
							toRemove &= (bp.getConcurrencyMatrix().areTrueConcurrent(n, t2) || bp.areExclusive(n, t2));
					if (toRemove)
						result.add(n);
				}
		}
		return result;
	}
	
	public static Set<Transition> getInterleavingTransitions(BehaviouralProfile bp, Set<Transition> c1, Set<Transition> c2) {
		Set<Transition> result = new HashSet<Transition>();

		for (Transition t1 : c1)
			for (Transition t2 : c2)
				if (bp.getConcurrencyMatrix().areTrueConcurrent(t1, t2)) {
					result.add(t1);
					result.add(t2);
				}

		return result;
	}
}
