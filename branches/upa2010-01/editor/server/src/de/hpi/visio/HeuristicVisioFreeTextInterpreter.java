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
import de.hpi.visio.util.VisioShapeDistanceUtil;
import java.util.Collections;

/**
 * HeuristicVisioFreeTextInterpreter interprets visio shapes, that do not have a
 * nameU (type) but a label and therefore are free-text that is on the visio
 * page and that must be handled to import this information to oryx. 
 * Depending on the label's length, the distance to the next shape and the shapes type
 * this label will be the nearest shape's label, an associated annotation or a
 * dangling annotation.
 * 
 * @author Thamsen
 */
public class HeuristicVisioFreeTextInterpreter {

	private ImportConfigurationUtil importUtil;
	private VisioShapeDistanceUtil shapeUtil;

	public HeuristicVisioFreeTextInterpreter(ImportConfigurationUtil importUtil, VisioShapeDistanceUtil shapeUtil) {
		this.importUtil = importUtil;
		this.shapeUtil = shapeUtil;
	}

	public Page interpretShapes(Page visioPage) {
		List<Shape> shapesWithNames = new ArrayList<Shape>();
		Boolean shouldSkipUnnamedWithLabel = Boolean.valueOf(importUtil.getHeuristic("skipUnknownNameUButWithLabel"));
		Boolean isInSimpleInterpretationMode = importUtil.getHeuristic("labelOnlyInterpretationMode").equalsIgnoreCase(
				"simple");
		String defaultTypeWithLabel = importUtil.getStencilSetConfig("unknownNameUButWithLabelType");
		for (Shape shape : visioPage.getShapes()) {
			if (shape.name == null || shape.name.equals("")) {
				if (shape.getLabel() != null && shape.getLabel() != "") {
					if (shouldSkipUnnamedWithLabel)
						continue;
					if (isInSimpleInterpretationMode) {
						shape.setName(defaultTypeWithLabel);

					} else {
						interpreteShapeWithoutNameButWithLabelHeuristic(shape, visioPage, shapesWithNames);
						continue;
					}
				}
			}
			shapesWithNames.add(shape);
		}
		visioPage.setShapes(shapesWithNames);
		return visioPage;
	}

	private void interpreteShapeWithoutNameButWithLabelHeuristic(Shape freeTextShape, Page visioPage, List<Shape> shapesWithNames) {
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
			for (int i = 0; !handled && i < labelThresholdShapeList.size() && i < considerationThreshold; i++) {
				if (labelThresholdShapeList.get(i).getKey().getLabel() == null || "".equals(labelThresholdShapeList.get(i).getKey().getLabel())) {
					labelThresholdShapeList.get(i).getKey().setLabel(freeTextShape.getLabel());
					handled = true;
					shapeUtil.getDistanceToShapeBorderFromPoint(labelThresholdShapeList.get(i).getKey(), freeTextShape.getCentralPin());
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
			centralPinY = association.getStartPoint().getY() + association.getHeight() / 2;
		} else {
			centralPinY = association.getEndPoint().getY() + association.getHeight() / 2;
		}
		if (association.getStartPoint().getX() > association.getEndPoint().getX()) {
			centralPinX = association.getStartPoint().getX() + association.getWidth() / 2;
		} else {
			centralPinX = association.getEndPoint().getX() + association.getWidth() / 2;
		}
		association.setCentralPin(new Point(centralPinX, centralPinY));
		return association;
	}

}
