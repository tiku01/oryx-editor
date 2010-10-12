package de.hpi.compatibility;

import java.io.PrintStream;
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
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class CorrespondenceAnalysis {
	
	protected BehaviouralProfile profile1 = null; 
	protected BehaviouralProfile profile2 = null; 
	protected List<TwoTransitionSets> correspondences = null;

	protected Map<String,Boolean> compatibilityResults = new HashMap<String, Boolean>();
	
	protected int nrComplexCorrespondences = 0;
	protected int nrSimpleCompatible = 0;
	protected int nrComplexCompatible = 0;
	protected int nrSimpleIncompatible = 0;
	protected int nrComplexIncompatible = 0;

	protected int nrComplexProtocolCorrespondences = 0;
	protected int nrSimpleProtocolCompatible = 0;
	protected int nrComplexProtocolCompatible = 0;
	protected int nrSimpleProtocolIncompatible = 0;
	protected int nrComplexProtocolIncompatible = 0;

	
	public CorrespondenceAnalysis(BehaviouralProfile profile1, BehaviouralProfile profile2, Set<TwoTransitionSets> correspondences) {
		this.profile1 = profile1;
		this.profile2 = profile2;
		this.correspondences = new ArrayList<TwoTransitionSets>(correspondences);
	}
	
	public boolean isComplex(TwoTransitionSets tts){
		return (tts.s1.size() > 1) || (tts.s2.size() > 1);
	}
	
	public void checkCompatibility() {
		/*
		 * Projection Compatibility
		 */
//		for (int i = 0; i < correspondences.size(); i++) {
//			TwoTransitionSets c1 = correspondences.get(i);
//			if (isComplex(c1)) nrComplexCorrespondences++;
//			for (int j = i+1; j < correspondences.size(); j++) {
//				TwoTransitionSets c2 = correspondences.get(j);
//				boolean areCompatible = correspondencesAreCompatible(c1,c2,profile1,profile2);
//				if (areCompatible){
//					if (isComplex(c1) || isComplex(c2)) nrComplexCompatible++; else nrSimpleCompatible++;
//				}else{
//					if (isComplex(c1) || isComplex(c2)) nrComplexIncompatible++; else nrSimpleIncompatible++;					
//				}
//				compatibilityResults.put(c1.toString() + " - " + c2.toString(), areCompatible);
//			}
//		}
		/*
		 * Derive encapsulated nets
		 */
		Set<Node> alignedInNet1 = new HashSet<Node>();
		Set<Node> alignedInNet2 = new HashSet<Node>();
		for (TwoTransitionSets c : correspondences) {
			alignedInNet1.addAll(c.s1);
			alignedInNet2.addAll(c.s2);
		}
		PTNet encapNet1 = deriveEncapNet(profile1.getNet(),alignedInNet1);
		PTNet encapNet2 = deriveEncapNet(profile2.getNet(),alignedInNet2);
		
		if (encapNet1.isWorkflowNet() && encapNet2.isWorkflowNet()) {
			/*
			 * Protocol Compatibility
			 */
			BehaviouralProfile profileEncap1 = BPCreatorNet.getInstance().deriveBehaviouralProfile(encapNet1);
			BehaviouralProfile profileEncap2 = BPCreatorNet.getInstance().deriveBehaviouralProfile(encapNet2);

			for (int i = 0; i < correspondences.size(); i++) {
				TwoTransitionSets c1 = correspondences.get(i);
				if (!allTransitionsInNets(profileEncap1,profileEncap2,c1))
					continue;
				if (isComplex(c1)) nrComplexProtocolCorrespondences++;
				for (int j = i+1; j < correspondences.size(); j++) {
					TwoTransitionSets c2 = correspondences.get(j);
					if (!allTransitionsInNets(profileEncap1,profileEncap2,c2))
						continue;
					TwoTransitionSets c1a = new TwoTransitionSets(adaptSet(profileEncap1.getNet(), c1.s1), adaptSet(profileEncap2.getNet(), c1.s2));
					TwoTransitionSets c2a = new TwoTransitionSets(adaptSet(profileEncap1.getNet(), c2.s1), adaptSet(profileEncap2.getNet(), c2.s2));
					
					boolean areCompatible = correspondencesAreCompatible(c1a,c2a,profileEncap1,profileEncap2);
					if (areCompatible){
						if (isComplex(c1) || isComplex(c2)) nrComplexProtocolCompatible++; else nrSimpleProtocolCompatible++;
					}else{
						if (isComplex(c1) || isComplex(c2)) nrComplexProtocolIncompatible++; else nrSimpleProtocolIncompatible++;
					}
				}
			}
		}
	}
	
	protected boolean allTransitionsInNets(BehaviouralProfile profile1, BehaviouralProfile profile2, TwoTransitionSets c) {
		return profile1.getNet().getTransitions().containsAll(adaptSet(profile1.getNet(),c.s1)) && profile2.getNet().getTransitions().containsAll(adaptSet(profile2.getNet(),c.s2));
	}
	
	protected PTNet deriveEncapNet(PTNet origNet, Set<Node> nodesToKeep) {
		PTNet net = null;
		
		Place i = null;
		Place o = null;
		
		try {
			net = (PTNet)origNet.clone();
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
				// 
				if ((!(found)) && (n instanceof LabeledTransition)) {
					if (!((LabeledTransition)n).getLabel().equals("tau")) {
						if (!((LabeledTransition)n).getLabel().matches("t\\d+")) {
							tToRemove.add((Transition)n);
						}
					}
				}
			}

			Set<FlowRelationship> fToRemove = new HashSet<FlowRelationship>();
			for(FlowRelationship f : net.getFlowRelationships()) {
				if (tToRemove.contains(f.getSource()) || tToRemove.contains(f.getTarget()))
					fToRemove.add(f);
			}

			net.getTransitions().removeAll(tToRemove);
			net.getFlowRelationships().removeAll(fToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Remove everything that is not consistent with the definition of a workflow net
		 */
		Set<Transition> tToRemove = new HashSet<Transition>();
		Set<Place> pToRemove = new HashSet<Place>();
		for (Node n : net.getNodes()) {
			if (n.equals(i) || n.equals(o))
				continue;
			if ((!(net.getTransitiveClosure().isPath(i, n))) || (!(net.getTransitiveClosure().isPath(n, o)))) {
				if (n instanceof Transition) {
					tToRemove.add((Transition)n);
				}
				else {
					pToRemove.add((Place)n);
				}
			}
		}

		Set<FlowRelationship> fToRemove = new HashSet<FlowRelationship>();
		for(FlowRelationship f : net.getFlowRelationships()) {
			if (tToRemove.contains(f.getSource()) || tToRemove.contains(f.getTarget()))
				fToRemove.add(f);
			if (pToRemove.contains(f.getSource()) || pToRemove.contains(f.getTarget()))
				fToRemove.add(f);
		}

		net.getPlaces().removeAll(pToRemove);
		net.getTransitions().removeAll(tToRemove);
		net.getFlowRelationships().removeAll(fToRemove);
		
		net.setTransitiveClosure(null);
		
		return net;
	}
	
	public String getResult() {
		String result = "\nNR CORRESPONDENCES:" + correspondences.size();
		result += "\nNR PROTOCOL COMPLEX CORRESPONDENCES:" + nrComplexProtocolCorrespondences;
		result += "\nNR PROTOCOL SIMPLE COMPATIBLE CORRESPONDENCES:" + nrSimpleProtocolCompatible;
		result += "\nNR PROTOCOL SIMPLE INCOMPATIBLE CORRESPONDENCES:" + nrSimpleProtocolIncompatible;
		result += "\nNR PROTOCOL COMPLEX COMPATIBLE CORRESPONDENCES:" + nrComplexProtocolCompatible;
		result += "\nNR PROTOCOL COMPLEX INCOMPATIBLE CORRESPONDENCES:" + nrComplexProtocolIncompatible;
		
		for (Map.Entry<String,Boolean> cr: compatibilityResults.entrySet()){
			result += "\n" + cr.getKey() + ", " + cr.getValue();
		}
		
		return result;
	}
	
	public void printResults(PrintStream outfile) {
		outfile.println("\tNR CORRESPONDENCES:" + correspondences.size());
//		outfile.println("\tNR COMPLEX CORRESPONDENCES:" + nrComplexCorrespondences);
//		outfile.println("\tNR SIMPLE COMPATIBLE CORRESPONDENCES:" + nrSimpleCompatible);
//		outfile.println("\tNR SIMPLE INCOMPATIBLE CORRESPONDENCES:" + nrSimpleIncompatible);
//		outfile.println("\tNR COMPLEX COMPATIBLE CORRESPONDENCES:" + nrComplexCompatible);
//		outfile.println("\tNR COMPLEX INCOMPATIBLE CORRESPONDENCES:" + nrComplexIncompatible);
		
		outfile.println("\tNR PROTOCOL COMPLEX CORRESPONDENCES:" + nrComplexProtocolCorrespondences);
		outfile.println("\tNR PROTOCOL SIMPLE COMPATIBLE CORRESPONDENCES:" + nrSimpleProtocolCompatible);
		outfile.println("\tNR PROTOCOL SIMPLE INCOMPATIBLE CORRESPONDENCES:" + nrSimpleProtocolIncompatible);
		outfile.println("\tNR PROTOCOL COMPLEX COMPATIBLE CORRESPONDENCES:" + nrComplexProtocolCompatible);
		outfile.println("\tNR PROTOCOL COMPLEX INCOMPATIBLE CORRESPONDENCES:" + nrComplexProtocolIncompatible);
		
		for (Map.Entry<String,Boolean> cr: compatibilityResults.entrySet()){
			outfile.println("\t" + cr.getKey() + ", " + cr.getValue());
		}
	}

	/**
	 * Clones the net and removes all nodes given for the original net from the clone.
	 */
	protected PTNet getSubnetOfClone(PTNet originalNet, Set<Node> nodesToRemove) {
		PTNet net = null;
		try {
			net = (PTNet) originalNet.clone();
			
			Set<Place> pToRemove = new HashSet<Place>();
			Set<Transition> tToRemove = new HashSet<Transition>();
			for (Node n : net.getNodes()) {
				for (Node toRemove : nodesToRemove) {
					if (n.getId().equals(toRemove.getId())) {
						if (n instanceof Place)
							pToRemove.add((Place)n);
						else
							tToRemove.add((Transition)n);
					}
				}
			}

			Set<FlowRelationship> fToRemove = new HashSet<FlowRelationship>();
			for(FlowRelationship f : net.getFlowRelationships()) {
				if (pToRemove.contains(f.getSource()) || pToRemove.contains(f.getTarget()) || tToRemove.contains(f.getSource()) || tToRemove.contains(f.getTarget()))
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
	
	protected Set<Transition> adaptSet(PTNet newNet, Set<Transition> transitions) {
		Set<Transition> result = new HashSet<Transition>();
		for (Transition t : transitions) {
			for (Transition t2 : newNet.getTransitions()) {
				if (t.getId().equals(t2.getId()))
					result.add(t2);
			}
		}
		
		return result;
	}
	
	protected boolean correspondencesAreCompatible(TwoTransitionSets c1, TwoTransitionSets c2, BehaviouralProfile profile1, BehaviouralProfile profile2) {
		boolean result = true;
		
	
		/*
		 * Do preprocessing
		 */
		PTNet preProcessedNet1 = getSubnetOfClone(profile1.getNet(), getPreProcessingNodes(profile1, c1.s1, c2.s1));
		PTNet preProcessedNet2 = getSubnetOfClone(profile2.getNet(), getPreProcessingNodes(profile2, c1.s2, c2.s2));

//		Set<Node> nodesToRemoveInNet1 = getPreProcessingNodes(profile1, c1.s1, c2.s1);
//		Set<Node> nodesToRemoveInNet2 = getPreProcessingNodes(profile2, c1.s2, c2.s2);
//		if ((nodesToRemoveInNet1.size() > 0) || (nodesToRemoveInNet2.size() > 0)) {
//			System.out.println("BREAK");
//			System.out.println(preProcessedNet2);
//		}
		
		if (!preProcessedNet1.isWorkflowNet())
			System.err.println("Preprocessed net1 is not WF!");
		if (!preProcessedNet2.isWorkflowNet())
			System.err.println("Preprocessed net2 is not WF!");
		
		/*
		 * Extract interleaving transitions
		 */
		Set<Transition> interleavingInNet1 = adaptSet(preProcessedNet1, getInterleavingTransitions(profile1, c1.s1, c2.s1));
		Set<Transition> interleavingInNet2 = adaptSet(preProcessedNet2, getInterleavingTransitions(profile2, c1.s2, c2.s2));
		
//		if ((interleavingInNet1.size() > 0) || (interleavingInNet2.size() > 0)) {
//			System.out.println("BREAK");
//		}

		/*
		 * Derive non-interleaving 
		 */
		Set<Transition> nonInterleavingC1InNet1 = new HashSet<Transition>(adaptSet(preProcessedNet1,c1.s1));
		nonInterleavingC1InNet1.removeAll(interleavingInNet1);
		Set<Transition> nonInterleavingC2InNet1 = new HashSet<Transition>(adaptSet(preProcessedNet1,c2.s1));
		nonInterleavingC2InNet1.removeAll(interleavingInNet1);
		
		Set<Transition> nonInterleavingC1InNet2 = new HashSet<Transition>(adaptSet(preProcessedNet2,c1.s2));
		nonInterleavingC1InNet2.removeAll(interleavingInNet2);
		Set<Transition> nonInterleavingC2InNet2 = new HashSet<Transition>(adaptSet(preProcessedNet2,c2.s2));
		nonInterleavingC2InNet2.removeAll(interleavingInNet2);

		/*
		 * Check both directions
		 */
		result &= correspondencesAreCompatibleInOneDirection(preProcessedNet1, preProcessedNet2, 
				nonInterleavingC1InNet1,
				nonInterleavingC2InNet1,
				interleavingInNet1,
				nonInterleavingC1InNet2,
				nonInterleavingC2InNet2,
				interleavingInNet2);

		result &= correspondencesAreCompatibleInOneDirection(preProcessedNet2, preProcessedNet1, 
				nonInterleavingC1InNet2,
				nonInterleavingC2InNet2,
				interleavingInNet2,
				nonInterleavingC1InNet1,
				nonInterleavingC2InNet1,
				interleavingInNet1);

		return result;
	}

	protected Set<Node> getNextNodesWithDifferentMode(Node current,
			Set<Transition> nonInterleavingC1,
			Set<Transition> nonInterleavingC2,
			Set<Transition> interleaving) {
		int mode = getModeForNode(current, nonInterleavingC1, nonInterleavingC2, interleaving);
		Set<Node> result = new HashSet<Node>();
		
		Set<Node> visited = new HashSet<Node>();
		visited.add(current);
		
		List<Node> sucNodes = new ArrayList<Node>();
		sucNodes.addAll(current.getSucceedingNodes());
		
		while(!(sucNodes.isEmpty())) {
			Node suc = sucNodes.remove(0);
			visited.add(suc);
			int sucMode = getModeForNode(suc, nonInterleavingC1, nonInterleavingC2, interleaving);
			if (sucMode != -1 && sucMode != mode) {
				result.add(suc);
			}
			else {
				for (Node n : suc.getSucceedingNodes()) {
					if (!(visited.contains(n))) {
						sucNodes.add(n);
					}
				}
			}
		}
		return result;
	}
	
	protected boolean correspondencesAreCompatibleInOneDirection(PTNet net1, PTNet net2, 
			Set<Transition> nonInterleavingC1InNet1,
			Set<Transition> nonInterleavingC2InNet1,
			Set<Transition> interleavingInNet1,
			Set<Transition> nonInterleavingC1InNet2,
			Set<Transition> nonInterleavingC2InNet2,
			Set<Transition> interleavingInNet2){
		
		/*
		 * Partitioning mode
		 * -1 not aligned
		 * 0 init
		 * 1 c1
		 * 2 c2
		 * 3 interleaving
		 * 4 final
		 */
	
		boolean result = true;
		
		Set<Transition> cTransitionsInNet1 = new HashSet<Transition>(nonInterleavingC1InNet1);
		cTransitionsInNet1.addAll(nonInterleavingC2InNet1);
		cTransitionsInNet1.addAll(interleavingInNet1);

		Map<Node,Set<Node>> goodMatches = new HashMap<Node, Set<Node>>();
		Set<Node> s = new HashSet<Node>();
		s.add(net2.getInitialPlace());
		goodMatches.put(net1.getInitialPlace(), s);
		
		List<Node> toCheck = new ArrayList<Node>();
		toCheck.add(net1.getInitialPlace());
		
		while (!(toCheck.isEmpty())) {
			Node current = toCheck.remove(0);
			Set<Node> nextNodes = getNextNodesWithDifferentMode(current, nonInterleavingC1InNet1, nonInterleavingC2InNet1, interleavingInNet1);
			for (Node nextNode : nextNodes) {
				if (goodMatches.containsKey(nextNode))
					continue;
				int modeNextNode = getModeForNode(nextNode, nonInterleavingC1InNet1, nonInterleavingC2InNet1, interleavingInNet1);
				boolean foundGoodNodeForNextNode = false;
				Set<Node> goodNodesForNextNode = new HashSet<Node>();
				for (Node goodNode : goodMatches.get(current)) {
					Set<Node> candidateNodes = getNextNodesWithDifferentMode(goodNode, nonInterleavingC1InNet2, nonInterleavingC2InNet2, interleavingInNet2);
					for (Node candidateNode : candidateNodes) {
						int modeCandidateNode = getModeForNode(candidateNode, nonInterleavingC1InNet2, nonInterleavingC2InNet2, interleavingInNet2);
						if (modeNextNode == modeCandidateNode) {
							foundGoodNodeForNextNode = true;
							goodNodesForNextNode.add(candidateNode);
						} else {
							System.out.println(modeNextNode + ";" + modeCandidateNode + "|" + nextNode + candidateNode);
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
		
//		System.out.println(nonInterleavingC1InNet1 + "  --- " + nonInterleavingC2InNet1);
//		System.out.println(result);

		return result;
	}

	protected int getModeForNode(Node n, Set<Transition> nonInterleavingC1, Set<Transition> nonInterleavingC2, Set<Transition> interleaving) {
		if (n.getPrecedingNodes().isEmpty())
			return 0;
		if (n.getSucceedingNodes().isEmpty())
			return 4;
		if (nonInterleavingC1.contains(n))
			return 1;
		if (nonInterleavingC2.contains(n))
			return 2;
		if (interleaving.contains(n))
			return 3;
		return -1;
	}
	
	protected Set<Transition> getInterleavingTransitions(BehaviouralProfile bp, Set<Transition> c1, Set<Transition> c2) {
		Set<Transition> result = new HashSet<Transition>();
		
		for (Transition t1 : c1) {
			for (Transition t2 : c2) {
				if (bp.getConcurrencyMatrix().areTrueConcurrent(t1, t2)) {
					result.add(t1);
					result.add(t2);
				}
			}
		}
		
		return result;
	}
	
	protected Set<Node> getPreProcessingNodes(BehaviouralProfile bp, Set<Transition> c1, Set<Transition> c2) {
		Set<Node> result = new HashSet<Node>();
		
		Set<Transition> cTransitions = new HashSet<Transition>(c1);
		cTransitions.addAll(c2);
		
		for (Node n : bp.getNet().getNodes()) {
			if (cTransitions.contains(n))
				continue;
			for (Transition t1 : cTransitions) {
				if (bp.getConcurrencyMatrix().areTrueConcurrent(n, t1)) {
					for (Transition t2 : cTransitions) {
						if (bp.getConcurrencyMatrix().areTrueConcurrent(t1, t2)) {
							if (bp.getConcurrencyMatrix().areTrueConcurrent(n, t2))
								result.add(n);
							else if (bp.areExclusive(n, t2))
								result.add(n);
						}
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return profile1.getNet().getId() + " - " + profile2.getNet().getId() + "\n" + correspondences;
	}
}
