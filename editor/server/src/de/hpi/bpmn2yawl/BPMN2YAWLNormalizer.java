package de.hpi.bpmn2yawl;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.analysis.BPMNNormalizer;

import java.util.Vector;

public class BPMN2YAWLNormalizer extends BPMNNormalizer{
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
		
		int counter = 0;
		Vector<String> nodeLabels = new Vector<String>();
		StartPlainEvent start = new StartPlainEvent();
		addNode(start, process);
		
		for (StartEvent s : startEvents){
			for (Edge e : s.getOutgoingEdges()){
				Node node = (Node)e.getTarget();
				connectNodes(start, node);
				
				if(nodeLabels.contains(node.getLabel()))
					node.setLabel(node.getLabel() + counter);
				else
					nodeLabels.add(node.getLabel());
				counter++;
			}
			
			removeNode(s);
		}
	}
	
	@Override
	protected void normalizeMultipleEndEvents(Container process,
			Vector<EndEvent> endEvents) {
		for(EndEvent event : endEvents){
			if(event instanceof EndErrorEvent){
				return;
			}
		}
		EndPlainEvent end = new EndPlainEvent();
		addNode(end, process);

		ORGateway gateway = new ORGateway();
		addNode(gateway, process);

		connectNodes(gateway, end);

		int index = 0;
		for (EndEvent e : endEvents) {
			removeNode(e);

			IntermediateEvent iEvent = convertToIntermediateEvent(e);

			addNode(iEvent, process);

			e.getIncomingEdges().get(0).setTarget(iEvent);

			// Id is needed because incoming edges of or-join needs ids to find
			// all combinations
			connectNodes(iEvent, gateway).setId(
					"seq" + String.valueOf(index) + e.getId());
			index++;
		}
	}
	
	protected void addNode(Node node, Container process) {
		diagram.getChildNodes().add(node);
		node.setParent(process);
		node.setProcess(process);
	}
}
