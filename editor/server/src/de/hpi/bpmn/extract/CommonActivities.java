package de.hpi.bpmn.extract;

import java.util.SortedSet;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.extract.exceptions.NoEndNodeException;
import de.hpi.bpmn.extract.exceptions.NoStartNodeException;

public class CommonActivities extends AbstractExtraction {

	public CommonActivities(BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException {
		super(diagramA, diagramB);
	}

	@Override
	public BPMNDiagram extract() throws NoStartNodeException, NoEndNodeException {
		
		SortedSet<CommonEntry> common = this.getCommonNodes(diagramA, diagramB);
		
		for (CommonEntry entry:common) {
			entry.getA().setParent(this.diagram);
		}
		
		return this.diagram;
	}

}
