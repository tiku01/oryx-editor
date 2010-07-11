package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.ShapeUtil;
import java.util.Collections;

public class HeuristicVisioInterpreter {
	
	private ImportConfigurationUtil importUtil;
	private ShapeUtil shapeUtil;

	public HeuristicVisioInterpreter(ImportConfigurationUtil importUtil, ShapeUtil shapeUtil) {
		this.importUtil = importUtil;
		this.shapeUtil = shapeUtil;
	}

	public Page interpret(Page visioPage) {
		Page page = removeInvalidShapeNames(visioPage);
		page = interpreteShapeNames(visioPage);
		page = interpreteTaskBounds(page);
		List<Shape> correctedChildShapes = correctAllEdges(page.getShapes());
		page.setShapes(correctedChildShapes);
		return page;
	}

	private Page removeInvalidShapeNames(Page visioPage) {
		List<Shape> shapesWithValidNames = new ArrayList<Shape>();
		for (Shape shape : visioPage.getShapes()) {
			if ("NOT_VALID_SHAPE_NAME".equalsIgnoreCase(shape.getName()))
				shape.setName("");
			shapesWithValidNames.add(shape);
		}
		visioPage.setShapes(shapesWithValidNames);
		return visioPage;
	}

	private List<Shape> correctAllEdges(List<Shape> shapes) {
		List<Shape> edges = getAllEdges(shapes);
		for (Shape edge : edges) {
			Shape source = shapeUtil.getSmallestShapeToPointWithinThreshold(edge, edge.getStartPoint(), shapes);
			Shape target = shapeUtil.getSmallestShapeToPointWithinThreshold(edge, edge.getEndPoint(), shapes);
			if (source != null) {
				edge.setSource(source);
				source.addOutgoing(edge);
				edge.addIncoming(edge);
			} 
			if (target != null) {
				target.addIncoming(edge);
				edge.addOutgoing(target);
				edge.setTarget(target);
			}
				
		}
		return shapes;
	}

	private List<Shape> getAllEdges(List<Shape> shapes) {
		String[] edgeStencils = importUtil.getOryxBPMNConfig("Edges").split(",");
		List<Shape> edges = new ArrayList<Shape>();
		for (Shape shape : shapes) {
			for (String edgeStencil : edgeStencils) {
				if (edgeStencil.equalsIgnoreCase(importUtil.getStencilIdForName(shape.getName())))
					edges.add(shape);
			}
		}
		return edges;
	}
	
	private Page interpreteShapeNames(Page visioPage) {
		List<Shape> shapesWithNames = new ArrayList<Shape>();
		Boolean shouldSkipUnnamedWithLabel = Boolean.valueOf(importUtil.getHeuristicValue("skipUnknownNameUButWithLabel"));
		Boolean shouldSkipUnnamedWithoutLabel = Boolean.valueOf(importUtil.getHeuristicValue("skipUnknownNameUAndWithoutLabel"));
		Boolean isInSimpleInterpretationMode = importUtil.getHeuristicValue("labelOnlyInterpretationMode").equalsIgnoreCase("simple");
		String defaultTypeWithLabel = importUtil.getHeuristicValue("unknownNameUButWithLabelType");
		String defaultTypeWithoutLabel = importUtil.getHeuristicValue("unknownNameUAndWithoutLabelType");
		for (Shape shape : visioPage.getShapes()) {
			if (shape.name == null || shape.name.equals("")) {
				if (shape.getLabel() != null && shape.getLabel() != "") {
					if (shouldSkipUnnamedWithLabel) 
						continue;
					if (isInSimpleInterpretationMode) {
						shape.setName(defaultTypeWithLabel);
						
					} else {
						interpreteShapeWithoutNameButWithLabelHeuristic(shape,visioPage,shapesWithNames);
						continue;
					}
				} else {
					if (shouldSkipUnnamedWithoutLabel) 
						continue;
					shape.setName(defaultTypeWithoutLabel);
				}
			}
			shapesWithNames.add(shape);
		}
		visioPage.setShapes(shapesWithNames);
		return visioPage;
	}
	
