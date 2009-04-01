package de.hpi.bpmn2yawl;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;

/**
 * @author Armin Zamani
 */

public class BPMN2YAWLSyntaxChecker extends BPMNSyntaxChecker{
	
	private static final String COMPLEXGATEWAY_NOT_SUPPORTED = "Complex gateways are not supported in the mapping";
	private static final String DATAOBJECTS_NOT_SUPPORTED = "Data objects are not supported in the mapping";
	private static final String ADHOCSUBPROCESS_NOT_SUPPORTED = "Adhoc subprocesses are not supported in the mapping";
	
	public BPMN2YAWLSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
	}
	
	@Override
	protected boolean checkNode(Node node) {
		boolean isOk = super.checkNode(node);
		
		if (node instanceof ComplexGateway) {
			addError(node, COMPLEXGATEWAY_NOT_SUPPORTED);
		}else if(node instanceof DataObject){
			addError(node, DATAOBJECTS_NOT_SUPPORTED);
		}
		
		if(node instanceof SubProcess){
			SubProcess subprocess = (SubProcess)node;
			if(subprocess.isAdhoc()){
				addError(node, ADHOCSUBPROCESS_NOT_SUPPORTED);
			}
		}
		return true;
	}
}