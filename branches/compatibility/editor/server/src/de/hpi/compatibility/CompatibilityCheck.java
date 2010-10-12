package de.hpi.compatibility;

import java.util.Set;

import nl.tue.tm.is.graph.TwoTransitionSets;
import de.hpi.PTnet.PTNet;
import de.hpi.bp.BPCreatorNet;
import de.hpi.bp.BehaviouralProfile;

public class CompatibilityCheck {
	private PTNet pn1;
	private PTNet pn2;
	private Set<TwoTransitionSets> correspondences;
	
	public CompatibilityCheck (PTNet pn1, PTNet pn2, Set<TwoTransitionSets> correspondences) {
		this.pn1 = pn1;
		this.pn2 = pn2;
		this.correspondences = correspondences;
	}
	
	public String run() {
		
		NetNormalizer.getInstance().normalizeNet(pn1);
		BehaviouralProfile bp1 = BPCreatorNet.getInstance().deriveBehaviouralProfile(pn1);
		NetNormalizer.getInstance().normalizeNet(pn2);
		BehaviouralProfile bp2 = BPCreatorNet.getInstance().deriveBehaviouralProfile(pn2);
		
		CorrespondenceAnalysis analysis = new CorrespondenceAnalysis(bp1, bp2, correspondences);
		analysis.checkCompatibility();
		
		return analysis.getResult();
	}
	
}
