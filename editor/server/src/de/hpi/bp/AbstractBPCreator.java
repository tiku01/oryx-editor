package de.hpi.bp;

import de.hpi.bp.BehaviouralProfile.CharacteristicRelationType;

public abstract class AbstractBPCreator {

	/**
	 * As the matrix of the behavioral profile is symmetric for
	 * the exclusive and concurrency relation, we use this procedure 
	 * to set these dependency between two nodes.
	 * 
	 * @param i
	 * @param j
	 * @param type
	 */
	protected void setMatrixEntry(CharacteristicRelationType[][] matrix, int i, int j, CharacteristicRelationType type) {
		assert(type.equals(CharacteristicRelationType.Concurrency)||type.equals(CharacteristicRelationType.Exclusive));
		matrix[i][j] = type;
		matrix[j][i] = type;
	}
	
	protected void setMatrixEntryOrder(CharacteristicRelationType[][] matrix, int from, int to) {
		matrix[from][to] = CharacteristicRelationType.StrictOrder;
		matrix[to][from] = CharacteristicRelationType.ReversedStrictOrder;
	}
	

}
