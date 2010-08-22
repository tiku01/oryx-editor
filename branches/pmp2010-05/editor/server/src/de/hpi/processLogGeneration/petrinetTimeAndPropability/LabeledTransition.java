package de.hpi.processLogGeneration.petrinetTimeAndPropability;

import de.hpi.petrinet.LabeledTransitionImpl;

public class LabeledTransition extends LabeledTransitionImpl
			implements de.hpi.petrinet.LabeledTransition, TransitionWithPropability{
	private int propability = -1;
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
