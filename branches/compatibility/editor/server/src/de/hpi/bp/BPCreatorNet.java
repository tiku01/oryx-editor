package de.hpi.bp;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.petrinet.Node;

public class BPCreatorNet extends AbstractBPCreator {
	
	private static BPCreatorNet eInstance;
	
	public static BPCreatorNet getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorNet();
		return eInstance;
	}
	
	private BPCreatorNet() {
		
	}
	
	public BehaviouralProfile deriveBehaviouralProfile(PTNet pn) {
		
		if (!pn.isWorkflowNet()) throw new IllegalArgumentException();
		
		if (!pn.isFreeChoiceNet()) throw new IllegalArgumentException();

		BehaviouralProfile profile = new BehaviouralProfile(pn,pn.getNodes());
		
		CharacteristicRelationType[][] matrix = profile.getMatrix();
		
		TrueConcurrencyRelation trueConcurrency = new TrueConcurrencyRelation(pn);
		
		profile.setConcurrencyMatrix(trueConcurrency);
		
		for(Node n1 : pn.getNodes()) {
			int index1 = pn.getNodes().indexOf(n1);
			for(Node n2 : pn.getNodes()) {
				int index2 = pn.getNodes().indexOf(n2);
				/*
				 * The matrix is symmetric. Therefore, we need to traverse only 
				 * half of the entries.
				 */
				if (index2 > index1)
					continue;
				/*
				 * What about the relation of a node to itself?
				 */
				if (index1 == index2) {
					if (pn.getTransitiveClosure().isPath(index1, index1)) {
						matrix[index1][index1] = CharacteristicRelationType.Concurrency;
					} else {
						matrix[index1][index1] = CharacteristicRelationType.Exclusive;
					}
				}
				else if (pn.getTransitiveClosure().isPath(index1, index2) && pn.getTransitiveClosure().isPath(index2, index1)) {
					super.setMatrixEntry(matrix,index1,index2,CharacteristicRelationType.Concurrency);
				}
				else if (trueConcurrency.areTrueConcurrent(index1,index2)) {
					super.setMatrixEntry(matrix,index1,index2,CharacteristicRelationType.Concurrency);
				}
				else if (!trueConcurrency.areTrueConcurrent(index1,index2) && !pn.getTransitiveClosure().isPath(index1, index2) && !pn.getTransitiveClosure().isPath(index2, index1)) {
					super.setMatrixEntry(matrix,index1,index2,CharacteristicRelationType.Exclusive);
				}
				else if (pn.getTransitiveClosure().isPath(index1, index2) && !pn.getTransitiveClosure().isPath(index2, index1)) {
					super.setMatrixEntryOrder(matrix,index1,index2);
				}
				else if (pn.getTransitiveClosure().isPath(index2, index1) && !pn.getTransitiveClosure().isPath(index1, index2)) {
					super.setMatrixEntryOrder(matrix,index2,index1);
				}
			}
		}
		
		return profile;
	}
}
