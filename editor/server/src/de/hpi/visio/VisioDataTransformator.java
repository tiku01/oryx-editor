package de.hpi.visio;

import java.util.ArrayList;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.ShapeUtil;

public class VisioDataTransformator {
	
	private final StencilSet BPMN_STENCILS = new StencilSet(
			"/oryx//stencilsets/bpmn2.0/bpmn2.0.json",
			"http://b3mn.org/stencilset/bpmn2.0#");
	
	private ImportConfigurationUtil importUtil;
	private ShapeUtil shapeUtil;
	
	public VisioDataTransformator(String contextPath) {
		importUtil = new ImportConfigurationUtil(contextPath + "execution/");
		shapeUtil = new ShapeUtil(importUtil);
	}

	public Diagram createDiagramFromVisioData(Page visioPage) {
		VisioDataCleaner cleaner = new VisioDataCleaner(importUtil);
		Page cleanedVisioPage = cleaner.checkAndCleanVisioData(visioPage);
		HeuristicVisioInterpreter interpreter = new HeuristicVisioInterpreter(importUtil, shapeUtil);
		Page interpretedPage = interpreter.interpret(cleanedVisioPage);
		Diagram diagram = getNewBPMNDiagram();
		diagram.setBounds(shapeUtil.getCorrectedDiagramBounds(visioPage.getBounds()));
		ArrayList<org.oryxeditor.server.diagram.Shape> childShapes = getOryxChildShapesFromVisioData(interpretedPage);
		diagram.setChildShapes(childShapes);
		return diagram;
	}

	private ArrayList<org.oryxeditor.server.diagram.Shape> getOryxChildShapesFromVisioData(Page visioPage) {
		ArrayList<org.oryxeditor.server.diagram.Shape> childShapes = new ArrayList<org.oryxeditor.server.diagram.Shape>();
		for (Shape visioShape : visioPage.getShapes()) {
			String stencilId = importUtil.getStencilIdForName(visioShape.getName());
			if (stencilId == null) {
				// stencilId is required in oryx json
				continue;
			}
			StencilType type = new StencilType(stencilId);
			org.oryxeditor.server.diagram.Shape oryxShape = new org.oryxeditor.server.diagram.Shape("oryx-canvas123", type);
			Bounds correctedBounds = shapeUtil.getCorrectedShapeBounds(visioShape, visioPage);
			oryxShape.setBounds(correctedBounds);
			if (visioShape.getLabel() != null && !visioShape.getLabel().equals("")) {
				oryxShape.putProperty("name", visioShape.getLabel());
				oryxShape.putProperty("title", visioShape.getLabel());
				oryxShape.putProperty("text", visioShape.getLabel());
				// TODO: importConfigurationUtil --> define a mapping for label to property in json
			}
			childShapes.add(oryxShape);
		}
		return childShapes;
	}

	private Diagram getNewBPMNDiagram() {
		StencilType bpmnType = new StencilType("BPMNDiagram");
		Diagram diagram = new Diagram("oryx-canvas123", bpmnType, BPMN_STENCILS);
		diagram.putProperty("targetnamespace", "http://www.omg.org/bpmn20");
		diagram.putProperty("typelanguage","http://www.w3.org/2001/XMLSchema");
		return diagram;
	}
	
	

}
