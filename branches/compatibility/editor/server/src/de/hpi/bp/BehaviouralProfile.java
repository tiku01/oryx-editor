/**
 * Copyright (c) 2009 Matthias Weidlich
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.Node;

public class BehaviouralProfile {
	
	public enum CharacteristicRelationType {
		StrictOrder,ReversedStrictOrder,Concurrency,Exclusive,None
	}

	protected PTNet pn;
	
	protected List<Node> nodes;
	
	protected CharacteristicRelationType[][] matrix;

	protected TrueConcurrencyRelation concurrencyMatrix;
	
	public CharacteristicRelationType[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(CharacteristicRelationType[][] matrix) {
		this.matrix = matrix;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}
	public BehaviouralProfile(PTNet pn, List<Node> nodes) {
		this.pn = pn;
		this.nodes = nodes;
		this.matrix = new CharacteristicRelationType[this.nodes.size()][this.nodes.size()];
	}
	
	public BehaviouralProfile(int size) {
		this.matrix = new CharacteristicRelationType[size][size];
	}
	
	public PTNet getNet() {
		return this.pn;
	}
		
	public boolean areConcurrent(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		return matrix[index1][index2].equals(CharacteristicRelationType.Concurrency);
	}

	public boolean areExclusive(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		return matrix[index1][index2].equals(CharacteristicRelationType.Exclusive);
	}

	public boolean areInStrictOrder(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		return matrix[index1][index2].equals(CharacteristicRelationType.StrictOrder);
	}

	public CharacteristicRelationType getRelationForNodes(Node n1, Node n2) {
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		return matrix[index1][index2];
	}
	
	public CharacteristicRelationType getRelationForIndex(int index1, int index2) {
		return matrix[index1][index2];
	}

	
	public Collection<Node> getNodesInRelation(Node n, CharacteristicRelationType relationType) {
		Collection<Node> nodes = new ArrayList<Node>();
		int index = this.nodes.indexOf(n);
		
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[index][i].equals(relationType)) {
				nodes.add(this.nodes.get(i));
			}
		}
		return nodes;
	}
	
	public void printAllNodes(CharacteristicRelationType relationType) {
		for(Node n1 : this.nodes) {
			int index1 = this.nodes.indexOf(n1);
			for(Node n2 : this.nodes) {
				int index2 = this.nodes.indexOf(n2);
				if (index2 > index1)
					continue;
				if (matrix[index1][index2].equals(relationType))
					System.out.println(relationType + " -- " + n1 + " : " + n2);
			}
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("Behavioural Profile Matrix\n");
		sb.append("------------------------------------------\n");
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				sb.append(matrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}
	
	/**
	 * Checks equality for two behavioural profiles
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equals (BehaviouralProfile profile) {
		if (!this.pn.equals(profile.getNet()))
			return false;
		
		boolean equal = true;
		
		for(Node n1 : this.nodes) {
			for(Node n2 : this.nodes) {
				equal &= this.getRelationForNodes(n1, n2).equals(profile.getRelationForNodes(n1, n2));
			}
		}
		return equal;
	}

	public TrueConcurrencyRelation getConcurrencyMatrix() {
		return concurrencyMatrix;
	}

	public void setConcurrencyMatrix(TrueConcurrencyRelation concurrencyMatrix) {
		this.concurrencyMatrix = concurrencyMatrix;
	}

}
