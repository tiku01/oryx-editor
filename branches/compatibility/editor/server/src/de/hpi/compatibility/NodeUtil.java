package de.hpi.compatibility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.petrinet.Node;
import de.hpi.petrinet.Transition;

public class NodeUtil {

	public enum PartitioningMode {
		NotAligned, Init, Final, Interleaving, C1, C2
	}

	public static PartitioningMode getMode(Node n, Set<Transition> nonInterleavingC1,
			Set<Transition> nonInterleavingC2, Set<Transition> interleaving) {
		if (n.getPrecedingNodes().isEmpty())
			return PartitioningMode.Init;
		if (n.getSucceedingNodes().isEmpty())
			return PartitioningMode.Final;
		if (nonInterleavingC1.contains(n))
			return PartitioningMode.C1;
		if (nonInterleavingC2.contains(n))
			return PartitioningMode.C2;
		if (interleaving.contains(n))
			return PartitioningMode.Interleaving;
		return PartitioningMode.NotAligned;
	}

	public static Set<Node> getNextNodesWithDifferentMode(Node current, Set<Transition> nonInterleavingC1,
			Set<Transition> nonInterleavingC2, Set<Transition> interleaving) {

		Set<Node> result = new HashSet<Node>();
		Set<Node> visited = new HashSet<Node>();
		visited.add(current);

		List<Node> sucNodes = new ArrayList<Node>();
		sucNodes.addAll(current.getSucceedingNodes());
		PartitioningMode mode = NodeUtil.getMode(current, nonInterleavingC1, nonInterleavingC2, interleaving);

		while (!(sucNodes.isEmpty())) {
			Node suc = sucNodes.remove(0);
			visited.add(suc);
			PartitioningMode sucMode = NodeUtil.getMode(suc, nonInterleavingC1, nonInterleavingC2, interleaving);
			if (sucMode != PartitioningMode.NotAligned && sucMode != mode) {
				result.add(suc);
			} else {
				for (Node n : suc.getSucceedingNodes()) {
					if (!(visited.contains(n))) {
						sucNodes.add(n);
					}
				}
			}
		}
		return result;
	}

}
