package de.hpi.bpmn.extract;

import java.util.List;
import java.util.SortedSet;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.extract.exceptions.NoEndNodeException;
import de.hpi.bpmn.extract.exceptions.NoStartNodeException;

public class ExtractLowestCommon extends ExtractLargestCommon {

	public ExtractLowestCommon(BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException, NoEndNodeException {
		super(diagramA, diagramB);
	}

	@Override
	public BPMNDiagram extract() throws NoStartNodeException, NoEndNodeException {
		
		SortedSet<CommonEntry> common = getCommonNodes(diagramA, diagramB, true);
		List<Node> removeList = revertList(diagramA, common);
		removeNodes(diagramA, removeList);
		return diagramA;
		
	}

}
