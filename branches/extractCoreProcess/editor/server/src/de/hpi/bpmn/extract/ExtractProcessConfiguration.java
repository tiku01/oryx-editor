package de.hpi.bpmn.extract;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import net.sf.saxon.exslt.Common;

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
 * @author Willi Tscheschner
 *
 */

public class ExtractProcessConfiguration {

	private BPMNDiagram diagramA;
	private BPMNDiagram diagramB;
	
	private BPMNDiagram diagram;
	private BPMNFactory factory;
	

	private XORDataBasedGateway initialSplit;
	private XORDataBasedGateway initialJoin;
	
	private List<Node> startNodesA;
	private List<Node> startNodesB;
	
	
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

		startNodesA = getStartNodes(diagramA);
		startNodesB = getStartNodes(diagramB);

		List<CommonEntry> common = stripCommonNodes(getCommonNodes(diagramA, diagramB));
		
		// Attach initial split and join
		attachInitialSplit(diagramA);
		attachInitialSplit(diagramB);
		attachInitialJoin(diagramA);
		attachInitialJoin(diagramB);
		
		addAll(diagramA);
		addAll(diagramB);

		compress(common);		
		
		return diagram;
	}
	
	private void compress(List<CommonEntry> common){

		List<Node> fStart = getFollowedNodes(initialSplit);
		List<Node> pEnd = getPreviousedNodes(initialJoin);
		
		for (CommonEntry entry:common){
			
			XORDataBasedGateway pJoin = getXOR();
			XORDataBasedGateway nSplit = getXOR();
			
			boolean pRemoved = false;
			boolean nRemoved = false;
			
			Node leftNode = entry.getA();
			
			// Set initial 
			if (fStart.containsAll(entry.getNodes()) && entry.getNodes().size() == fStart.size()){
				pRemoved = true;
			}

			if (pEnd.containsAll(entry.getNodes()) && entry.getNodes().size() == pEnd.size()){
				nRemoved = true;
			}
			
			
			for (Node node:entry.getNodes()){
				// Reattach all incoming sf to the join and msgf to the node a
				List<Edge> iEdges = new ArrayList<Edge>(node.getIncomingEdges());
				for (Edge edge : iEdges){
					if (edge instanceof SequenceFlow){ 
						if (pRemoved){
							edge.setTarget(null);
							edge.setSource(null);
							if (diagram.getEdges().contains(edge))
								diagram.getEdges().remove(edge);
						} else {
							edge.setTarget(pJoin);
						}
					} else { edge.setTarget(leftNode);
					}
				}
				// Reattach all outgoing sf to the split and msgf to the node a
				List<Edge> oEdges = new ArrayList<Edge>(node.getOutgoingEdges());
				for (Edge edge : oEdges){
					if (edge instanceof SequenceFlow){ 
						if (nRemoved){
							edge.setTarget(null);
							edge.setSource(null);
							if (diagram.getEdges().contains(edge))
								diagram.getEdges().remove(edge);
						} else {
							edge.setSource(nSplit);
							if (entry.isDiagramA(node)){
								((SequenceFlow) edge).setConditionExpression(getCondition(diagramA));
							} else if (entry.isDiagramB(node)){
								((SequenceFlow) edge).setConditionExpression(getCondition(diagramB));
							}
						}
					} else { edge.setSource(leftNode); }
				}
			}

			if (pRemoved){
				pJoin.setParent(null);
				initialSplit.getIncomingSequenceFlows().get(0).setTarget(leftNode);
				initialSplit.setParent(null);
			} else {
				getSequenceFlow(pJoin, leftNode);
			}
			
			if (nRemoved){
				nSplit.setParent(null);
				initialJoin.getOutgoingSequenceFlows().get(0).setSource(leftNode);
				initialJoin.setParent(null);
			} else {
				getSequenceFlow(leftNode, nSplit);
			}

			entry.getB().setParent(null);
			
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
	
	/**
	 * Returns a set on nodes which are contained in both process models
	 * @return
	 * @throws NoStartNodeException 
	 */
	private SortedSet<CommonEntry> getCommonNodes(BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException{

		List<Node> nodesA = diagramA.getAllChildNodes();
		List<Node> nodesB = diagramB.getAllChildNodes();
		
		SortedSet<CommonEntry> sortedCommons = new TreeSet<CommonEntry>(new CommonEntryComparator());  
		
		for (Node nodeA:nodesA){
			if (nodeA.getLabel() == null || "".equals(nodeA.getLabel().trim())){
				continue;
			}
			
			String label = nodeA.getLabel().trim();
			// Get the min count from a start node to the node
			int  count = -1;
			for (Node startNode:startNodesA){
				count = count != -1 ? Math.min(count, countPathSteps(startNode, nodeA)) : countPathSteps(startNode, nodeA);
			}
			// Node is not in the normal flow
			if (count == -1){
				continue;
			}
			
			List<Node> commonNodes = new ArrayList<Node>();
			for (Node nodeB:nodesB){
				if (nodeB.getLabel() == null || "".equals(nodeB.getLabel().trim())){
					continue;
				}
				if (label.equals(nodeB.getLabel().trim())) {
					commonNodes.add(nodeB);
				}
			}
			
			for (Node nodeB:commonNodes){
				// Get the min count from a start node to the node
				int  countB = -1;
				for (Node startNode:startNodesB){
					countB = countB != -1 ? Math.min(countB, countPathSteps(startNode, nodeB)) : countPathSteps(startNode, nodeB);
				}
				// Node is not in the normal flow
				if (countB == -1){
					continue;
				}
								
				sortedCommons.add(new CommonEntry(nodeA, count, nodeB, countB));
				
			}			
		}
		
		return sortedCommons;
	}
	
	/**
	 * Remove all common entries which are no in the flow of the algorithm
	 * @param common
	 * @return
	 */
	private List<CommonEntry> stripCommonNodes(SortedSet<CommonEntry> common){
		
		List<CommonEntry> stripped = new ArrayList<CommonEntry>();

		List<Node> usedNodesA = new ArrayList<Node>();
		List<Node> usedNodesB = new ArrayList<Node>();
		
		// Get all common entries
		for (CommonEntry entry:common){
			if (!isOnPathBetween(entry.getA(), startNodesA, usedNodesA) &&
				!isOnPathBetween(entry.getB(), startNodesB, usedNodesB) ){
				
				stripped.add(entry);
				usedNodesA.add(entry.getA());
				usedNodesB.add(entry.getB());
				
			}
			
		}

		return stripped;
	}
	
	/**
	 * Returns a TRUE if the Node is on a path
	 * between the start nodes and the end nodes
	 * @param start
	 * @param end
	 * @param node
	 * @return
	 */
	private boolean isOnPathBetween(Node node, List<Node> start, List<Node> end) {
		
		if (start.size() <= 0 || end.size() <= 0 || node == null){
			return false;
		}
		
		boolean has = false;
		for (Node f:start){
			has = hasAPath(f, node);
			if (has){ break; }
		}
		
		if (!has){ return false;
		} else { has = false; }
	
		for (Node t:end){
			has = hasAPath(node, t);
			if (has){ break; }
		}
		return has;
	}
	
	/**
	 * Returns a TRUE if there is a path which goes from 'from' to 'to'
	 * @param from
	 * @param to
	 * @return
	 */
	private boolean hasAPath(Node from, Node to){
		return countPathSteps(from, to) >= 0;
	}
	
	/**
	 * Returns the number of steps which is needed to come 
	 * from 'from' and goes to 'to'. (If there is no path, it will
	 * return -1)
	 * @param from
	 * @param to
	 * @return
	 */
	private int countPathSteps(Node from, Node to){
		
		if (from == to){
			return 0;
		}
		
		// Get all followed nodes
		List<Node> fNodes = getFollowedNodes(from);
		
		// If containing
		if (fNodes.contains(to)){
			return 1; // Return one
		} else {
			// Otherwise, go thru every next node
			for (Node fNode:fNodes){
				// Get step
				int step = countPathSteps(fNode, to);
				if (step >= 0){
					// Increase step
					return step+1;
				}
			}
			return -1;
		}
	
	}
	
	/**
	 * Returns a list off all nodes which are followed 
	 * (direct or indirect) by the node
	 * @param node
	 * @return
	 */
	private List<Node> getAllFollowedNodes(Node node){
		
		List<Node> nodes = new ArrayList<Node>();
		
		for (Node fNode:getFollowedNodes(node)){
			if (!nodes.contains(fNode)) {
				nodes.addAll(getAllFollowedNodes(fNode));
			}
		}
		
		return nodes;
		
	}

	/**
	 * Return a list of all nodes which are followed to the
	 * given node.
	 * @param node
	 * @return
	 */
	private List<Node> getFollowedNodes(Node node){

		List<Node> nodes = new ArrayList<Node>();
		for (SequenceFlow flow:node.getOutgoingSequenceFlows()){
			if (flow.getTarget() != null && 
				flow.getTarget() instanceof Node && 
				!nodes.contains(flow.getTarget()) &&
				flow.getTarget() != node)
				
				nodes.add((Node)flow.getTarget());
		}
		return nodes;
		
	}
	
	/**
	 * Return a list of all nodes which are previous to the
	 * given node.
	 * @param node
	 * @return
	 */
	private List<Node> getPreviousedNodes(Node node){

		List<Node> nodes = new ArrayList<Node>();
		for (SequenceFlow flow:node.getIncomingSequenceFlows()){
			if (flow.getSource() != null && 
				flow.getSource() instanceof Node && 
				!nodes.contains(flow.getSource()) &&
				flow.getSource() != node)
				
				nodes.add((Node)flow.getSource());
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
		getSequenceFlow(start, initialSplit);
		getSequenceFlow(initialJoin, end);
		
		setIds(initialJoin, initialSplit, start, end);
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
	
	/**
	 * Creates a sequence flow from 'from' to 'to'
	 * and returns it
	 * @param source
	 * @param target
	 * @return
	 */
	private SequenceFlow getSequenceFlow(DiagramObject source, DiagramObject target) {
		SequenceFlow f = factory.createSequenceFlow();
		f.setSource(source);
		f.setTarget(target);
		addEdges(f);
		setIds(f);
		return f;
	}
	
	/**
	 * Add all edges to the this.diagram
	 * @param edges
	 */
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
	
	class CommonEntryComparator implements Comparator<CommonEntry> {
	  public int compare (CommonEntry o1, CommonEntry o2) {
		int distance1 = Math.abs(-o1.getCountA()+o1.getCountB());
		int distance2 = Math.abs(-o2.getCountA()+o2.getCountB());
		int start1 = Math.min(o1.getCountA(), o1.getCountB());
		int start2 = Math.min(o2.getCountA(), o2.getCountB());
		int val = (distance1+1)*(start1+1) - (distance2+1)*(start2+1);
	    return val == 0 ? -1 : val;
	  }
	}

	
	private class CommonEntry implements Comparable<CommonEntry>{
		private Node a,b;
		private int countA, countB;
		public CommonEntry(Node a, int countA, Node b, int countB){
			this.a = a;
			this.b = b;
			this.countA = countA;
			this.countB = countB;
		}
		
		public boolean isDiagramA(Node n){
			return n == a;
		}		
		public boolean isDiagramB(Node n){
			return n == b;
		}
		public Node getA(){
			return a;
		}
		public Node getB(){
			return b;
		}
		public List<Node> getNodes(){
			List<Node> nodes = new ArrayList<Node>();
			nodes.add(a);
			nodes.add(b);
			return nodes;
		}
		public int getCountA(){
			return countA;
		}
		public int getCountB(){
			return countB;
		}

		public int compareTo(CommonEntry c) {
			return Math.abs(-countA+countB) - Math.abs(-c.getCountA()+c.getCountB());
		}		
	}
	
}
