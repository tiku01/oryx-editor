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
		super(diagramB, diagramB);
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
