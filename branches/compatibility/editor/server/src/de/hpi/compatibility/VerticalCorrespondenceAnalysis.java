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

	private PTNetCapsule model1;
	private PTNetCapsule model2;
	private Helper status;

	public VerticalCorrespondenceAnalysis(BehaviouralProfile profile1, BehaviouralProfile profile2,
			Set<TwoTransitionSets> correspondences) {
		super(profile1, profile2, correspondences);

	}

	@Override
	protected boolean check(PTNet net1, PTNet net2, Set<Transition> interleavingInNet1,
			Set<Transition> interleavingInNet2, Set<Transition> c1InNet1, Set<Transition> c2InNet1,
			Set<Transition> c1InNet2, Set<Transition> c2InNet2) {

		// Prepare service classes for both models
		model1 = new PTNetCapsule(interleavingInNet1, c1InNet1, c2InNet1);
		model2 = new PTNetCapsule(interleavingInNet2, c1InNet2, c2InNet2);
		status = new Helper();

		// Match initial places
		Set<Node> s = new HashSet<Node>();
		s.add(net2.getInitialPlace());
		status.addMatch(net1.getInitialPlace(), s);

		return check();
	}

	private boolean check() {
		boolean result = true;
		while (!status.toCheck()) {
			Node currentNode = status.getCurrentNode();
			Set<Node> nextNodes = model1.getNextNodesWithDifferentMode(currentNode);
			for (Node nextNode : nextNodes) {
				if (status.alreadyChecked(nextNode))
					continue;

				Set<Node> matches = getMatchingNodes(currentNode, nextNode);

				if (matches.isEmpty()) {
					result = false;
					break;
				}
				status.addMatch(nextNode, matches);
			}
		}
		return result;
	}

	private Set<Node> getMatchingNodes(Node currentNode, Node nextNode) {
		Set<Node> matches = new HashSet<Node>();
		for (Node currentNodeNet2 : status.getMatches(currentNode)) {
			PartitioningMode modeCurrentNode = model1.getMode(currentNode);
			PartitioningMode modeCurrentNodeNet2 = model2.getMode(currentNodeNet2);

			Set<Node> nextNodesNet2 = model2.getNextNodesWithDifferentMode(currentNodeNet2);
			
			// check for equal trace partitioning
			for (Node nextNodeNet2 : nextNodesNet2)
				if (haveSameMode(nextNode, nextNodeNet2))
					matches.add(nextNodeNet2);
			
			if (modeCurrentNode == modeCurrentNodeNet2) {
				matches.addAll(checkForParallelizations(modeCurrentNode, nextNode, nextNodesNet2));
				matches.addAll(checkForSequencings(modeCurrentNode, nextNode, nextNodesNet2));
			}
		}
		return matches;
	}

	/**
	 * checks whether both nodes have the same PartitioningMode
	 */
	private boolean haveSameMode(Node nextNode, Node nextNodeNet2) {
		if (model1.getMode(nextNode) == model2.getMode(nextNodeNet2)) {
			return true;
		}
		return false;
	}

	/**
	 * match (A, B) -> (A, interleaving) optional: extend to (A, B) -> (A,
	 * interleaving, B)
	 */
	private Set<Node> checkForParallelizations(PartitioningMode modeCurrentNode, Node nextNode, Set<Node> nextNodesNet2) {
		Set<Node> matches = new HashSet<Node>();
		PartitioningMode modeNextNode = model1.getMode(nextNode);
		for (Node nextNodeNet2 : nextNodesNet2) {
			PartitioningMode modeNextNodeNet2 = model2.getMode(nextNodeNet2);

			if (modeNextNode != PartitioningMode.Interleaving && modeNextNode != PartitioningMode.Final
					&& modeNextNodeNet2 == PartitioningMode.Interleaving) {
				matches.add(nextNodeNet2);

				// Extending ... CAUTION: A must not be initial
				if (modeCurrentNode != PartitioningMode.Init) {
					Set<Node> nextCandidateNodes = model2.getNextNodesWithDifferentMode(nextNodeNet2);
					for (Node nextCandidateNode : nextCandidateNodes) {
						if (haveSameMode(nextNode, nextCandidateNode))
							matches.add(nextCandidateNode);
					}
				}
			}
		}
		return matches;
	}

	/**
	 * match (A, interleaving) -> (A, B) optional: extend to (A, interleaving, B) -> (A, B)
	 */
	private Set<Node> checkForSequencings(PartitioningMode modeCurrentNode, Node nextNode, Set<Node> nextNodesNet2) {
		Set<Node> matches = new HashSet<Node>();
		PartitioningMode modeNextNode = model1.getMode(nextNode);

		if (modeCurrentNode != PartitioningMode.Init && modeNextNode == PartitioningMode.Interleaving) {
			PartitioningMode refForExtension = null;
			for (Node nextNodeNet2 : nextNodesNet2) {
				PartitioningMode modeNextNodeNet2 = model2.getMode(nextNodeNet2);
				if (modeNextNodeNet2 != PartitioningMode.Interleaving && modeNextNodeNet2 != PartitioningMode.Final) {
					// all goodNextNodes have the same mode, either c1 or c2
					if (refForExtension == null)
						refForExtension = modeNextNodeNet2;
					matches.add(nextNodeNet2);
				}
			}
			// Extending ...
			if (!matches.isEmpty()) {
				Set<Node> nextNextNodes = model1.getNextNodesWithDifferentMode(nextNode);
				for (Node nextNextNode : nextNextNodes) {
					PartitioningMode modeNextNextNode = model1.getMode(nextNextNode);
					if (modeNextNextNode == refForExtension) {
						status.addMatch(nextNextNode, matches);
					}
				}
			}
		}
		return matches;
	}

	private class PTNetCapsule {
		private Set<Transition> interleaving;
		private Set<Transition> c1;
		private Set<Transition> c2;

		public PTNetCapsule(Set<Transition> interleaving, Set<Transition> c1, Set<Transition> c2) {
			this.interleaving = interleaving;
			this.c1 = c1;
			this.c2 = c2;
		}

		public Set<Node> getNextNodesWithDifferentMode(Node node) {
			return NodeUtil.getNextNodesWithDifferentMode(node, c1, c2, interleaving);
		}

		public PartitioningMode getMode(Node node) {
			return NodeUtil.getMode(node, c1, c2, interleaving);
		}
	}

	private class Helper {
		private Map<Node, Set<Node>> goodMatches;
		private List<Node> toCheck;

		public Helper() {
			goodMatches = new HashMap<Node, Set<Node>>();
			toCheck = new ArrayList<Node>();
		}

		public void addMatch(Node key, Set<Node> value) {
			goodMatches.put(key, value);
			if (!(key.getSucceedingNodes().isEmpty()))
				toCheck.add(key);
		}

		public boolean toCheck() {
			return toCheck.isEmpty();
		}

		public Node getCurrentNode() {
			return toCheck.remove(0);
		}

		public boolean alreadyChecked(Node node) {
			return goodMatches.containsKey(node);
		}

		public Set<Node> getMatches(Node node) {
			return goodMatches.get(node);
		}

	}
}
