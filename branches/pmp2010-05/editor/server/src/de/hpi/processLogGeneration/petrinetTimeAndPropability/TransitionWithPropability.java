package de.hpi.processLogGeneration.petrinetTimeAndPropability;

/**
 * An interface for transitions allowing getting and setting the probability of
 * this transition.
 * 
 * @author Thomas Milde
 * */
public interface TransitionWithPropability {
	/**
	 * reads the probability of the transition to execute, if it stands in 
	 * competition with other transitions.
	 * */
	public int getPropability();
	/**
	 * sets the probability of the transition to execute, if it stands in 
	 * competition with other transitions.
	 * 
	 * @param propability the new probability-value
	 * */
	public void setPropability(int propability);
}
