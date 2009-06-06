package de.hpi.bpmn2yawl;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.analysis.BPMNNormalizer;
import de.hpi.bpmn.XOREventBasedGateway;

import java.util.ArrayList;
import java.util.Vector;

public class BPMN2YAWLNormalizer extends BPMNNormalizer{
	//private BPMNDiagram diagram;
	BPMNDiagram diagram;
	
	public BPMN2YAWLNormalizer(BPMNDiagram diagram){
		super(diagram);
		this.diagram = diagram;
	}
	
	public void normalizeForYAWL(){
		normalize();
	}
	
	@Override
	protected void normalizeMultipleStartEvents(Container process,
			Vector<StartEvent> startEvents){
		if (startEvents.size() < 2)
			return;
		
		StartPlainEvent start = new StartPlainEvent();
		addNode(start, process);
		
		for (StartEvent s : startEvents){
			for (Edge e : s.getOutgoingEdges()){
				Node node = (Node)e.getTarget();
				connectNodes(start, node);
			}
			
			removeNode(s);
		}
	}
	
	private SequenceFlow connectNodes(Node source, Node target) {
		SequenceFlow seqFlow = new SequenceFlow();
		seqFlow.setSource(source);
		seqFlow.setTarget(target);
		diagram.getEdges().add(seqFlow);
		return seqFlow;
	}

	private void addNode(Node node, Container process) {
		diagram.getChildNodes().add(node);
		node.setParent(process);
		node.setProcess(process);
	}
	
	private void removeNode(Node node) {
		diagram.getChildNodes().remove(node);
		node.setParent(null);
		node.setProcess(null);
	}
}
