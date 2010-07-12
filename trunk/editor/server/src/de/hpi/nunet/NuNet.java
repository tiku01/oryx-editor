/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.ArrayList;
import java.util.List;

public class NuNet {
	
	public static String NEW = "new";

	private List<Place> places;
	private List<Transition> transitions;
	private List<FlowRelationship> flowRelationships;
	private Marking initialMarking;
	
	public List<Place> getPlaces() {
		if (places == null)
			places = new ArrayList<Place>();
		return places;
	}

	public List<Transition> getTransitions() {
		if (transitions == null)
			transitions = new ArrayList<Transition>();
		return transitions;
	}

	public List<FlowRelationship> getFlowRelationships() {
		if (flowRelationships == null)
			flowRelationships = new ArrayList<FlowRelationship>();
		return flowRelationships;
	}

	public Marking getInitialMarking() {
		if (initialMarking == null)
			initialMarking = NuNetFactory.eINSTANCE.createMarking(this);
		return initialMarking;
	}
	
} // NuNet