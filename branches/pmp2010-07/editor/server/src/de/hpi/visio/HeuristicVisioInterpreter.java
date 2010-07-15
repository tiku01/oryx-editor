package de.hpi.visio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.DistanceToShapeComparator;
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
		List<Shape> correctedChildShapes = assignAllEdges(page);
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

	private List<Shape> assignAllEdges(Page page) {
		List<Shape> shapes = page.getShapes();
		List<Shape> edges = getAllEdges(shapes);
		for (Shape edge : edges) {
			Shape source = shapeUtil.getNearestPreferedShapeToPointWithinThreshold(edge, edge.getStartPoint(), shapes);
			Shape target = shapeUtil.getNearestPreferedShapeToPointWithinThreshold(edge, edge.getEndPoint(), shapes);
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
		String[] edgeStencils = importUtil.getStencilSetConfig("Edges").split(",");
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
		Boolean shouldSkipUnnamedWithLabel = Boolean.valueOf(importUtil.getHeuristic("skipUnknownNameUButWithLabel"));
		Boolean shouldSkipUnnamedWithoutLabel = Boolean.valueOf(importUtil.getHeuristic("skipUnknownNameUAndWithoutLabel"));
		Boolean isInSimpleInterpretationMode = importUtil.getHeuristic("labelOnlyInterpretationMode").equalsIgnoreCase("simple");
		String defaultTypeWithLabel = importUtil.getStencilSetConfig("unknownNameUButWithLabelType");
		String defaultTypeWithoutLabel = importUtil.getStencilSetConfig("unknownNameUAndWithoutLabelType");
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
	
	private void interpreteShapeWithoutNameButWithLabelHeuristic(Shape freeTextShape,Page visioPage, List<Shape> shapesWithNames) {
		Double isLabelThreshold = Double.valueOf(importUtil.getHeuristic("labelOnlyIsLabelForAnotherShapeThreshold"));
		Double isAnnotationThreshold = Double.valueOf(importUtil.getHeuristic("labelOnlyIsAnnotationToAnotherShapeThreshold"));
		String annotationType = importUtil.getStencilSetConfig("labelOnlyAnnotationType");
		Map<Shape, Double> labelThresholdShapes = new HashMap<Shape, Double>();
		Map<Shape, Double> annotationThresholdShapes = new HashMap<Shape, Double>();
		for (Shape otherShape : getNotExcludedShapesWithType(visioPage)) {
			if (freeTextShape == otherShape) 
				continue;
			Double currentDistance = shapeUtil.getAroundMinimalDistanceBetweenTwoShapeBorders(freeTextShape, otherShape);
			if (currentDistance < isLabelThreshold)
				labelThresholdShapes.put(otherShape, currentDistance);
			if (currentDistance < isAnnotationThreshold)
				annotationThresholdShapes.put(otherShape, currentDistance);
		}
		List<Map.Entry<Shape, Double>> labelThresholdShapeList = new ArrayList<Map.Entry<Shape, Double>>(labelThresholdShapes.entrySet());
		List<Map.Entry<Shape, Double>> annotationThresholdShapeList = new ArrayList<Map.Entry<Shape, Double>>(annotationThresholdShapes.entrySet());
		Integer stringLengthThresholdForLabels = Integer.valueOf(importUtil.getHeuristic("labelOnlyTooLongForAnotherShapesLabelThreshold"));
		Collections.sort(labelThresholdShapeList, new DistanceToShapeComparator());
		Integer considerationThreshold = Integer.valueOf(importUtil.getHeuristic("considerXShapesWithinLabelThresholdWithXIs"));
		Boolean handled = false;
		if (freeTextShape.getLabel().length() < stringLengthThresholdForLabels) {
			for (int i=0;!handled && i < labelThresholdShapeList.size() && i < considerationThreshold;i++) {
				if (labelThresholdShapeList.get(i).getKey().getLabel() == null || "".equals(labelThresholdShapeList.get(i).getKey().getLabel())) {
					labelThresholdShapeList.get(i).getKey().setLabel(freeTextShape.getLabel());
					handled = true;
					shapeUtil.getDistanceToShapeBorderFromPoint(labelThresholdShapeList.get(4).getKey(), freeTextShape.getCentralPin());
				} 
			} 
		}
		if (!handled) {
			freeTextShape.setName(annotationType);
			shapesWithNames.add(freeTextShape);
			if (annotationThresholdShapes.size() > 0) {
				Collections.sort(annotationThresholdShapeList, new DistanceToShapeComparator());
				Shape association = createAssociationBetween(annotationThresholdShapeList.get(0).getKey(),freeTextShape);
				shapesWithNames.add(association);
			}
		}
	}


	private List<Shape> getNotExcludedShapesWithType(Page visioPage) {
		List<Shape> shapesWithType = new ArrayList<Shape>();
		String excludedStencilsString = importUtil.getStencilSetConfig("FromFreeTextInterpretationExcludedStencils");
		String[] excludedStencils = excludedStencilsString.split(",");
		for (Shape shape : visioPage.getShapes()) {
			String configuredStencilId = importUtil.getStencilIdForName(shape.getName());
			if (configuredStencilId == null || "".equals(configuredStencilId))
				continue;
			Boolean isExcluded = false;
			if (excludedStencilsString != null && !"".equals(excludedStencilsString)) {
				for (String excludedStencil : excludedStencils) {
					if (excludedStencil.equalsIgnoreCase(configuredStencilId))
						isExcluded = true;
				}
			}
			if (!isExcluded) {
				shapesWithType.add(shape);
			}
		}
		
		return shapesWithType;
	}
	
	private Shape createAssociationBetween(Shape shape, Shape annotation) {
		String associationType = importUtil.getStencilSetConfig("labelOnlyAnnotationAssociation");
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
		Boolean hugeTasksAreSubprocesses = Boolean.valueOf(importUtil.getStencilSetHeuristic("interpreteHugeTasksAsSubprocesses"));
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
		Double widthThreshold = Double.valueOf(importUtil.getStencilSetHeuristic("taskToSubprocessThresholdWidth"));
		Double heightThreshold = Double.valueOf(importUtil.getStencilSetHeuristic("taskToSubprocessThresholdHeight"));
		if (task.getWidth() >= widthThreshold || task.getHeight() >= heightThreshold) {
			return true;
		} else {
			return false;
		}
	}

}
