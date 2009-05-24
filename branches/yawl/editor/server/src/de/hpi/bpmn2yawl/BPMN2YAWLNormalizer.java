package de.hpi.bpmn2yawl;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.analysis.BPMNNormalizer;
import de.hpi.bpmn.XOREventBasedGateway;

import java.util.ArrayList;

public class BPMN2YAWLNormalizer{
	private BPMNDiagram diagram;
	
	public BPMN2YAWLNormalizer(BPMNDiagram diagram){
		this.diagram = diagram;
	}
	
	public void normalizeForYAWL(){
		BPMNNormalizer normalizer = new BPMNNormalizer(diagram);
		normalizer.normalize();
		
		for (Container process : diagram.getProcesses()) {
			normalizeElementsInProcess(process);
		}
	}
	
	private void normalizeElementsInProcess(Container process) {
		ArrayList<Node> nodesToChange = new ArrayList<Node>();
		
		for (Node node : process.getChildNodes()) {
			if((node instanceof Event) && !(node instanceof StartEvent)){
				Node predNode = (Node)node.getIncomingEdges().get(0).getSource();
				if(predNode instanceof Event){
					nodesToChange.add(node);
				}
			} else if(node instanceof XOREventBasedGateway){
				Node predNode = (Node)node.getIncomingEdges().get(0).getSource();
				if(predNode instanceof Event){
					nodesToChange.add(node);
				}
			}
		}
		
		for (Node node : nodesToChange){
			addTask(node, process);
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
	
	private void removeEdge(Edge edge) {
		edge.getSource().getOutgoingEdges().remove(edge);
		edge.getTarget().getIncomingEdges().remove(edge);
		diagram.getEdges().remove(edge);
	}
	
	private void addTask(Node node, Container process){
		Node predNode = (Node)node.getIncomingEdges().get(0).getSource();
		
		Task task = new Task();
		task.setId("TBC");
		task.setLabel("Task between conditions");
		addNode(task, process);
		
		removeEdge(node.getIncomingEdges().get(0));
		
		connectNodes(predNode, task);
		connectNodes(task, node);
	}
}
