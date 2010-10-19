package de.hpi.compatibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.TwoTransitionSets;
import de.hpi.PTnet.PTNet;
import de.hpi.bp.BPCreatorNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.compatibility.NodeUtil.PartitioningMode;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Transition;

public class CorrespondenceAnalysis {
	protected static boolean DEBUG = true;

	protected BehaviouralProfile profile1 = null;
	protected BehaviouralProfile profile2 = null;
	protected List<TwoTransitionSets> correspondences = null;

	protected Result statistics = new Result();

	public CorrespondenceAnalysis(BehaviouralProfile profile1, BehaviouralProfile profile2,
			Set<TwoTransitionSets> correspondences) {
		this.profile1 = profile1;
		this.profile2 = profile2;
		this.correspondences = new ArrayList<TwoTransitionSets>(correspondences);
	}

	public void checkCompatibility() {

		long start = System.currentTimeMillis();
		checkProjectionCompatibility();
		long stop = System.currentTimeMillis();
		if (DEBUG)
			System.out.println("Proj time: " + (stop - start) + " ms.");

		// Derive encapsulated nets
		Set<Node> alignedInNet1 = new HashSet<Node>();
		Set<Node> alignedInNet2 = new HashSet<Node>();
		for (TwoTransitionSets c : correspondences) {
			alignedInNet1.addAll(c.s1);
			alignedInNet2.addAll(c.s2);
		}
		PTNet encapNet1 = Util.deriveEncapNet(profile1.getNet(), alignedInNet1);
		PTNet encapNet2 = Util.deriveEncapNet(profile2.getNet(), alignedInNet2);

		checkProtocolCompatibility(encapNet1, encapNet2);

		stop = System.currentTimeMillis();
		if (DEBUG)
			System.out.println("Prot time: " + (stop - start) + " ms.");

	}

	protected void checkProjectionCompatibility() {

		for (int i = 0; i < correspondences.size(); i++) {
			TwoTransitionSets c1 = correspondences.get(i);
			if (c1.isComplex())
				statistics.addComplexProjectionCorrespondence();
			for (int j = i + 1; j < correspondences.size(); j++) {
				TwoTransitionSets c2 = correspondences.get(j);
				boolean areCompatible = correspondencesAreCompatible(c1, c2, profile1, profile2);
				statistics.addProjectionCorrespondenceResult(c1, c2, areCompatible);
			}
		}
	}

	protected void checkProtocolCompatibility(PTNet encapNet1, PTNet encapNet2) {
		if (encapNet1.isWorkflowNet() && encapNet2.isWorkflowNet()) {

			BehaviouralProfile profileEncap1 = BPCreatorNet.getInstance().deriveBehaviouralProfile(encapNet1);
			BehaviouralProfile profileEncap2 = BPCreatorNet.getInstance().deriveBehaviouralProfile(encapNet2);

			for (int i = 0; i < correspondences.size(); i++) {
				TwoTransitionSets c1 = correspondences.get(i);
				if (!Util.allTransitionsInNets(profileEncap1, profileEncap2, c1))
					continue;
				if (c1.isComplex())
					statistics.addComplexProtocolCorrespondence();
				for (int j = i + 1; j < correspondences.size(); j++) {
					TwoTransitionSets c2 = correspondences.get(j);
					if (!Util.allTransitionsInNets(profileEncap1, profileEncap2, c2))
						continue;
					TwoTransitionSets c1a = new TwoTransitionSets(Util.adaptSet(profileEncap1.getNet(), c1.s1),
							Util.adaptSet(profileEncap2.getNet(), c1.s2));
					TwoTransitionSets c2a = new TwoTransitionSets(Util.adaptSet(profileEncap1.getNet(), c2.s1),
							Util.adaptSet(profileEncap2.getNet(), c2.s2));

					boolean areCompatible = correspondencesAreCompatible(c1a, c2a, profileEncap1, profileEncap2);
					statistics.addProtocolCorrespondenceResult(c1, c2, areCompatible);
				}
			}
		}
	}

