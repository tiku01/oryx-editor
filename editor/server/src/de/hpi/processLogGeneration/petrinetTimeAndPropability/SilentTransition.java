package de.hpi.processLogGeneration.petrinetTimeAndPropability;

import de.hpi.petrinet.SilentTransitionImpl;

/**
 * Represents a silent transition in a Petrinet. In addition to the usual SilentTransition,
 * this one has a probability of executing in competition with other transitions.
 * 
 * @author Thomas Milde
 * */
public class SilentTransition extends SilentTransitionImpl
			implements de.hpi.petrinet.SilentTransition, TransitionWithPropability {
	/**
	 * the probability for this transition to execute, when it is in competition
	 * with other transitions.
	 * */
	private int propability = -1;
	public void setPropability(int propability) {
		this.propability = propability;
	}
	public int getPropability() {
		return propability;
	}
}
