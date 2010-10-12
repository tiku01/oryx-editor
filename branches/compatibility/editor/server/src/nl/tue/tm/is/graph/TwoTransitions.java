package nl.tue.tm.is.graph;

import de.hpi.petrinet.Transition;

public class TwoTransitions{
	public Transition t1;
	public Transition t2;

	public TwoTransitions(Transition t1, Transition t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	public String toString(){
		return "("+t1+","+t2+")";
	}

	public boolean equals(Object pair2){
		return pair2 instanceof TwoTransitions?(t1.equals(((TwoTransitions)pair2).t1) && t2.equals(((TwoTransitions)pair2).t2)):false;
	}

	public int hashCode(){
		return t1.hashCode() + t2.hashCode();
	}
}