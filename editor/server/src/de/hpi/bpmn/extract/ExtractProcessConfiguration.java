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

public class ExtractProcessConfiguration extends AbstractExtraction{


	private XORDataBasedGateway initialSplit;
	private XORDataBasedGateway initialJoin;

	
	
	public ExtractProcessConfiguration (BPMNDiagram diagramA, BPMNDiagram diagramB) throws NoStartNodeException, NoEndNodeException{
		super(diagramA, diagramB);
	}
	
	public BPMNDiagram extract() throws NoStartNodeException, NoEndNodeException {
		
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
		
		List<CommonEntry> used = new ArrayList<CommonEntry>();
		
		for (CommonEntry entry:common){
			
			if (used.contains(entry)){
				continue;
			}
			Node pJoin = getXOR();
			Node nSplit = getXOR();
			
			Node leftNode = entry.getA();
			
			// Set initial 
			if (fStart.containsAll(entry.getNodes()) && entry.getNodes().size() == fStart.size()){
				reattachIncomingEdgesFromTo(initialSplit, leftNode);
				reattachIncomingEdgesFromTo(entry.getB(), leftNode);
				pJoin.setParent(null);
				removeAllOutgoingEdges(initialSplit, diagram);
				initialSplit.setParent(null);
				pJoin = leftNode;
			} else {
				reattachIncomingEdgesFromTo(entry.getA(), pJoin);
				reattachIncomingEdgesFromTo(entry.getB(), pJoin);
				getSequenceFlow(pJoin, leftNode);
			}

			CommonEntry curr = entry;
			
			while (curr != null) {
				List<Node> f1 = getFollowedNodes(curr.getA());
				List<Node> f2 = getFollowedNodes(curr.getB());
				curr = null;
				for (CommonEntry cm:common) {
					if (used.contains(cm)){
						continue;
					}
					if (f1.contains(cm.getA()) && f2.contains(cm.getB())) {
						removeAllIncomingEdges(cm.getB(), diagram);
						cm.getB().setParent(null);
						entry.getB().setParent(null);
						leftNode = cm.getA();	
						used.add(cm);
						curr = cm;
						entry = cm;
						break;
					}
				}
			}

			labelEdges(entry.getA().getOutgoingEdges(), getCondition(diagramA));
			labelEdges(entry.getB().getOutgoingEdges(), getCondition(diagramB));
			
			if (pEnd.containsAll(entry.getNodes()) && entry.getNodes().size() == pEnd.size()){
				reattachOutgoingEdgesFromTo(initialJoin, leftNode);
				reattachOutgoingEdgesFromTo(entry.getB(), leftNode);
				nSplit.setParent(null);
				removeAllIncomingEdges(initialJoin,  diagram);
				initialJoin.setParent(null);
				nSplit = leftNode;
			} else {
				reattachOutgoingEdgesFromTo(entry.getA(), nSplit);
				reattachOutgoingEdgesFromTo(entry.getB(), nSplit);
				getSequenceFlow(leftNode, nSplit);
			}
		
			entry.getB().setParent(null);	
			
			used.add(entry);
		}
				
	}
	
	private void labelEdges(List<Edge> edges, String label){
		for (Edge edge:edges) {
			if (edge instanceof SequenceFlow) {
				((SequenceFlow) edge).setConditionExpression(label);
			}
		}
	}
	
	private void removeAllIncomingEdges(Node node, BPMNDiagram diagram){
		List<Edge> iEdges = new ArrayList<Edge>(node.getIncomingEdges());
		for (Edge edge : iEdges){
			edge.setTarget(null);
			edge.setSource(null);
			if (diagram.getEdges().contains(edge)) {
				diagram.getEdges().remove(edge);
			}
		}
	}

	private void removeAllOutgoingEdges(Node node, BPMNDiagram diagram){
		List<Edge> iEdges = new ArrayList<Edge>(node.getOutgoingEdges());
		for (Edge edge : iEdges){
			edge.setTarget(null);
			edge.setSource(null);
			if (diagram.getEdges().contains(edge)) {
				diagram.getEdges().remove(edge);
			}
		}
	}
	
	private void reattachIncomingEdgesFromTo(Node from, Node to){
		
		List<Edge> iEdges = new ArrayList<Edge>(from.getIncomingEdges());
		for (Edge edge : iEdges){
			if (edge instanceof SequenceFlow){ 
				edge.setTarget(to);
			} else { 
			}
		}
		
	}

	private void reattachOutgoingEdgesFromTo(Node from, Node to){
		
		List<Edge> iEdges = new ArrayList<Edge>(from.getOutgoingEdges());
		for (Edge edge : iEdges){
			if (edge instanceof SequenceFlow){ 
				edge.setSource(to);
			} else { 
			}
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
			//f.setConditionExpression(getCondition(diagram));
		}		
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
	 * Returns an condition which is unique for that diagram
	 * @param diamgram
	 * @return
	 */
	private String getCondition(BPMNDiagram diagram){
		return 	  "If process == \"" 
				+ (diagram.getTitle() == null || "".equals(diagram.getTitle().trim()) ? diagram.getId() : diagram.getTitle().trim()) 
				+ "\""; 
	}
	
	
	@Override
	protected void initDiagram() throws NoStartNodeException, NoEndNodeException {
		
		super.initDiagram();
		
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

	
}
