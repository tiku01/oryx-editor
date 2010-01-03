package de.hpi.bpmn2yawl;

import java.util.Vector;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.SequenceFlow.ConditionType;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;

/**
 * @author Armin Zamani
 */

public class BPMN2YAWLSyntaxChecker extends BPMNSyntaxChecker{

	private static final String COMPLEXGATEWAY_NOT_SUPPORTED = "Complex gateways are not supported in the mapping";
	private static final String ADHOCSUBPROCESS_NOT_SUPPORTED = "Adhoc subprocesses are not supported in the mapping";
	private static final String GATEWAY_WITHOUT_DEFAULTFLOW = "XORDataBasedGateways and ORGateways must have one outgoing default flow.";
	private static final String EXPRESSION_MISSING = "An expression is missing for this flow.";
	private static final String NOEXPRESSION_NOT_SUPPORTED = "Outgoing flows of XORDataBasedGateways and ORGateways must either have an expression or be a default flow.";
	private static final String MORE_THAN_TWO_DEFAULTFLOWS_PER_GATEWAY_NOT_SUPPORTED = "XORDataBasedGateways and ORGateways must have only one outgoing default flow.";
	
	public BPMN2YAWLSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
		
		forbiddenNodes.add("ComplexGateway");
		forbiddenNodes.add("StartMessageEvent");
		forbiddenNodes.add("StartConditionalEvent");
		forbiddenNodes.add("StartSignalEvent");
		forbiddenNodes.add("StartMultipleEvent");
		forbiddenNodes.add("IntermediateCancelEvent");
		forbiddenNodes.add("IntermediateCompensationEvent");
		forbiddenNodes.add("IntermediateConditionalEvent");
		forbiddenNodes.add("IntermediateSignalEvent");
		forbiddenNodes.add("IntermediateMultipleEvent");
		forbiddenNodes.add("IntermediateLinkEvent");
		forbiddenNodes.add("EndCompensationEvent");
		forbiddenNodes.add("EndSignalEvent");
		forbiddenNodes.add("EndMultipleEvent");
	}
	
	@Override
	protected boolean checkNode(Node node) {
		boolean isOk = super.checkNode(node);
		
		if (node instanceof ComplexGateway)
			isOk &= handleComplexGateway(node);
		else if(node instanceof SubProcess)
			isOk &= handleSubProcess(node);
		else if((node instanceof ORGateway) || (node instanceof XORDataBasedGateway))
			isOk &= handleGateway(node);
		else if (node instanceof Lane)
			isOk &= handleLane((Lane)node);
		
		return isOk;
	}

	/**
	 * @param node
	 * @param isOk
	 * @return
	 */
	private boolean handleGateway(Node node) {
		boolean isOk = true;
		if(node.getOutgoingSequenceFlows().size() > 1){
			int numberOfOutgoingDefaultFlows = 0;
			for(SequenceFlow sequenceFlow : node.getOutgoingSequenceFlows()){
				if(sequenceFlow.getConditionType() == ConditionType.DEFAULT)
					numberOfOutgoingDefaultFlows++;
				else if(sequenceFlow.getConditionType() == ConditionType.NONE)
					isOk &= handleSequenceFlowConditionTypeNone(sequenceFlow);
				else
					isOk &= handleSequenceFlowConditionTypeExpression(sequenceFlow);
			}
			
			if(numberOfOutgoingDefaultFlows == 0){
				isOk = false;
				addError(node, GATEWAY_WITHOUT_DEFAULTFLOW);
			}else if(numberOfOutgoingDefaultFlows > 1){
				isOk = false;
				addError(node, MORE_THAN_TWO_DEFAULTFLOWS_PER_GATEWAY_NOT_SUPPORTED);
			}
		}
		return isOk;
	}

	/**
	 * @param isOk
	 * @param sequenceFlow
	 * @return
	 */
	private boolean handleSequenceFlowConditionTypeExpression(SequenceFlow sequenceFlow) {
		boolean isOk = true;
		if(sequenceFlow.getConditionExpression().isEmpty()){
			isOk = false;
			addError(sequenceFlow, EXPRESSION_MISSING);
		}
		return isOk;
	}

	/**
	 * @param isOk
	 * @param sequenceFlow
	 * @return
	 */
	private boolean handleSequenceFlowConditionTypeNone(SequenceFlow sequenceFlow) {
		boolean isOk = true;
		if(sequenceFlow.getConditionExpression().isEmpty()){
			isOk = false;
			addError(sequenceFlow, NOEXPRESSION_NOT_SUPPORTED);
		}else
			sequenceFlow.setConditionType(ConditionType.EXPRESSION);
		
		return isOk;
	}

	/**
	 * @param node
	 * @param isOk
	 * @return
	 */
	private boolean handleSubProcess(Node node) {
		boolean isOk = true;
		SubProcess subprocess = (SubProcess)node;
		if(subprocess.isAdhoc())
		{
			isOk = false;
			addError(node, ADHOCSUBPROCESS_NOT_SUPPORTED);
		}
		return isOk;
	}

	/**
	 * @param node
	 * @return
	 */
	private boolean handleComplexGateway(Node node) {
		addError(node, COMPLEXGATEWAY_NOT_SUPPORTED);
		return false;
	}
	
	public boolean checkForNonEmptyTasks(BPMNDiagram diagram){
		boolean isOk = true;
		Vector<StartEvent> startEvents = new Vector<StartEvent>();
		
		getStartEventsFromDiagram(diagram, startEvents);
		isOk &= checkStartEvents(startEvents);
		
		return isOk;
	}

	/**
	 * @param isOk
	 * @param startEvents
	 * @return
	 */
	private boolean checkStartEvents(Vector<StartEvent> startEvents) {
		boolean isOk = true;
		for(StartEvent start : startEvents){
			if(start.getOutgoingSequenceFlows().size() > 1){
				for (SequenceFlow flow : start.getOutgoingSequenceFlows()){
					Node node = (Node)flow.getTarget();
					isOk &= checkNodeFollowingStartEvent(node);
				}
			}
		}
		return isOk;
	}

	/**
	 * @param isOk
	 * @param node
	 * @return
	 */
	private boolean checkNodeFollowingStartEvent(Node node) {
		boolean isOk = true;
		if(!((node instanceof Activity) | (node instanceof IntermediateMessageEvent) | (node instanceof IntermediateTimerEvent))){
			isOk = false;
			addError(node, "Nodes that follow start events have to be instances of Activities, Intermediate Message Events or Intermediate Timer Events only");
		}
		return isOk;
	}

	/**
	 * @param diagram
	 * @param startEvents
	 */
	private void getStartEventsFromDiagram(BPMNDiagram diagram, Vector<StartEvent> startEvents) {
		for (Container process : diagram.getProcesses()) {
			for (Node node : process.getChildNodes()) {
				if (node instanceof StartEvent)
					startEvents.add((StartEvent) node);
			}
		}
	}
	
	public boolean handleLane(Lane lane){
		boolean isOk = true;
		if (lane.getResourcingType() == null | lane.getResourcingType().isEmpty()){
			isOk = false;
			addError(lane, "Please load the YAWL stencilset extension and choose a resourcing type for this lane.");
		}
		
		return isOk;
	}
}