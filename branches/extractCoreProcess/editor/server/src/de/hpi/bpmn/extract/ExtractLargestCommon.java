package de.hpi.bpmn.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.bpmn.BPMNDiagram;
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
			throws NoStartNodeException {
		super(diagramA, diagramB);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BPMNDiagram extract() throws NoStartNodeException,NoEndNodeException {
		
		
		
		return null;
	}
	

	
	@Override
	protected void initDiagram() throws NoStartNodeException{
		super.initDiagram();
	}

	@Override
	protected SortedSet<CommonEntry> getCommonNodes(BPMNDiagram diagramA, BPMNDiagram diagramB) {

		HighPetriNet pn1 = getPetriNet(diagramA);		
		HighPetriNet pn2 = getPetriNet(diagramB);
		
		if (!pn1.isWorkflowNet() || !pn2.isWorkflowNet()) {
			throw new IsNotWorkflowNetException();
		}
		

		SortedSet<CommonEntry> nodes = super.getCommonNodes(diagramA, diagramB);
		
		BehaviouralProfile bpA = getBehaviouralProfile(pn1);
		BehaviouralProfile bpB = getBehaviouralProfile(pn2);
		
		List<CommonEntryBP> commonEntries = new ArrayList<CommonEntryBP>();
		
		
		SortedSet<CommonEntry> removedSet = new TreeSet<CommonEntry>();
		
		for (CommonEntry entry:nodes){

			de.hpi.petrinet.Node n1 = getNodeByResourceId(pn1, entry.getA().getResourceId());
			de.hpi.petrinet.Node n2 = getNodeByResourceId(pn2, entry.getB().getResourceId());
			
			if (n1 == null || n2 == null){
				continue;
			}

			Collection<de.hpi.petrinet.Node> relationsA = bpA.getNodesInRelation(n1);
			Collection<de.hpi.petrinet.Node> relationsB = bpB.getNodesInRelation(n2);

			HashMap<de.hpi.petrinet.Node[], CharacteristicRelationType> relations = new HashMap<de.hpi.petrinet.Node[], CharacteristicRelationType>();
			for (de.hpi.petrinet.Node node:relationsA) {
				CharacteristicRelationType relation = bpA.getRelationForNodes(n1, node);
				relations.put(new de.hpi.petrinet.Node[]{n1,node}, relation);
			}
			for (de.hpi.petrinet.Node node:relationsA) {
				CharacteristicRelationType relation = bpB.getRelationForNodes(n2, node);
				relations.put(new de.hpi.petrinet.Node[]{n2,node}, relation);
			}		
			
			for (de.hpi.petrinet.Node[] key:relations.keySet()){
				CharacteristicRelationType c1 = relations.get(key);
				for (de.hpi.petrinet.Node[] key2:relations.keySet()){
					CharacteristicRelationType c2 = relations.get(key2);
					if (		c1.equals(CharacteristicRelationType.StrictOrder) && 
								c2.equals(CharacteristicRelationType.StrictOrder)) {
						
					}
					else if (	c1.equals(CharacteristicRelationType.Concurrency) && 
								c2.equals(CharacteristicRelationType.Concurrency)) {
						
					}
					else if (	c1.equals(CharacteristicRelationType.Concurrency) && 
								c2.equals(CharacteristicRelationType.Concurrency)) {
						
					}
					else if (	c1.equals(CharacteristicRelationType.Concurrency) && 
							c2.equals(CharacteristicRelationType.Concurrency)) {
					
					}
					else if (	c1.equals(CharacteristicRelationType.Concurrency) && 
							c2.equals(CharacteristicRelationType.Concurrency)) {
					
					}
					else if (	c1.equals(CharacteristicRelationType.Concurrency) && 
							c2.equals(CharacteristicRelationType.Concurrency)) {
					
					}
				}				
			}
			
		}
		
		/*for (int i=0; i<nodes.length; i++){

			de.hpi.petrinet.Node n11 = getNodeByResourceId(pn1, nodes[i].getA().getResourceId());
			de.hpi.petrinet.Node n21 = getNodeByResourceId(pn2, nodes[i].getB().getResourceId());
			
			if (n11 == null || n21 == null){ continue; }
			
			for (int j=i+1; j<nodes.length; j++){

				de.hpi.petrinet.Node n12 = getNodeByResourceId(pn1, nodes[j].getA().getResourceId());
				de.hpi.petrinet.Node n22 = getNodeByResourceId(pn2, nodes[j].getB().getResourceId());

				if (n12 == null || n22 == null){ continue; }

				CharacteristicRelationType r1 = bpA.getRelationForNodes(n11, n12);
				CharacteristicRelationType r2 = bpA.getRelationForNodes(n21, n22);
				
				if (r1 != null && r2 != null && r1.equals(r2)) {
					commonEntries.add(new )
				}
			}			
		}*/
		
	}
	
	
	private HighPetriNet getPetriNet(BPMNDiagram diagram) {
		return new HighConverter(this.diagram).convert();
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
		
		for (de.hpi.petrinet.Node node : net.getNodes()){
			if (node.getResourceId() != null && id.equals(node.getResourceId())) {
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
