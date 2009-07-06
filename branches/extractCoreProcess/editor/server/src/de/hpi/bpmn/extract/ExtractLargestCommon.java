package de.hpi.bpmn.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.batik.dom.xbl.NodeXBL;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.extract.AbstractExtraction.CommonEntry;
import de.hpi.bpmn.extract.exceptions.IsNotWorkflowNetException;
import de.hpi.bpmn.extract.exceptions.NoEndNodeException;
import de.hpi.bpmn.extract.exceptions.NoStartNodeException;
import de.hpi.bpmn2pn.converter.HighConverter;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.highpetrinet.HighPetriNet;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Transition;

public class ExtractLargestCommon extends AbstractExtraction {

	public ExtractLargestCommon(BPMNDiagram diagramA, BPMNDiagram diagramB)
			throws NoStartNodeException, NoEndNodeException {
		super(diagramA, diagramB);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BPMNDiagram extract() throws NoStartNodeException,NoEndNodeException, IsNotWorkflowNetException {
		
		SortedSet<CommonEntry> common = getCommonNodes(diagramA, diagramB);
		List<Node> removeList = revertList(diagramA, common);
		removeNodes(diagramA, removeList);
		return diagramA;
		
	}
	

	
	@Override
	protected void initDiagram() throws NoStartNodeException, NoEndNodeException{
		super.initDiagram();
	}

	@Override
	protected SortedSet<CommonEntry> getCommonNodes(BPMNDiagram diagramA, BPMNDiagram diagramB) throws IsNotWorkflowNetException, NoStartNodeException {

		// Get petri net for diagram a/b
		HighPetriNet pn1 = getPetriNet(diagramA);		
		HighPetriNet pn2 = getPetriNet(diagramB);
		
		// Check if it is a workflow net
		if (!pn1.isWorkflowNet() || !pn2.isWorkflowNet()) {
			throw new IsNotWorkflowNetException();
		}
		
		// Get behavioural profiles
		BehaviouralProfile bpA = getBehaviouralProfile(pn1);
		BehaviouralProfile bpB = getBehaviouralProfile(pn2);

		// Init vars
		SortedSet<CommonEntry> nodes = super.getCommonNodes(diagramA, diagramB);
		SortedSet<CommonEntry> resultList = new TreeSet<CommonEntry>(new CommonEntryComparator());		
		List<de.hpi.petrinet.Node> removedNodes = new ArrayList<de.hpi.petrinet.Node>();
		
		
		// Go thru every common node
		for (CommonEntry entry:nodes){

			// Get the pn node for the current activity
			de.hpi.petrinet.Node n1 = getNodeByResourceId(pn1, entry.getA().getResourceId());
			de.hpi.petrinet.Node n2 = getNodeByResourceId(pn2, entry.getB().getResourceId());
			
			// Check whether it is labels transition and does have a label
			if (	n1 == null || 
					n2 == null || 
					!(n1 instanceof LabeledTransition) || 
					!(n2 instanceof LabeledTransition) ||
					((LabeledTransition) n1).getLabel() == null || "".equals(((LabeledTransition) n1).getLabel()) || 
					((LabeledTransition) n2).getLabel() == null || "".equals(((LabeledTransition) n2).getLabel()) ){
				continue;
			}
			
			// Check whether it is contained in the removed list
			if ( 	removedNodes.contains(n1) || 
					removedNodes.contains(n2) ) {
				continue;
			}

			// Get all characteristical relations for 
			HashMap<de.hpi.petrinet.Node[], CharacteristicRelationType> relations = getCharacteristicalRelations(bpA, n1, bpB, n2);
	
			
			// Iterate over every relation
			for (de.hpi.petrinet.Node[] key:relations.keySet()){
				CharacteristicRelationType c1 = relations.get(key);
				de.hpi.petrinet.Node nodeA = key[0];
				de.hpi.petrinet.Node nodeB = key[1];
				
				// Check if A is already removed
				if (removedNodes.contains(nodeA)) {
					break;
				}
				
				// Combine every relation with it self
				for (de.hpi.petrinet.Node[] key2:relations.keySet()){
					de.hpi.petrinet.Node nodeX = key2[1];
					CharacteristicRelationType c2 = relations.get(key2);
				
					
					de.hpi.petrinet.Node[][] eval;
					
					// Evaluate the relations depending on if the B == X
					if (isEqual(nodeX, nodeB)) {
						eval = evalCharacteristicalRelation(nodeA, nodeB, c1, c2);
					} else {
						eval = evalCharacteristicalRelation(nodeA, c1, c2);
					}
					
					
					// Add all removed nodes to the list of removed nodes 
					if (eval[1].length > 0) {
						for (de.hpi.petrinet.Node node:eval[1]) {
							removedNodes.add(node);
						}
					}
					
					// Check if the node contains in the result list
					boolean isIncluded = false;
					for (de.hpi.petrinet.Node node:eval[0]) {
						if (isEqual(node, n1)){
							isIncluded = true;
							break;
						}
					}
					
					// If not, add to removed
					if (!isIncluded) {
						removedNodes.add(n1);
						break;
					}
				}				
			}			
		}

		// Add all common entries which are not contained in the 
		// removed list
		for (CommonEntry entry:nodes) {
			boolean isIncluded = false;
			for (de.hpi.petrinet.Node node:removedNodes) {
				if (isEqual(entry.getA(), node) || 
					isEqual(entry.getB(), node) ){
					isIncluded = true;
					break;
				}
			}
			
			if (!isIncluded) {
				resultList.add(entry);
			}
		}
		
		return resultList;
	}
	
	
	private void removeNodes(BPMNDiagram diagram, List<Node> nodes) {
		for (Node node:nodes){
			// Add all incoming edges to the outgoing list
			List<Node> targets = getFollowedNodes(node);
			for (Edge edge:new ArrayList<Edge>(node.getIncomingEdges())) {
				if (targets.size() > 0){
					edge.setTarget(targets.get(0));
				} else {
					edge.setTarget(null);
					edge.setSource(null);
					diagram.getEdges().remove(edge);
				}
			}
			
			// Remove all outgoing edges
			for (Edge edge:new ArrayList<Edge>(node.getOutgoingEdges())) {
				edge.setTarget(null);
				edge.setSource(null);
				diagram.getEdges().remove(edge);
			}
			node.setParent(null);
		}
	}
	/**
	 * Returns a list on all activities in the diagram which are not
	 * contained in the nodes list.
	 * @param diagram
	 * @param nodes
	 * @return
	 */
	private List<Node> revertList(BPMNDiagram diagram, SortedSet<CommonEntry> nodes) {
		List<Node> revert = new ArrayList<Node>();
		for (Node node:diagram.getAllActivities()) {
			boolean isIncluded = false;
			for (CommonEntry entry :nodes) {
				if (node == entry.getA() || node == entry.getB()) {
					isIncluded = true;
				}
			}
			if (!isIncluded) {
				revert.add(node);
			}
		}
		return revert;
	}


	private boolean isEqual(Node a, de.hpi.petrinet.Node b){
		return 	b instanceof LabeledTransition &&
				a.getLabel() != null &&
				((LabeledTransition) b).getLabel() != null &&
				a.getLabel().trim().equals(((LabeledTransition) b).getLabel().trim());
	}
	
	
	private boolean isEqual(de.hpi.petrinet.Node a, de.hpi.petrinet.Node b){
		return 	a instanceof LabeledTransition && 
				b instanceof LabeledTransition &&
				((LabeledTransition) a).getLabel() != null &&
				((LabeledTransition) b).getLabel() != null &&
				((LabeledTransition) a).getLabel().trim().equals(((LabeledTransition) b).getLabel().trim());
	}
	
	private HashMap<de.hpi.petrinet.Node[], CharacteristicRelationType> getCharacteristicalRelations(BehaviouralProfile bpA, de.hpi.petrinet.Node n1, BehaviouralProfile bpB, de.hpi.petrinet.Node n2){
		
		Collection<de.hpi.petrinet.Node> relationsA = bpA.getNodesInRelation(n1);
		Collection<de.hpi.petrinet.Node> relationsB = bpB.getNodesInRelation(n2);
		
		HashMap<de.hpi.petrinet.Node[], CharacteristicRelationType> relations = new HashMap<de.hpi.petrinet.Node[], CharacteristicRelationType>();
		for (de.hpi.petrinet.Node node:relationsA) {
			if (	!(node instanceof LabeledTransition) || 
					((LabeledTransition) node).getLabel() == null || 
					"".equals(((LabeledTransition) node).getLabel())) {
				continue;
			}
			if (isEqual(n1, node)) {
				continue;
			}
			CharacteristicRelationType relation = bpA.getRelationForNodes(n1, node);
			relations.put(new de.hpi.petrinet.Node[]{n1,node}, relation);
		}
		for (de.hpi.petrinet.Node node:relationsB) {
			if (	!(node instanceof LabeledTransition) || 
					((LabeledTransition) node).getLabel() == null || 
					"".equals(((LabeledTransition) node).getLabel())) {
				continue;
			}
			if (isEqual(n2, node)) {
				continue;
			}
			CharacteristicRelationType relation = bpB.getRelationForNodes(n2, node);
			relations.put(new de.hpi.petrinet.Node[]{n2,node}, relation);
		}	
		
		return relations;
	}
	/**
	 * Returns a 2-dimension array where the first values are the nodes
	 * which should be added and the second are the nodes which shouldn't added
	 * @param a
	 * @param b
	 * @param rel1
	 * @param rel2
	 * @return
	 */
	private de.hpi.petrinet.Node[][] evalCharacteristicalRelation(de.hpi.petrinet.Node a, de.hpi.petrinet.Node b, CharacteristicRelationType rel1, CharacteristicRelationType rel2){
		// Take A and B
		if (		(rel1.equals(CharacteristicRelationType.StrictOrder) && 
					rel2.equals(CharacteristicRelationType.StrictOrder)) ||
					
					(rel1.equals(CharacteristicRelationType.Concurrency) && 
					rel2.equals(CharacteristicRelationType.Concurrency)) ||
					
					(rel1.equals(CharacteristicRelationType.Exclusive) && 
					rel2.equals(CharacteristicRelationType.Exclusive)) ||
					
					(rel1.equals(CharacteristicRelationType.ReversedStrictOrder) && 
					rel2.equals(CharacteristicRelationType.ReversedStrictOrder))) {
				
			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{a, b}, new de.hpi.petrinet.Node[]{}};
		}
		
		// Take A
		else if (	(rel1.equals(CharacteristicRelationType.StrictOrder) && 
					rel2.equals(CharacteristicRelationType.Concurrency)) ||
					
					(rel1.equals(CharacteristicRelationType.Concurrency) && 
					rel2.equals(CharacteristicRelationType.StrictOrder))) {

			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{a}, new de.hpi.petrinet.Node[]{}};
		}
		
