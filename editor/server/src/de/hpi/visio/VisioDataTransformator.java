package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.data.VisioDocument;
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

	public Diagram createDiagramFromVisioData(VisioDocument visioData) {
		VisioPageMerger merger = new VisioPageMerger();
		visioData = merger.mergeAllPages(visioData);
		VisioDataCleaner cleaner = new VisioDataCleaner(importUtil, shapeUtil);
		Page cleanedVisioPage = cleaner.checkAndCleanVisioData(visioData);
		HeuristicVisioInterpreter visioInterpreter = new HeuristicVisioInterpreter(importUtil, shapeUtil);
		Page interpretedPage = visioInterpreter.interpret(cleanedVisioPage);
		interpretedPage.setShapes(removeShapesWithoutStencilId(interpretedPage.getShapes()));
		assignShapeIds(interpretedPage.getShapes());
		Diagram diagram = getNewBPMNDiagram();
		diagram.setBounds(shapeUtil.correctPointsOfBounds(interpretedPage.getBounds()));
		ArrayList<org.oryxeditor.server.diagram.Shape> childShapes = getOryxChildShapesFromVisioData(interpretedPage);
		diagram.setChildShapes(childShapes);
		HeuristicOryxInterpreter oryxInterpreter = new HeuristicOryxInterpreter(importUtil);
		diagram = oryxInterpreter.interpret(diagram); 
		return diagram;
	}

	private ArrayList<org.oryxeditor.server.diagram.Shape> getOryxChildShapesFromVisioData(Page visioPage) {
		ArrayList<org.oryxeditor.server.diagram.Shape> childShapes = new ArrayList<org.oryxeditor.server.diagram.Shape>();
		for (Shape visioShape : visioPage.getShapes()) {
			String stencilId = importUtil.getStencilIdForName(visioShape.getName());
			StencilType type = new StencilType(stencilId);
			org.oryxeditor.server.diagram.Shape oryxShape = new org.oryxeditor.server.diagram.Shape(visioShape.getShapeId(), type);
			Bounds correctedBounds = shapeUtil.getCorrectedShapeBounds(visioShape, visioPage);
			oryxShape.setBounds(correctedBounds);
			ArrayList<Point> dockers = shapeUtil.getCorrectedDockersForShape(visioShape, visioPage);
			oryxShape.setDockers(dockers);
			if (visioShape.getLabel() != null && !visioShape.getLabel().equals("")) {
				String[] labelPropertyNameExceptions = importUtil.getOryxBPMNConfig("LabelPropertyException").split(",");
				for (String exception : labelPropertyNameExceptions) {
					if (exception.equalsIgnoreCase(oryxShape.getStencilId())) 
						oryxShape.putProperty(importUtil.getOryxBPMNConfig(exception + ".LabelProperty"), visioShape.getLabel());
				}
				if (oryxShape.getProperties().size() == 0) {
					oryxShape.putProperty(importUtil.getOryxBPMNConfig("DefaultLabelProperty"), visioShape.getLabel());
				}
			}
			for (String propertyKey : visioShape.getProperties().keySet()) {
				oryxShape.putProperty(propertyKey, visioShape.getPropertyValueByKey(propertyKey));
			}
			childShapes.add(oryxShape);
		}
		correctFlowOfShapes(childShapes, visioPage);
		return childShapes;
	}

	private Diagram getNewBPMNDiagram() {
		StencilType bpmnType = new StencilType("BPMNDiagram");
		Diagram diagram = new Diagram("oryx-canvas123", bpmnType, BPMN_STENCILS);
		diagram.putProperty("targetnamespace", "http://www.omg.org/bpmn20");
		diagram.putProperty("typelanguage","http://www.w3.org/2001/XMLSchema");
		return diagram;
	}
	
	private List<Shape> removeShapesWithoutStencilId(List<Shape> shapes) {
		List<Shape> shapesWithId = new ArrayList<Shape>();
		for (Shape shape : shapes) {
			String stencilID = importUtil.getStencilIdForName(shape.getName());
			if (stencilID == null || "".equals(stencilID)) {
				// stencilId is required in oryx json
				continue;
			}
		}
		return shapesWithId;
	}
	
	private void assignShapeIds(List<Shape> shapes) {
		String shapeString = "bpmnvisio_";
		Integer i = 0;
		for (Shape shape : shapes) {
			shape.setShapeId(shapeString + i.toString());
			i += 1;
		}
	}
	
	private void correctFlowOfShapes(ArrayList<org.oryxeditor.server.diagram.Shape> childShapes, Page visioPage) {
		for (Shape visioShape : visioPage.getShapes()) {
			if (visioShape.getOutgoings().size() > 0) {
				org.oryxeditor.server.diagram.Shape oryxShape = getOryxShapeById(childShapes, visioShape.getShapeId());
				for (Shape outgoing : visioShape.getOutgoings()) {
					oryxShape.addOutgoing(getOryxShapeById(childShapes, outgoing.getShapeId()));
				}
			}
			if (visioShape.getIncomings().size() > 0) {
				org.oryxeditor.server.diagram.Shape oryxShape = getOryxShapeById(childShapes, visioShape.getShapeId());
				for (Shape incoming : visioShape.getIncomings()) {
					oryxShape.addIncoming(getOryxShapeById(childShapes, incoming.getShapeId()));
				}
			}
			if (visioShape.getTarget() != null) {
				org.oryxeditor.server.diagram.Shape oryxShape = getOryxShapeById(childShapes, visioShape.getShapeId());
				oryxShape.setTarget(getOryxShapeById(childShapes, visioShape.getTarget().getShapeId()));
			}
		}
	}

	private org.oryxeditor.server.diagram.Shape getOryxShapeById(ArrayList<org.oryxeditor.server.diagram.Shape> childShapes, String shapeId) {
		for (org.oryxeditor.server.diagram.Shape oryxShape : childShapes) {
			if (oryxShape.getResourceId().equals(shapeId))
				return oryxShape;
		}
		return null;
	}
	

}
