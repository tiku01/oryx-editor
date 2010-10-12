package de.hpi.compatibility;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class NetNormalizer {
	
	private static NetNormalizer eInstance = null;
	
	public static NetNormalizer getInstance(){
		if (eInstance == null)
			eInstance = new NetNormalizer();
		
		return eInstance;
	}

	public void normalizeNet(PTNet net) {
		if (net.getFinalPlaces().size() == 0)
			return;
		
	
		Place p = net.getFactory().createPlace();
		p.setId("newFinalPlace");
		net.getPlaces().add(p);
				
		int i = 0;
		for (Place o : net.getFinalPlaces()) {
			
			Transition t = net.getFactory().createSilentTransition();
			t.setId("finalHelper" + i);
			net.getTransitions().add(t);
			createFlowRelationship(net, o, t);
			createFlowRelationship(net,t,p);
	
			i++;
		}
		
		net.clearInitialAndFinalPlaces();
		
	}
	
	protected void createFlowRelationship(PetriNet net, Node src, Node tar) {
		FlowRelationship f = net.getFactory().createFlowRelationship();
		f.setSource(src);
		f.setTarget(tar);
		net.getFlowRelationships().add(f);
	}

}
