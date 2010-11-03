package de.hpi.compatibility;

import java.util.HashSet;
import java.util.Set;

import nl.tue.tm.is.graph.TwoTransitionSets;
import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.compatibility.NodeUtil.PartitioningMode;
import de.hpi.compatibility.PathInfo.Part;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Transition;

public class SoftCorrespondenceAnalysis extends CorrespondenceAnalysis {

	public SoftCorrespondenceAnalysis(BehaviouralProfile profile1, BehaviouralProfile profile2,
			Set<TwoTransitionSets> correspondences) {
		super(profile1, profile2, correspondences);

	}

	@Override
	protected boolean check(PTNet net1, PTNet net2, Set<Transition> interleavingInNet1,
			Set<Transition> interleavingInNet2, Set<Transition> c1InNet1, Set<Transition> c2InNet1,
			Set<Transition> c1InNet2, Set<Transition> c2InNet2) {

		// Prepare service classes for both models
		PTNetCapsule parent = new PTNetCapsule(net1, interleavingInNet1, c1InNet1, c2InNet1);
		PTNetCapsule child = new PTNetCapsule(net2, interleavingInNet2, c1InNet2, c2InNet2);

		Set<PathInfo> parentPaths = getPathInfos(parent);
		Set<PathInfo> childPaths = getPathInfos(child);

		return pathInfoSetsAreCompatible(parentPaths, childPaths);
	}

	private static Set<PathInfo> getPathInfos(PTNetCapsule net) {

		Set<Node> startNodes = net.getNextNodesWithDifferentMode(net.getInitialPlace());
		Set<PathInfo> allPaths = new HashSet<PathInfo>();
		for (Node startNode : startNodes) {
			Set<PathInfo> paths = new HashSet<PathInfo>();
			getEndings(startNode, net, paths, startNode);
			allPaths.addAll(paths);
		}
		return allPaths;
	}

	private static void getEndings(Node node, PTNetCapsule net, Set<PathInfo> paths, Node startNode) {
		for (Node succeedingNode : net.getNextNodesWithDifferentMode(node)) {
			// If (startNode is followed by finalNode)
			// path is exclusive or complete interleaving
			if (node.equals(startNode) && net.getMode(succeedingNode) == PartitioningMode.Final) {
				paths.add(new PathInfo(net.getMode(startNode)));
			} else if (net.getMode(succeedingNode) == PartitioningMode.Final) {
				paths.add(new PathInfo(net.getMode(startNode), net.getMode(node)));
			}
			// if succeedingNode != finalNode, inspect its succeeding nodes.
			else
				getEndings(succeedingNode, net, paths, startNode);
		}
	}

	/**
	 * returns false if there is a path in the parent model that has no matching
	 * path in the child model
	 */
	private static boolean pathInfoSetsAreCompatible(Set<PathInfo> parentPaths, Set<PathInfo> childPaths) {
		// types of path that can occur in parent and matches
		// (1) exclusive path -> child must have an equal path
		// (2) full interleaving -> child must have an equal path
		// (3) ordered starts -> child must have a path with ordered starts
		//     end must equal or interleaving
		// (4) ordered ends -> child must have a path with ordered ends
		//     start must be equal or interleaving
		// (5) ordered starts and ends -> child must have a path with
		// either ordered start or end

		for (PathInfo parentPath : parentPaths) {
			// check for (1), (2) and other equal paths
			if (childPaths.contains(parentPath))
				continue;
			// check for (3)
			if (parentPath.hasOrderedStart()) {
				if (haveCompatibleStarts(childPaths, parentPath))
					continue;
			}
			// check for (4)
			if (parentPath.hasOrderedEnd()) {
				if (haveCompatibleEnds(childPaths, parentPath))
					continue;
			}
			// (5) is implicitly covered by checking for (3) and (4)
			return false;
		}

		return true;
	}

	private static boolean haveCompatibleStarts(Set<PathInfo> childPaths, PathInfo parentPath) {
		boolean found = false;
		for (PathInfo childPath : childPaths) {
			if (childPath.getStartPart() == parentPath.getStartPart()) {
				if (childPath.getFinalPart() == parentPath.getFinalPart()
						|| childPath.getFinalPart() == Part.Interleaving) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	private static boolean haveCompatibleEnds(Set<PathInfo> childPaths, PathInfo parentPath) {
		boolean found = false;
		for (PathInfo childPath : childPaths) {
			if (childPath.getFinalPart() == parentPath.getFinalPart()) {
				if (childPath.getStartPart() == parentPath.getStartPart()
						|| childPath.getStartPart() == Part.Interleaving) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	private class PTNetCapsule {
		private PTNet net;
		private Set<Transition> interleaving;
		private Set<Transition> c1;
		private Set<Transition> c2;

		public PTNetCapsule(PTNet net, Set<Transition> interleaving, Set<Transition> c1, Set<Transition> c2) {
			this.net = net;
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

		public Node getInitialPlace() {
			return net.getInitialPlace();
		}
	}

}