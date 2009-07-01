package de.hpi.bpmn.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.extract.exceptions.NoEndNodeException;
import de.hpi.bpmn.extract.exceptions.NoStartNodeException;
import de.hpi.diagram.OryxUUID;

/**
 * 
 * @author willitscheschner
 *
 */

public class ExtractProcessConfiguration {

	private BPMNDiagram diagramA;
	private BPMNDiagram diagramB;
	
	private BPMNDiagram diagram;
	private BPMNFactory factory;
	

	private XORDataBasedGateway initialSplit;
	private XORDataBasedGateway initialJoin;
	
	
	public ExtractProcessConfiguration (BPMNDiagram diagramA, BPMNDiagram diagramB){

		this.diagramA = diagramA;
		this.diagramB = diagramB;
		
		this.factory = new BPMNFactory();
		
	}
	
	public BPMNDiagram extract() throws NoStartNodeException, NoEndNodeException{

		this.initDiagram();
		
		return combine();
	}
	
	private BPMNDiagram combine() throws NoStartNodeException, NoEndNodeException {

		List<Node> commonNodes = getCommonNodes(diagramA, diagramB);
		
		// Attach initial split and join
		attachInitialSplit(diagramA);
		attachInitialSplit(diagramB);
		attachInitialJoin(diagramA);
		attachInitialJoin(diagramB);

		addAll(diagramA);
		addAll(diagramB);
		
		return diagram;
	}
	
	private void addAll(BPMNDiagram diagram) {
		
		List<Node> nodes = new ArrayList<Node>();
		
		// Add all child from A to diagram
		for (Node node:diagram.getChildNodes()) {
			nodes.add(node);
		}
		
		for (Node node:nodes) {
			node.setParent(this.diagram);
		}

		List<Edge> edges = new ArrayList<Edge>();
		
		// Add all child from A to diagram
		for (Edge edge:diagram.getEdges()) {
			edges.add(edge);
		}
		
		for (Edge edge:edges) {
			diagram.getEdges().remove(edge);
			this.diagram.getEdges().add(edge);
		}
		
	}
	
	private List<Node> getCommonNodes(BPMNDiagram diagramA, BPMNDiagram diagramB){

		List<Node> nodesA = getAllChildNodes(diagramA);
		List<Node> nodesB = getAllChildNodes(diagramB);	
		
		List<Node> nodes = new ArrayList<Node>();
		
		// Add all common nodes from A
		for (Node nodeA:nodesA){
			if (nodeA.getLabel() == null || "".equals(nodeA.getLabel())){
				continue;
			}
			for (Node nodeB:nodesB){
				if (nodeA.getLabel().equals(nodeB.getLabel()) && !containsTitle(nodes, nodeA.getLabel())) {
					nodes.add(nodeA);
					break;
				}
			}
		}

		// Add all common nodes from B
		for (Node nodeB:nodesB){
			if (nodeB.getLabel() == null || "".equals(nodeB.getLabel())){
				continue;
			}
			for (Node nodeA:nodesA){
				if (nodeB.getLabel().equals(nodeA.getLabel()) && !containsTitle(nodes, nodeB.getLabel())) {
					nodes.add(nodeB);
					break;
				}
			}
		}
		
		return nodes;
	}
	
	private boolean containsTitle(List<Node> nodes, String title) {
		for (Node node:nodes){
			if (title.equals(node.getLabel())) {
				return true;
			}
		}
		return false;
	}
	
	
	
