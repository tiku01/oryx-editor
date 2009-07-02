package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class SequenceFlowTemplate extends ConnectorTemplate implements
		BPMN2ERDFTemplate {
	
	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new SequenceFlowTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		SequenceFlow e = (SequenceFlow) diagramObject;
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#SequenceFlow");
		appendOryxField(s,"conditionexpression",e.getConditionExpression());
		appendOryxField(s,"conditiontype",e.getConditionType().name().toUpperCase().substring(0,1)+e.getConditionType().name().toLowerCase().substring(1)); 
		appendStandardFields(s);
		appendDockerInformation(s, e);
		appendBounds(s, e);
		appendResourceEndPattern(s, e, context);
		return s;
	}

}