	protected boolean correspondencesAreCompatible(TwoTransitionSets c1, TwoTransitionSets c2,
			BehaviouralProfile profile1, BehaviouralProfile profile2) {
		boolean result = true;

		/*
		 * Do preprocessing
		 */
		PTNet preProcessedNet1 = Util.getSubnetOfClone(profile1.getNet(),
				Util.getPreProcessingNodes(profile1, c1.s1, c2.s1));
		PTNet preProcessedNet2 = Util.getSubnetOfClone(profile2.getNet(),
				Util.getPreProcessingNodes(profile2, c1.s2, c2.s2));

		if (!preProcessedNet1.isWorkflowNet())
			System.err.println("Preprocessed net1 is not WF!");
		if (!preProcessedNet2.isWorkflowNet())
			System.err.println("Preprocessed net2 is not WF!");

		/*
		 * Extract interleaving transitions
		 */
		Set<Transition> interleavingInNet1 = Util.adaptSet(preProcessedNet1,
				Util.getInterleavingTransitions(profile1, c1.s1, c2.s1));
		Set<Transition> interleavingInNet2 = Util.adaptSet(preProcessedNet2,
				Util.getInterleavingTransitions(profile2, c1.s2, c2.s2));

		/*
		 * Derive non-interleaving
		 */
		Set<Transition> nonInterleavingC1InNet1 = new HashSet<Transition>(Util.adaptSet(preProcessedNet1, c1.s1));
		nonInterleavingC1InNet1.removeAll(interleavingInNet1);
		Set<Transition> nonInterleavingC2InNet1 = new HashSet<Transition>(Util.adaptSet(preProcessedNet1, c2.s1));
		nonInterleavingC2InNet1.removeAll(interleavingInNet1);

		Set<Transition> nonInterleavingC1InNet2 = new HashSet<Transition>(Util.adaptSet(preProcessedNet2, c1.s2));
		nonInterleavingC1InNet2.removeAll(interleavingInNet2);
		Set<Transition> nonInterleavingC2InNet2 = new HashSet<Transition>(Util.adaptSet(preProcessedNet2, c2.s2));
		nonInterleavingC2InNet2.removeAll(interleavingInNet2);

		/*
		 * Check both directions
		 */
		result &= correspondencesAreCompatibleInOneDirection(preProcessedNet1, preProcessedNet2,
				nonInterleavingC1InNet1, nonInterleavingC2InNet1, interleavingInNet1, nonInterleavingC1InNet2,
				nonInterleavingC2InNet2, interleavingInNet2);

		result &= correspondencesAreCompatibleInOneDirection(preProcessedNet2, preProcessedNet1,
				nonInterleavingC1InNet2, nonInterleavingC2InNet2, interleavingInNet2, nonInterleavingC1InNet1,
				nonInterleavingC2InNet1, interleavingInNet1);

		return result;
	}

	protected boolean correspondencesAreCompatibleInOneDirection(PTNet net1, PTNet net2,
			Set<Transition> nonInterleavingC1InNet1, Set<Transition> nonInterleavingC2InNet1,
			Set<Transition> interleavingInNet1, Set<Transition> nonInterleavingC1InNet2,
			Set<Transition> nonInterleavingC2InNet2, Set<Transition> interleavingInNet2) {

		boolean result = true;

		Set<Transition> cTransitionsInNet1 = new HashSet<Transition>(nonInterleavingC1InNet1);
		cTransitionsInNet1.addAll(nonInterleavingC2InNet1);
		cTransitionsInNet1.addAll(interleavingInNet1);

		Map<Node, Set<Node>> goodMatches = new HashMap<Node, Set<Node>>();
		Set<Node> s = new HashSet<Node>();
		s.add(net2.getInitialPlace());
		goodMatches.put(net1.getInitialPlace(), s);

		List<Node> toCheck = new ArrayList<Node>();
		toCheck.add(net1.getInitialPlace());

		while (!(toCheck.isEmpty())) {
			Node current = toCheck.remove(0);
			Set<Node> nextNodes = NodeUtil.getNextNodesWithDifferentMode(current, nonInterleavingC1InNet1,
					nonInterleavingC2InNet1, interleavingInNet1);
			for (Node nextNode : nextNodes) {
				if (goodMatches.containsKey(nextNode))
					continue;
				PartitioningMode modeNextNode = NodeUtil.getMode(nextNode, nonInterleavingC1InNet1,
						nonInterleavingC2InNet1, interleavingInNet1);
				boolean foundGoodNodeForNextNode = false;
				Set<Node> goodNodesForNextNode = new HashSet<Node>();
				for (Node goodNode : goodMatches.get(current)) {
					Set<Node> candidateNodes = NodeUtil.getNextNodesWithDifferentMode(goodNode,
							nonInterleavingC1InNet2, nonInterleavingC2InNet2, interleavingInNet2);
					for (Node candidateNode : candidateNodes) {
						PartitioningMode modeCandidateNode = NodeUtil.getMode(candidateNode, nonInterleavingC1InNet2,
								nonInterleavingC2InNet2, interleavingInNet2);
						if (modeNextNode == modeCandidateNode) {
							foundGoodNodeForNextNode = true;
							goodNodesForNextNode.add(candidateNode);
						}
					}
				}
				if (!foundGoodNodeForNextNode) {
					result = false;
					break;
				}
				goodMatches.put(nextNode, goodNodesForNextNode);
				if (!(nextNode.getSucceedingNodes().isEmpty()))
					toCheck.add(nextNode);

			}
		}

		return result;
	}

	public String getResult() {
		return statistics.getResult(correspondences);
	}

	@Override
	public String toString() {
		return profile1.getNet().getId() + " - " + profile2.getNet().getId() + "\n" + correspondences;
	}
}
