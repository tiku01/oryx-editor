package nl.tue.tm.is.graph;

import java.util.Set;
import de.hpi.petrinet.Transition;

public class TwoTransitionSets {
	public Set<Transition> s1;
	public Set<Transition> s2;

	public TwoTransitionSets(Set<Transition> s1, Set<Transition> s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public String toString(){
		return "("+s1+","+s2+")";
	}

	public boolean equals(Object pair2){
		return pair2 instanceof TwoTransitionSets?(s1.equals(((TwoTransitionSets)pair2).s1) && s2.equals(((TwoTransitionSets)pair2).s2)):false;
	}

	public int hashCode(){
		return s1.hashCode() + s2.hashCode();
	}
	
	public boolean isComplex() {
		return (s1.size() > 1) || (s2.size() > 1);
	}

}
