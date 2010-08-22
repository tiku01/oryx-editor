package de.hpi.processLogGeneration.petrinetTimeAndPropability;

import de.hpi.petrinet.LabeledTransitionImpl;

/**
 * Represents a transition of a Petrinet, that is not a silent one and has, 
 * in addition to the usual LabledTransition an execution-time and probability.
 * 
 * @author Thomas Milde
 * */
public class LabeledTransition extends LabeledTransitionImpl
			implements de.hpi.petrinet.LabeledTransition, TransitionWithPropability{
	/**
	 * the probability of this Transition to execution in competition with other
	 * transitions.
	 * */
	private int propability = -1;
	
	/**
	 * the time (in seconds), that this transition takes to execute.
	 * */
	private int time = 1;
	public void setPropability(int propability) {
		this.propability = propability;
	}
	public int getPropability() {
		return propability;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getTime() {
		return time;
	}
}
