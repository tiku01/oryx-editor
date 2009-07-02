package de.hpi.bpmn.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.extract.exceptions.NoEndNodeException;
import de.hpi.bpmn.extract.exceptions.NoStartNodeException;
import de.hpi.diagram.OryxUUID;
import de.hpi.util.Bounds;

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

		HashMap<String, ArrayList<Node>> commonNodes = getCommonNodes(diagramA, diagramB);
		
		compress(commonNodes);
		
		// Attach initial split and join
		attachInitialSplit(diagramA);
		attachInitialSplit(diagramB);
		attachInitialJoin(diagramA);
		attachInitialJoin(diagramB);

		addAll(diagramA);
		addAll(diagramB);
		
		return diagram;
	}
	
	private void compress(HashMap<String, ArrayList<Node>> common){
		
		for (ArrayList<Node> nodes:common.values()){

			for (Node node:nodes) {
				node.setParent(null);
			}

			XORDataBasedGateway p = getXOR();
			XORDataBasedGateway n = getXOR();
			
			getSequenceFlow(p, nodes.get(0));
			getSequenceFlow(nodes.get(0), n);
			nodes.get(0).setParent(this.diagram);
		}
				
	}
	
	/**
	 * Add all elements from the diagram 
	 * to the this.diagram
	 * @param diagram
	 */
	private void addAll(BPMNDiagram diagram) {
		
		List<Node> nodes = new ArrayList<Node>();
		List<Edge> edges = new ArrayList<Edge>();
		
		// Add all child from A to diagram
		for (Edge edge:diagram.getEdges()) {
			edges.add(edge);
		}
		
		
		// Add all child from A to diagram
		for (Node node:diagram.getChildNodes()) {
			nodes.add(node);
		}
		
		for (Node node:nodes) {
			setIds(node);
			node.setParent(this.diagram);
		}
		
		for (Edge edge:edges) {
			// If the source and target is included in the diagram, add it
			if (this.diagram.getChildNodes().contains(edge.getSource()) && this.diagram.getChildNodes().contains(edge.getTarget())) {
				setIds(edge);
				diagram.getEdges().remove(edge);
				this.diagram.getEdges().add(edge);
			}
		}
	
	}
	
	private HashMap<String, ArrayList<Node>> getCommonNodes(BPMNDiagram diagramA, BPMNDiagram diagramB){

		List<Node> nodesA = getAllChildNodes(diagramA);
		List<Node> nodesB = getAllChildNodes(diagramB);	
		
		
		HashMap<String, ArrayList<Node>>  nodes = new HashMap<String, ArrayList<Node>>();
		
		// Add all common nodes from A
		for (Node nodeA:nodesA){
			if (nodeA.getLabel() == null || "".equals(nodeA.getLabel())){
				continue;
			}
			String label = nodeA.getLabel().trim();
			for (Node nodeB:nodesB){
				if (nodeB.getLabel() != null && label.equals(nodeB.getLabel().trim())) {
					if (!nodes.containsKey(label)) {
						nodes.put(label, new ArrayList<Node>());
					}
					nodes.get(label).add(nodeA);
				}
			}
		}

		// Add all common nodes from B
		for (Node nodeB:nodesB){
			if (nodeB.getLabel() == null || "".equals(nodeB.getLabel())){
				continue;
			}
			String label = nodeB.getLabel().trim();
			for (Node nodeA:nodesA){
				if (nodeA.getLabel() != null && label.equals(nodeA.getLabel().trim())) {
					if (!nodes.containsKey(label)) {
						nodes.put(label, new ArrayList<Node>());
					}
					nodes.get(label).add(nodeB);
				}
			}
		}
		
		return nodes;
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
		
		List<Node> nodes = new ArrayList<Node>();
		
		// Get thru all nodes without incoming edges
		for (Node start:getStartNodes(diagram)){
			// If its a start node, 
			// remove the node and add all followed 
			// nodes to the start list
			if (start instanceof StartEvent) {
				List<SequenceFlow> edges = start.getOutgoingSequenceFlows();
				for (SequenceFlow edge:edges){
					if (edge.getTarget()!= null && edge.getTarget() instanceof Node)
						nodes.add((Node)edge.getTarget());
					if (diagram.getEdges().contains(edge))
						diagram.getEdges().remove(edge);
				}
				start.setParent(null);
			} else {
				nodes.add(start);
			}
		}
		
		for (Node start:nodes){	
			if (start instanceof StartEvent) {
				continue;
			}
			SequenceFlow f = getSequenceFlow(initialSplit, start);
			f.setConditionExpression(getCondition(diagram));
		}		
	}

	
	/**
	 * Attaches all end nodes from the diagram to the initial join
	 * @param diagram
	 * @throws NoStartNodeException
	 * @throws NoEndNodeException 
	 */
	private void attachInitialJoin(BPMNDiagram diagram) throws NoEndNodeException{
		
		List<Node> nodes = new ArrayList<Node>();
		// Get thru all nodes without outgoing edges
		for (Node end:getEndNodes(diagram)){
			// If its a start node, 
			// remove the node and add all previous 
			// nodes to the end list
			if (end instanceof EndEvent) {
				List<SequenceFlow> edges = end.getIncomingSequenceFlows();
				for (SequenceFlow edge:edges){
					if (edge.getSource()!= null && edge.getSource() instanceof Node)
						nodes.add((Node)edge.getSource());
					if (diagram.getEdges().contains(edge))
						diagram.getEdges().remove(edge);
				}
				
				end.setParent(null);
			} else {
				nodes.add(end);
			}
		}
		
		for (Node end:nodes){
			SequenceFlow f = getSequenceFlow(end, initialJoin);
			f.setConditionExpression(getCondition(diagram));
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
	
	/**
	 * Initialize the diagram with the initial start/end events and their gateways
	 */
	private void initDiagram(){
		
		// Set up diagram
		diagram = new BPMNDiagram();
		diagram.setId(OryxUUID.generate());
		diagram.setTitle("Extract Process Configuration from: " + diagramA.getTitle() + ", " + diagramB.getTitle());
		
		// Init events
		StartEvent start = factory.createStartPlainEvent();
		start.setParent(diagram);
		start.setBounds(new Bounds(0,0,30,30));

		EndEvent end = factory.createEndPlainEvent();
		end.setParent(diagram);
		end.setBounds(new Bounds(0,0,30,30));

		// Init xors
		initialSplit = getXOR();
		initialJoin = getXOR();
		
		// Init flows
		SequenceFlow f1 = getSequenceFlow(start, initialSplit);
		SequenceFlow f2 = getSequenceFlow(initialJoin, end);
		
		setIds(initialJoin, initialSplit);
	}
	
	/**
	 * Returns a xor gateway, contained
	 * in the this.diagram
	 * @return
	 */
	private XORDataBasedGateway getXOR(){
		
		XORDataBasedGateway xor = factory.createXORDataBasedGateway();
		xor.setParent(diagram);
		xor.setBounds(new Bounds(0,0,40,40));
		setIds(xor);
		return xor;
	}
	
	private SequenceFlow getSequenceFlow(DiagramObject source, DiagramObject target) {
		SequenceFlow f = factory.createSequenceFlow();
		f.setSource(source);
		f.setTarget(target);
		addEdges(f);
		setIds(f);
		return f;
	}
	
	private void addEdges(Edge ... edges) {
		for (Edge edge : edges){
			diagram.getEdges().add(edge);
		}
	}
	
	/**
	 * Set the ids and resource ids for a given set of diagram objects
	 * @param objs
	 */
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
	private String getCondition(BPMNDiagram diagram){
		return 	  "If process == \"" 
				+ (diagram.getTitle() == null || "".equals(diagram.getTitle().trim()) ? diagram.getId() : diagram.getTitle().trim()) 
				+ "\""; 
	}
	
}