		// Take Nothing
		else if (	(rel1.equals(CharacteristicRelationType.StrictOrder) && 
					rel2.equals(CharacteristicRelationType.Exclusive)) ||
					
					(rel1.equals(CharacteristicRelationType.Exclusive) && 
					rel2.equals(CharacteristicRelationType.StrictOrder)) ||
					
					(rel1.equals(CharacteristicRelationType.Concurrency) && 
					rel2.equals(CharacteristicRelationType.Exclusive)) ||
							
					(rel1.equals(CharacteristicRelationType.Exclusive) && 
					rel2.equals(CharacteristicRelationType.Concurrency)) ) {

			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{}, new de.hpi.petrinet.Node[]{}};
		}
		
		// Take A but remove B
		else if (	(rel1.equals(CharacteristicRelationType.StrictOrder) && 
					rel2.equals(CharacteristicRelationType.ReversedStrictOrder)) ||
					
					(rel1.equals(CharacteristicRelationType.ReversedStrictOrder) && 
					rel2.equals(CharacteristicRelationType.StrictOrder))) {

			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{a}, new de.hpi.petrinet.Node[]{b}};
		}
		
		// Take B
		else {
			
			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{b}, new de.hpi.petrinet.Node[]{}};
		}
		
	}

	
	/**
	 * Returns a 2-dimension array where the first values are the nodes
	 * which should be added and the second are the nodes which shouldn't added
	 * @param a
	 * @param rel1
	 * @param rel2
	 * @return
	 */
	private de.hpi.petrinet.Node[][] evalCharacteristicalRelation(de.hpi.petrinet.Node a, CharacteristicRelationType rel1, CharacteristicRelationType rel2){
		// Take nothing
		if (	(rel1.equals(CharacteristicRelationType.Exclusive) || 
				rel2.equals(CharacteristicRelationType.Exclusive)) ) {
				
			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{}, new de.hpi.petrinet.Node[]{}};
		}
		
		// Take A
		else {
			
			return new de.hpi.petrinet.Node[][]{new de.hpi.petrinet.Node[]{a}, new de.hpi.petrinet.Node[]{}};
		}
		
	}
	
	private HighPetriNet getPetriNet(BPMNDiagram diagram) {
		return new HighConverter(diagram).convert();
	}
	
	private BehaviouralProfile getBehaviouralProfile(BPMNDiagram diagram) {
		return new BehaviouralProfile(getPetriNet(diagram));
	}

	private BehaviouralProfile getBehaviouralProfile(PTNet diagram) {
		return new BehaviouralProfile(diagram);
	}
	
	/**
	 * Returns a petri net node by there id
	 * @param net
	 * @param id
	 * @return
	 */
	private de.hpi.petrinet.Node getNodeByResourceId(HighPetriNet net, String id) {
		if (id == null || "".equals(id)) {
			return null;
		}
		
		for (de.hpi.petrinet.Node node : net.getTransitions()){
			if (node.getId() != null && id.equals(node.getId())) {
				return node;
			}
		}
		return null;
	}
	
	protected class CommonEntryBP extends CommonEntry {
		
		private CharacteristicRelationType crt;
		
		public CommonEntryBP(CommonEntry c, CharacteristicRelationType crt){
			super(c.getA(),c.getCountA(),c.getB(),c.getCountB());
			this.crt = crt;
		}
		public CharacteristicRelationType getRelation(){
			return crt;
		}
	}

}
