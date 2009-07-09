package de.hpi.bpmn2yawl;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.SequenceFlow.ConditionType;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;

/**
 * @author Armin Zamani
 */

public class BPMN2YAWLSyntaxChecker extends BPMNSyntaxChecker{

	private static final String COMPLEXGATEWAY_NOT_SUPPORTED = "Complex gateways are not supported in the mapping";
	private static final String DATAOBJECTS_NOT_SUPPORTED = "Data objects are not supported in the mapping";
	private static final String ADHOCSUBPROCESS_NOT_SUPPORTED = "Adhoc subprocesses are not supported in the mapping";
	private static final String GATEWAY_WITHOUT_DEFAULTFLOW = "XORDataBasedGateways and ORGateways must have one outgoing default flow.";
	private static final String EXPRESSION_MISSING = "An expression is missing for this flow.";
	private static final String NOEXPRESSION_NOT_SUPPORTED = "Outgoing flows of XORDataBasedGateways and ORGateways must either have an expression or be a default flow.";
	private static final String MORE_THAN_TWO_DEFAULTFLOWS_PER_GATEWAY_NOT_SUPPORTED = "XORDataBasedGateways and ORGateways must have only one outgoing default flow.";
	
	public BPMN2YAWLSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
		
		forbiddenNodes.add("ComplexGateway");
		forbiddenNodes.add("DataObject");
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
			addError(node, COMPLEXGATEWAY_NOT_SUPPORTED);
		else if(node instanceof DataObject)
			addError(node, DATAOBJECTS_NOT_SUPPORTED);
		
		if(node instanceof SubProcess){
			SubProcess subprocess = (SubProcess)node;
			if(subprocess.isAdhoc())
				addError(node, ADHOCSUBPROCESS_NOT_SUPPORTED);
		}
		
		if((node instanceof ORGateway) || (node instanceof XORDataBasedGateway)){
			if(node.getOutgoingSequenceFlows().size() > 1){
				int numberOfOutgoingDefaultFlows = 0;
				for(SequenceFlow sequenceFlow : node.getOutgoingSequenceFlows()){
					if(sequenceFlow.getConditionType() == ConditionType.DEFAULT)
						numberOfOutgoingDefaultFlows++;
					else if(sequenceFlow.getConditionType() == ConditionType.NONE){
						if(sequenceFlow.getConditionExpression().isEmpty())
							addError(sequenceFlow, NOEXPRESSION_NOT_SUPPORTED);
						else
							sequenceFlow.setConditionType(ConditionType.EXPRESSION);
					}else{
						if(sequenceFlow.getConditionExpression().isEmpty())
							addError(sequenceFlow, EXPRESSION_MISSING);
					}
				}
				if(numberOfOutgoingDefaultFlows == 0)
					addError(node, GATEWAY_WITHOUT_DEFAULTFLOW);
				else if(numberOfOutgoingDefaultFlows > 1)
					addError(node, MORE_THAN_TWO_DEFAULTFLOWS_PER_GATEWAY_NOT_SUPPORTED);
			}
		}
		return true;
	}
}