	private void interpreteShapeWithoutNameButWithLabelHeuristic(Shape shape,Page visioPage, List<Shape> shapesWithNames) {
		Double isLabelThreshold = Double.valueOf(importUtil.getHeuristicValue("labelOnlyIsLabelForAnotherShapeThreshold"));
		Double isAnnotationThreshold = Double.valueOf(importUtil.getHeuristicValue("labelOnlyIsAnnotationToAnotherShapeThreshold"));
		String annotationType = importUtil.getHeuristicValue("labelOnlyAnnotationType");
		List<Shape> labelThresholdShapes = new ArrayList<Shape>();
		List<Shape> annotationThresholdShapes = new ArrayList<Shape>();
		for (Shape otherShape : visioPage.getShapes()) {
			if (shape == otherShape) 
				continue;
			Double currentDistance = shapeUtil.getAroundMinimalDistanceBetweenTwoShapeBorders(shape, otherShape);
			if (currentDistance < isLabelThreshold)
				labelThresholdShapes.add(otherShape);
			if (currentDistance < isAnnotationThreshold)
				annotationThresholdShapes.add(otherShape);
		}
		Boolean handled = false;
		Integer stringLengthThresholdForLabels = Integer.valueOf(importUtil.getHeuristicValue("labelOnlyTooLongForAnotherShapesLabelThreshold"));
		if (labelThresholdShapes.size() > 0 && shape.getLabel().length() < stringLengthThresholdForLabels) {
			Collections.sort(labelThresholdShapes);
			if (labelThresholdShapes.get(0).getLabel() == null || "".equals(labelThresholdShapes.get(0).getLabel())) {
				labelThresholdShapes.get(0).setLabel(shape.getLabel());
				handled = true;
			} 
		} 
		if (!handled) {
			shape.setName(annotationType);
			shapesWithNames.add(shape);
			if (annotationThresholdShapes.size() > 0) {
				Collections.sort(annotationThresholdShapes);
				Shape association = createAssociationBetween(annotationThresholdShapes.get(0),shape);
				shapesWithNames.add(association);
			}
		}
	}

	private Shape createAssociationBetween(Shape shape, Shape annotation) {
		String associationType = importUtil.getHeuristicValue("labelOnlyAnnotationAssociation");
		Shape association = new Shape();
		association.setName(associationType);
		association.setStartPoint(shape.getCentralPin());
		association.setEndPoint(annotation.getCentralPin());
		association.setHeight(Math.abs(association.getStartPoint().getY() - association.getEndPoint().getY()));
		association.setWidth(Math.abs(association.getStartPoint().getX() - association.getEndPoint().getX()));
		Double centralPinX;
		Double centralPinY;
		if (association.getStartPoint().getY() > association.getEndPoint().getY()) {
			centralPinY = association.getStartPoint().getY() + association.getHeight()/2;
		} else {
			centralPinY = association.getEndPoint().getY() + association.getHeight()/2;
		}
		if (association.getStartPoint().getX() > association.getEndPoint().getX()) {
			centralPinX = association.getStartPoint().getX() + association.getWidth()/2;
		} else {
			centralPinX = association.getEndPoint().getX() + association.getWidth()/2;
		}
		association.setCentralPin(new Point(centralPinX, centralPinY));
		return association;
	}

	private Page interpreteTaskBounds(Page page) {
		Boolean hugeTasksAreSubprocesses = Boolean.valueOf(importUtil.getHeuristicValue("interpreteHugeTasksAsSubprocesses"));
		if (hugeTasksAreSubprocesses) {
			List<Shape> tasks = getAllTaskShapes(page);
			for (Shape task : tasks) {
				if (taskIsAsHugeAsASubprocess(task)) 
					task.setName("Subprocess");
			}
		}
		return page;
	}

	private List<Shape> getAllTaskShapes(Page page) {
		List<Shape> tasks = new ArrayList<Shape>();
		for (Shape shape : page.getShapes()) {
			if ("Task".equals(importUtil.getStencilIdForName(shape.getName())))
				tasks.add(shape);
		}
		return tasks;
	}
	
	private boolean taskIsAsHugeAsASubprocess(Shape task) {
		Double widthThreshold = Double.valueOf(importUtil.getHeuristicValue("taskToSubprocessThresholdWidth"));
		Double heightThreshold = Double.valueOf(importUtil.getHeuristicValue("taskToSubprocessThresholdHeight"));
		if (task.getWidth() >= widthThreshold || task.getHeight() >= heightThreshold) {
			return true;
		} else {
			return false;
		}
	}

}
