package de.hpi.bpmn2_0.validation;

import java.util.HashMap;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.diagram.verification.AbstractSyntaxChecker;

/**
 * Copyright (c) 2009 Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class BPMN2SyntaxChecker extends AbstractSyntaxChecker {

	protected static final String NO_SOURCE = "BPMN_NO_SOURCE";
	protected static final String NO_TARGET = "BPMN_NO_TARGET";
	protected static final String DIFFERENT_PROCESS = "BPMN_DIFFERENT_PROCESS";
	protected static final String SAME_PROCESS = "BPMN_SAME_PROCESS";
	protected static final String FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS";
	protected static final String ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW";
	protected static final String STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW";
//	protected static final String INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "BPMN_INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW";
	protected static final String STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW";
	protected static final String ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW";
	protected static final String ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW";
	protected static final String ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW";
	protected static final String EVENTBASEDGATEWAY_BADCONTINUATION = "BPMN_EVENTBASEDGATEWAY_BADCONTINUATION";
	protected static final String NODE_NOT_ALLOWED = "BPMN_NODE_NOT_ALLOWED";
	
	protected static final String DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION";
	protected static final String DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION";

	private Definitions defs;
		
//	public HashSet<String> allowedNodes;
//	public HashSet<String> forbiddenNodes;
	
	public BPMN2SyntaxChecker(Definitions defs) {
		this.defs = defs;
		this.errors = new HashMap<String, String>();
		
//		this.allowedNodes = new HashSet<String>();
//		this.forbiddenNodes = new HashSet<String>();
	}

	@Override
	public boolean checkSyntax() {
		
		errors.clear();
		
		this.checkEdges();
		this.checkNodes();
		
		return errors.size() == 0;
	}
	
	private void checkEdges() {	
		for(Edge edge : this.defs.getEdges()) {			

			if(edge.getSourceRef() == null) 
				this.addError(edge, NO_SOURCE);
			else if(edge.getTargetRef() == null)
				this.addError(edge, NO_TARGET);
			else if(edge instanceof SequenceFlow)
				if(edge.getSourceRef().getProcessRef() != edge.getTargetRef().getProcessRef()) 
					this.addError(edge, DIFFERENT_PROCESS);		
			else if(edge instanceof MessageFlow)
				//TODO: Add requirement for Messageflows between diferrent Pools
				System.out.println("Message Flows currently not Supported");
			/*
			 * In case this is not really checking the edge but the node.
			 * We do it here because its much easier than checking the node itself 
			 * for incoming or outgoing edges.
			 */
			else if(edge instanceof DataOutputAssociation || edge instanceof DataInputAssociation)  {
				System.out.println();
				if(edge.getTargetRef() instanceof DataInput)
					this.addError(edge.getTargetRef(), DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION);
				else if(edge.getSourceRef() instanceof DataOutput)
					this.addError(edge.getSourceRef(), DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION);
			}
			// TODO: EventSubProcesses can be checked the same way
			// 		 (They must not have any incoming or outgoing sequence flow)
			
		}
	}
	
	private void checkNodes() {		
		for(RootElement rootElement : this.defs.getRootElement()) 			
			if(rootElement instanceof Process) 		
				for(FlowElement flowElement : ((Process) rootElement).getFlowElement()) 				
					if(!this.isEdge(flowElement))
						this.checkNode(flowElement);		
	}
	
	private void checkNode(FlowElement node) {
		
//		this.checkForAllowedAndForbiddenNodes(node);
		
		if((node instanceof Activity || node instanceof Event || node instanceof Gateway) && node.getProcessRef() == null) {			
			this.addError(node, FLOWOBJECT_NOT_CONTAINED_IN_PROCESS);			
		}
		
		// Events
		if(node instanceof EndEvent && !this.hasIncomingControlFlow((FlowNode) node))
			this.addError(node, ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW);
		
		if(node instanceof EndEvent && this.hasOutgoingControlFlow((FlowNode) node))
			this.addError(node, ENDEVENT_WITH_OUTGOING_CONTROL_FLOW);
		
		if(node instanceof StartEvent && this.hasIncomingControlFlow((FlowNode) node))
			this.addError(node, STARTEVENT_WITH_INCOMING_CONTROL_FLOW);
		
		if(node instanceof StartEvent && !this.hasOutgoingControlFlow((FlowNode) node))
			this.addError(node, STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
		
		if(node instanceof BoundaryEvent)
			this.checkBoundaryEvent((BoundaryEvent) node);
				
		// Gateways
		// TODO: Add EventBasedGateway
		
		/*
		 * Looking for DataInputs and DataOutputs?
		 * Go to checkEdges :)
		 */
		
	}
	
	// TODO: Check if this Method and the invoked one are really necessary
//	private void checkForAllowedAndForbiddenNodes(FlowElement node) {
//		// Check for allowed and permitted nodes
//		if(!checkForAllowedNode(node, allowedNodes, true) || !checkForAllowedNode(node, forbiddenNodes, false)){
//			System.out.println("error");
//			addError(node, NODE_NOT_ALLOWED);
//		}
//	}
//	
//	private boolean checkForAllowedNode(FlowElement node, HashSet<String> classes, boolean allowed) {
//		// If checking for allowed classes, empty classes means all are allowed
//		if(allowed && classes.size() == 0)
//			return true;
//		
//		boolean containedInClasses = false;
//		String nodeClassName = node.getClass().getSimpleName();
//	
//		for(String clazz : classes){
//			//TODO this doesn't checks for superclasses!!!
//			// better would be "node instanceof Class.forName(clazz)" 
//			if(clazz.equals(nodeClassName)){
//				containedInClasses = true;
//			} else if(clazz.equals("MultipleInstanceActivity")){
//				containedInClasses = (node instanceof Activity) && ((Activity)node).isMultipleInstance();
//			}
//			
//			if(containedInClasses) break;
//		}
//		
//		return containedInClasses == allowed;
//	}
	
	private void checkBoundaryEvent(BoundaryEvent node) {
		
		if(this.hasIncomingControlFlow(node))
			this.addError(node, ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW);
		
		// TODO: Add Support for CompensationEvents
		if(node.getOutgoingSequenceFlows().size() != 1/* && !node.getEventDefinitionRef() instanceof CompensationEventDefinition*/)
			this.addError(node, ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
	}

	private boolean hasIncomingControlFlow(FlowNode node) {
		return node.getIncomingSequenceFlows().size() > 0;
	}
	
	private boolean hasOutgoingControlFlow(FlowNode node) {
		return node.getOutgoingSequenceFlows().size() > 0;
	}
	
	private boolean isEdge(FlowElement node) {
		if(node instanceof SequenceFlow || node instanceof MessageFlow)
			return true;
		
		return false;
	}
	
	protected void addError(FlowElement elem, String errorText) {
		this.errors.put(elem.getId(), errorText);
	}	
}