	private List<Node> getAllChildNodes(Container container){
		
		List<Node> nodes = container.getChildNodes();
		for (Node node:container.getChildNodes()){
			if (node instanceof Container) {
				nodes.addAll(getAllChildNodes((Container) node));
			}
		}
		return nodes;
	}
	
	
	/**
	 * Attaches all start nodes from the diagram to the initial split
	 * @param diagram
	 * @throws NoStartNodeException
	 */
	private void attachInitialSplit(BPMNDiagram diagram) throws NoStartNodeException{
		for (Node start:getStartNodes(diagram)){
			SequenceFlow f = factory.createSequenceFlow();
			f.setSource(initialSplit);
			f.setTarget(start);
			f.setConditionExpression(getCondition(diagram));
			setIds(f);
			addEdges(f);
		}		
	}

	
	/**
	 * Attaches all end nodes from the diagram to the initial join
	 * @param diagram
	 * @throws NoStartNodeException
	 * @throws NoEndNodeException 
	 */
	private void attachInitialJoin(BPMNDiagram diagram) throws NoEndNodeException{
		for (Node end:getEndNodes(diagram)){
			SequenceFlow f = factory.createSequenceFlow();
			f.setSource(end);
			f.setTarget(initialJoin);
			f.setConditionExpression(getCondition(diagram));
			setIds(f);
			addEdges(f);
		}		
	}
	
	/**
	 * Returns a list on start nodes for the given diagram.
	 * A end node is a node without any incoming edges.
	 * @param diagram
	 * @return
	 * @throws NoStartNodeException
	 */
	private List<Node> getStartNodes(BPMNDiagram diagram) throws NoStartNodeException{
		
		List<Node> nodes = new ArrayList<Node>();
		
		for (Node node:diagram.getChildNodes()){
			if (node.getIncomingEdges().size() == 0){
				nodes.add(node);
			}
		}
		
		if (nodes.size() == 0){
			throw new NoStartNodeException();
		}
		
		return nodes;
	}

	/**
	 * Returns a list on end nodes for the given diagram.
	 * A end node is a node without any outgoing edges.
	 * @param diagram
	 * @return
	 * @throws NoEndNodeException 
	 * @throws NoStartNodeException
	 */
	private List<Node> getEndNodes(BPMNDiagram diagram) throws NoEndNodeException{
		
		List<Node> nodes = new ArrayList<Node>();
		
		for (Node node:diagram.getChildNodes()){
			if (node.getOutgoingEdges().size() == 0){
				nodes.add(node);
			}
		}
		
		if (nodes.size() == 0){
			throw new NoEndNodeException();
		}
		
		return nodes;
	}
	
	private void initDiagram(){
		
		// Set up diagram
		diagram = new BPMNDiagram();
		diagram.setId(OryxUUID.generate());
		diagram.setTitle("Extract Process Configuration from: " + diagramA.getTitle() + ", " + diagramB.getTitle());
		
		// Init events
		StartEvent start = factory.createStartPlainEvent();
		start.setParent(diagram);

		StartEvent end = factory.createStartPlainEvent();
		end.setParent(diagram);

		// Init xors
		initialSplit = factory.createXORDataBasedGateway();
		initialSplit.setParent(diagram);

		initialJoin = factory.createXORDataBasedGateway();
		initialJoin.setParent(diagram);
		
		// Init flows
		SequenceFlow f1 = factory.createSequenceFlow();
		f1.setSource(start);
		f1.setTarget(initialSplit);
		
		SequenceFlow f2 = factory.createSequenceFlow();
		f2.setSource(start);
		f2.setTarget(initialSplit);
		
		addEdges(f1, f2);
		setIds(initialJoin, initialSplit, start, end, f1, f2);
	}
	
	private void addEdges(Edge ... edges) {
		for (Edge edge : edges){
			diagram.getEdges().add(edge);
		}
	}
	
	private void setIds(DiagramObject ... objs){
		
		for (DiagramObject obj : objs){
			obj.setResourceId(OryxUUID.generate());
			obj.setId(obj.getResourceId());
		}
		
	}
	
	/**
	 * Returns an condition which is unique for that diagram
	 * @param diamgram
	 * @return
	 */
	private String getCondition(BPMNDiagram diamgram){
		return 	  "If process == \"" 
				+ ("".equals(diagram.getTitle().trim()) ? diagram.getId() : diagram.getTitle().trim()) 
				+ "\""; 
	}
	
}
