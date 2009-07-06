package de.hpi.bpmn.extract;

import java.util.SortedSet;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.extract.exceptions.NoEndNodeException;
import de.hpi.bpmn.extract.exceptions.NoStartNodeException;

public class CommonMandatoryActivities extends ExtractLargestCommon {

	public CommonMandatoryActivities(BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException, NoEndNodeException {
		super(diagramA, diagramB);
	}

	@Override
	public BPMNDiagram extract() throws NoStartNodeException, NoEndNodeException {
		
		SortedSet<CommonEntry> common = getCommonNodes(diagramA, diagramB, true);
		
		for (CommonEntry entry:common) {
			entry.getA().setParent(this.diagram);
		}
		
		return this.diagram;
	}

}
