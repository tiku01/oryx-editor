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

abstract public class AbstractExtraction {

	protected BPMNDiagram diagramA;
	protected BPMNDiagram diagramB;
	
	protected BPMNDiagram diagram;
	protected BPMNFactory factory;

	protected List<Node> startNodesA;
	protected List<Node> startNodesB;
	
	protected List<Node> endNodesA;
	protected List<Node> endNodesB;
	
	
	public AbstractExtraction (BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException, NoEndNodeException{

		this.diagramA = diagramA;
		this.diagramB = diagramB;
		
		this.factory = new BPMNFactory();

		this.initDiagram();
	}
	
	abstract public BPMNDiagram extract() throws NoStartNodeException, NoEndNodeException;
	
	
	/**
	 * Returns a set on nodes which are contained in both process models
	 * @return
	 * @throws NoStartNodeException 
	 */
	protected SortedSet<CommonEntry> getCommonNodes(BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException{

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
	 * Remove all common entries which are not in the flow of the algorithm
	 * @param common
	 * @return
	 */
	protected List<CommonEntry> stripCommonNodes(SortedSet<CommonEntry> common){
		
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
	protected boolean isOnPathBetween(Node node, List<Node> start, List<Node> end) {
		
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
	protected boolean hasAPath(Node from, Node to){
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
	protected int countPathSteps(Node from, Node to){
		
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
	protected List<Node> getAllFollowedNodes(Node node){
		
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
	protected List<Node> getFollowedNodes(Node node){

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
	protected List<Node> getPreviousedNodes(Node node){

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
	 * Returns a list on start nodes for the given diagram.
	 * A end node is a node without any incoming edges.
	 * @param diagram
	 * @return
	 * @throws NoStartNodeException
	 */
	protected List<Node> getStartNodes(BPMNDiagram diagram) throws NoStartNodeException{
		
		if (diagram == diagramA && startNodesA != null){
			return startNodesA;
		} else if (diagram == diagramB && startNodesB != null) {
			return startNodesB;
		}
		
		
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
	protected List<Node> getEndNodes(BPMNDiagram diagram) throws NoEndNodeException{
		
		if (diagram == diagramA && endNodesA != null){
			return endNodesA;
		} else if (diagram == diagramB && endNodesB != null) {
			return endNodesB;
		}
		
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
	 * @throws NoStartNodeException 
	 * @throws NoEndNodeException 
	 */
	protected void initDiagram() throws NoStartNodeException, NoEndNodeException{
		
		// Set up diagram
		diagram = new BPMNDiagram();
		diagram.setId(OryxUUID.generate());
		diagram.setTitle("Extract Process Configuration from: " + diagramA.getTitle() + ", " + diagramB.getTitle());

		startNodesA = getStartNodes(diagramA);
		startNodesB = getStartNodes(diagramB);
		
		endNodesA = getEndNodes(diagramA);
		endNodesB = getEndNodes(diagramB);

	}

	
	/**
	 * Add all edges to the this.diagram
	 * @param edges
	 */
	protected void addEdges(Edge ... edges) {
		for (Edge edge : edges){
			diagram.getEdges().add(edge);
		}
	}
	
	/**
	 * Set the ids and resource ids for a given set of diagram objects
	 * @param objs
	 */
	protected void setIds(DiagramObject ... objs){
		
		for (DiagramObject obj : objs){
			obj.setResourceId(OryxUUID.generate());
			obj.setId(obj.getResourceId());
		}
		
	}
	
	
	protected class CommonEntryComparator implements Comparator<CommonEntry> {
	  public int compare (CommonEntry o1, CommonEntry o2) {
		int distance1 = Math.abs(-o1.getCountA()+o1.getCountB());
		int distance2 = Math.abs(-o2.getCountA()+o2.getCountB());
		int start1 = Math.min(o1.getCountA(), o1.getCountB());
		int start2 = Math.min(o2.getCountA(), o2.getCountB());
		int val = (distance1+1)*(start1+1) - (distance2+1)*(start2+1);
	    return val == 0 ? -1 : val;
	  }
	}

	
	protected class CommonEntry implements Comparable<CommonEntry>{
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
