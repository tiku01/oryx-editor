package de.hpi.processLogGeneration.petrinetTimeAndPropability;

import de.hpi.petrinet.SilentTransitionImpl;

public class SilentTransition extends SilentTransitionImpl
			implements de.hpi.petrinet.SilentTransition, TransitionWithPropability {
	private int propability = -1;
	public void setPropability(int propability) {
		this.propability = propability;
	}
	public int getPropability() {
		return propability;
	}
}
