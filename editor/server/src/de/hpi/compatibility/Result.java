package de.hpi.compatibility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tue.tm.is.graph.TwoTransitionSets;

public class Result {

	private int nrComplexProjectionCorrespondences = 0;
	private int nrSimpleProjectionCompatible = 0;
	private int nrComplexProjectionCompatible = 0;
	private int nrSimpleProjectionIncompatible = 0;
	private int nrComplexProjectionIncompatible = 0;

	private int nrComplexProtocolCorrespondences = 0;
	private int nrSimpleProtocolCompatible = 0;
	private int nrComplexProtocolCompatible = 0;
	private int nrSimpleProtocolIncompatible = 0;
	private int nrComplexProtocolIncompatible = 0;
	
	private Map<String, Boolean> compatibilityResults = new HashMap<String, Boolean>();
	
	public void addProjectionCorrespondenceResult(TwoTransitionSets c1, TwoTransitionSets c2, boolean areCompatible) {
		if (areCompatible) {
			if (c1.isComplex() || c2.isComplex())
				nrComplexProjectionCompatible++;
			else
				nrSimpleProjectionCompatible++;
		} else {
			if (c1.isComplex() || c2.isComplex())
				nrComplexProjectionIncompatible++;
			else
				nrSimpleProjectionIncompatible++;
		}
		compatibilityResults.put(c1.toString() + " - " + c2.toString(), areCompatible);
	}
	
	public void addProtocolCorrespondenceResult(TwoTransitionSets c1, TwoTransitionSets c2, boolean areCompatible) {
		if (areCompatible) {
			if (c1.isComplex() || c2.isComplex())
				nrComplexProtocolCompatible++;
			else
				nrSimpleProtocolCompatible++;
		} else {
			if (c1.isComplex() || c2.isComplex())
				nrComplexProtocolIncompatible++;
			else
				nrSimpleProtocolIncompatible++;
		}
	}
	
	public void addComplexProjectionCorrespondence() {
		nrComplexProjectionCorrespondences++;
	}
	
	public void addComplexProtocolCorrespondence() {
		nrComplexProtocolCorrespondences++;
	}
	
	public String getResult(List<TwoTransitionSets> correspondences) {
		String result = "\nNR CORRESPONDENCES:" + correspondences.size();

		result += "\nNR PROJECTION COMPLEX CORRESPONDENCES:" + nrComplexProjectionCorrespondences;
		result += "\nNR PROJECTION SIMPLE COMPATIBLE CORRESPONDENCES:" + nrSimpleProjectionCompatible;
		result += "\nNR PROJECTION SIMPLE INCOMPATIBLE CORRESPONDENCES:" + nrSimpleProjectionIncompatible;
		result += "\nNR PROJECTION COMPLEX COMPATIBLE CORRESPONDENCES:" + nrComplexProjectionCompatible;
		result += "\nNR PROJECTION COMPLEX INCOMPATIBLE CORRESPONDENCES:" + nrComplexProjectionIncompatible;
		
		result += "\nNR PROTOCOL COMPLEX CORRESPONDENCES:" + nrComplexProtocolCorrespondences;
		result += "\nNR PROTOCOL SIMPLE COMPATIBLE CORRESPONDENCES:" + nrSimpleProtocolCompatible;
		result += "\nNR PROTOCOL SIMPLE INCOMPATIBLE CORRESPONDENCES:" + nrSimpleProtocolIncompatible;
		result += "\nNR PROTOCOL COMPLEX COMPATIBLE CORRESPONDENCES:" + nrComplexProtocolCompatible;
		result += "\nNR PROTOCOL COMPLEX INCOMPATIBLE CORRESPONDENCES:" + nrComplexProtocolIncompatible;

		/*for (Map.Entry<String, Boolean> cr : compatibilityResults.entrySet()) {
			result += "\n" + cr.getKey() + ", " + cr.getValue();
		}*/

		return result;
	}
}
