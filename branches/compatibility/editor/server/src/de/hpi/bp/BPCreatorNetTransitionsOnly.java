package de.hpi.bp;

import java.util.List;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.BehaviouralProfile.CharacteristicRelationType;
import de.hpi.petrinet.Node;

public class BPCreatorNetTransitionsOnly extends AbstractBPCreator {

	private static BPCreatorNetTransitionsOnly eInstance;
	
	public static BPCreatorNetTransitionsOnly getInstance() {
		if (eInstance == null)
			eInstance  = new BPCreatorNetTransitionsOnly();
		return eInstance;
	}
	
	private BPCreatorNetTransitionsOnly() {
		
	}
	
	public BehaviouralProfile deriveBehaviouralProfile(PTNet pn) {
		
		if (!pn.isWorkflowNet()) throw new IllegalArgumentException();
		
		if (!pn.isFreeChoiceNet()) throw new IllegalArgumentException();

		List<Node> lTransitions = pn.getLabeledTransitions();

		BehaviouralProfile profile = new BehaviouralProfile(pn,lTransitions);
		CharacteristicRelationType[][] matrix = profile.getMatrix();
	
		TrueConcurrencyRelation trueConcurrency = new TrueConcurrencyRelation(pn);
		

		for(Node t1 : lTransitions) {
			int index1 = lTransitions.indexOf(t1);
			for(Node t2 : lTransitions) {
				int index2 = lTransitions.indexOf(t2);
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
					if (pn.getTransitiveClosure().isPath(t1, t1)) {
						matrix[index1][index1] = CharacteristicRelationType.Concurrency;
					} else {
						matrix[index1][index1] = CharacteristicRelationType.Exclusive;
					}
				}
				else if (pn.getTransitiveClosure().isPath(t1, t2) && pn.getTransitiveClosure().isPath(t2, t1)) {
					super.setMatrixEntry(matrix,index1,index2,CharacteristicRelationType.Concurrency);
				}
				else if (trueConcurrency.areTrueConcurrent(t1,t2)) {
					super.setMatrixEntry(matrix,index1,index2,CharacteristicRelationType.Concurrency);
				}
				else if (!trueConcurrency.areTrueConcurrent(t1,t2) && !pn.getTransitiveClosure().isPath(t1, t2) && !pn.getTransitiveClosure().isPath(t2, t1)) {
					super.setMatrixEntry(matrix,index1,index2,CharacteristicRelationType.Exclusive);
				}
				else if (pn.getTransitiveClosure().isPath(t1, t2) && !pn.getTransitiveClosure().isPath(t2, t1)) {
					super.setMatrixEntryOrder(matrix,index1,index2);
				}
				else if (pn.getTransitiveClosure().isPath(t2, t1) && !pn.getTransitiveClosure().isPath(t1, t2)) {
					super.setMatrixEntryOrder(matrix,index2,index1);
				}
			}
		}
		
		return profile;
	}

}
