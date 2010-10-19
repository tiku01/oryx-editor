package de.hpi.compatibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.TwoTransitionSets;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.compatibility.NodeUtil.PartitioningMode;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Transition;

public class VerticalCorrespondenceAnalysis extends CorrespondenceAnalysis {

	public VerticalCorrespondenceAnalysis(BehaviouralProfile profile1, BehaviouralProfile profile2,
			Set<TwoTransitionSets> correspondences) {
		super(profile1, profile2, correspondences);

	}

	@Override
	protected boolean check(PTNet net1, PTNet net2, Set<Transition> interleavingInNet1,
			Set<Transition> interleavingInNet2, Set<Transition> nonInterleavingC1InNet1,
			Set<Transition> nonInterleavingC2InNet1, Set<Transition> nonInterleavingC1InNet2,
			Set<Transition> nonInterleavingC2InNet2) {

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
						// if there is no node with the same mode, look for interleaving nodes
						if ((modeNextNode.equals(PartitioningMode.C1) || modeNextNode.equals(PartitioningMode.C2)) && modeCandidateNode.equals(PartitioningMode.Interleaving)) {
							foundGoodNodeForNextNode = true;
							goodNodesForNextNode.add(candidateNode);
							// look for node with mode of nextNode after the interleaving part
							Set<Node> nextCandidateNodes = NodeUtil.getNextNodesWithDifferentMode(candidateNode,
									nonInterleavingC1InNet2, nonInterleavingC2InNet2, interleavingInNet2);
							for (Node nextCandidateNode : nextCandidateNodes) {
								PartitioningMode modeNextCandidateNode = NodeUtil.getMode(nextCandidateNode,
										nonInterleavingC1InNet2, nonInterleavingC2InNet2, interleavingInNet2);
								if (modeNextNode == modeNextCandidateNode) {
									goodNodesForNextNode.add(nextCandidateNode);
								}
							}
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

}
