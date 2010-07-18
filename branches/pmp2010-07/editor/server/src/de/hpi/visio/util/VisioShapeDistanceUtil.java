package de.hpi.visio.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Shape;
import java.util.Collections;

/**
 * Utility class to interpret visio shapes for oryx-shape-containment.
 * 
 * @author Thamsen
 */
public class VisioShapeDistanceUtil {

	private ImportConfigurationUtil importUtil;
	private Double maxDistanceThresholdInVisioUnit;

	public VisioShapeDistanceUtil(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
		maxDistanceThresholdInVisioUnit = Double.valueOf(importUtil.getHeuristic("maxEdgeToShapeDistance"));
	}

	/**
	 * For a given oryx stencil id and a list of visio shapes and one specific
	 * shape, this will return the first shape that's under the specific shape.
	 */
	public Shape getFirstShapeOfStencilThatContainsTheGivenShape(List<Shape> shapes, Shape givenShape, String stencilId) {
		for (Shape currentShape : shapes) {
			if (isFirstShapeOnSecondShape(givenShape, currentShape) && givenShape != currentShape) {
				if (currentShape.getName() == null)
					continue;
				if (stencilId.equals(importUtil.getStencilIdForName(currentShape.getName()))) {
					return currentShape;
				}
			}
		}
		return null;
	}

	public Shape getFirstShapeThatContainsTheGivenShape(List<Shape> shapes, Shape givenShape) {
		for (Shape currentShape : shapes) {
			if (isFirstShapeOnSecondShape(givenShape, currentShape) && givenShape != currentShape)
				return currentShape;
		}
		return null;
	}

	private Boolean isFirstShapeOnSecondShape(Shape firstShape, Shape secondShape) {
		Boolean result = isAPointInsideBounds(firstShape.getCentralPin(), secondShape.getVisioBounds());
		return result;
	}

	private Boolean isAPointInsideBounds(Point point, Bounds bounds) {
		if (point.getX() >= bounds.getUpperLeft().getX() && point.getX() <= bounds.getLowerRight().getX()) {
			if (point.getY() >= bounds.getLowerRight().getY() && point.getY() <= bounds.getUpperLeft().getY()) {
				return true;
			}
		}
		return false;
	}

	public Shape getNearestPreferedShapeToPointWithinThreshold(Shape self, Point point, List<Shape> shapes) {
		// anti prefered stencils: e.g. pools, because the distance is nearly
		// always 0.0,
		// so tasks should be preferred when distance within also in threshold
		// Better results than to take the smallest shape, because edges always
		// win in that comparison
		String[] antiPreferedStencils = importUtil.getStencilSetConfig("antiPreferForEdgeAssignment").split(",");
		if (shapes.size() > 0) {
			Map<Shape, Double> shapesWithinThresholdMap = new HashMap<Shape, Double>();
			for (Shape shape : shapes) {
				if (shape == self)
					continue;
				Double distance = getDistanceToShapeBorderFromPoint(shape, point);
				if (distance < maxDistanceThresholdInVisioUnit) {
					shapesWithinThresholdMap.put(shape, distance);
				}
			}
			if (shapesWithinThresholdMap.size() > 0) {
				List<Map.Entry<Shape, Double>> shapesWithinThresholdList = new ArrayList<Map.Entry<Shape, Double>>(
						shapesWithinThresholdMap.entrySet());
				Collections.sort(shapesWithinThresholdList, new DistanceToShapeComparator());
				int i = 0;
				while (i < shapesWithinThresholdMap.size()) {
					Shape shape = shapesWithinThresholdList.get(i).getKey();
					Boolean isAntiPrefered = false;
					for (String antiPreferedStencil : antiPreferedStencils) {
						if (antiPreferedStencil.equals(importUtil.getStencilIdForName(shape.getName())))
							isAntiPrefered = true;
					}
					if (!isAntiPrefered)
						return shape;
					i++;
				}
				// all stencils anti-prefered or anti-preferation undefined
				Shape nearestShape = shapesWithinThresholdList.get(0).getKey();
				return nearestShape;
			}
		}
		return null;
	}
	
	/*	Getting of a point to a shape's nearest border
	 * 	All 8 distance calculation cases are: 
	 * 
	 * 		8		2		5
	 * 			----------
	 * 		7	|	1	 |	4
	 * 			----------
	 * 		9		3		6
	 */
	public Double getAroundMinimalDistanceBetweenTwoShapeBorders(Shape shape1, Shape shape2) {
		List<Double> distancesFromCorners = new ArrayList<Double>();
		
		// 1 
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, shape1.getCentralPin()));
		// 2
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX(), shape1.getCentralPin().getY() + shape1.getHeight()/2)));
		// 3
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX(), shape1.getCentralPin().getY() - shape1.getHeight()/2)));
		// 4
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX() + shape1.getWidth()/2, shape1.getCentralPin().getY())));
		// 5
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX() + shape1.getWidth()/2, shape1.getCentralPin().getY() + shape1.getHeight()/2)));
		// 6
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX() + shape1.getWidth()/2, shape1.getCentralPin().getY() - shape1.getHeight()/2)));
		// 7
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX() - shape1.getWidth()/2, shape1.getCentralPin().getY())));
		// 8
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX() - shape1.getWidth()/2, shape1.getCentralPin().getY() + shape1.getHeight()/2)));
		// 9
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, 
				new Point(shape1.getCentralPin().getX() - shape1.getWidth()/2, shape1.getCentralPin().getY() - shape1.getHeight()/2)));
		
		Collections.sort(distancesFromCorners);
		return distancesFromCorners.get(0);
	}
	
	/*	Getting of a point to a shape's nearest border
	 * 	All 8 distance calculation cases are: 
	 * 
	 * 		8		2		5
	 * 			----------
	 * 		7	|	1	 |	4
	 * 			----------
	 * 		9		3		6
	 */
	public Double getDistanceToShapeBorderFromPoint(Shape shape, Point point) {
		if (shape.getVisioBounds().getUpperLeft().getX() < point.getX()) {
			if (point.getX() < shape.getVisioBounds().getLowerRight().getX()) {
				if (shape.getVisioBounds().getLowerRight().getY() <= point.getY()) {
					if (point.getY() <= shape.getVisioBounds().getUpperLeft().getY()) {
						// 1: point is inside of the shape bounds
						return 0.0;
					} else {
						// 2: point directly above
						return point.getY() - shape.getVisioBounds().getUpperLeft().getY();
					}
				} else {
					// 3: point directly under
					return shape.getVisioBounds().getLowerRight().getY() - point.getY();
				}
			} else {
				if (shape.getVisioBounds().getLowerRight().getY() <= point.getY()) {
					if (point.getY() <= shape.getVisioBounds().getUpperLeft().getY()) {
						return point.getX() - shape.getVisioBounds().getLowerRight().getX();
					} else {
						// 5: point in the top right area
						Double xDistance = point.getX() - shape.getVisioBounds().getLowerRight().getX();
						Double yDistance = point.getY() - shape.getVisioBounds().getUpperLeft().getY();
						return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
					}
				} else {
					// 6: point in the lower right area
					Double xDistance = point.getX() - shape.getVisioBounds().getLowerRight().getX();
					Double yDistance = point.getY() - shape.getVisioBounds().getLowerRight().getY();
					return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
				}
			}
		} else {
			if (shape.getVisioBounds().getLowerRight().getY() <= point.getY()) {
				if (point.getY() <= shape.getVisioBounds().getUpperLeft().getY()) {
					// 7: point is directly left
					return shape.getVisioBounds().getUpperLeft().getX() - point.getX();
				} else {
					// 8: point is in the top left area
					Double xDistance = point.getX() - shape.getVisioBounds().getUpperLeft().getX();
					Double yDistance = point.getY() - shape.getVisioBounds().getUpperLeft().getY();
					return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
				}
			} else {
				// 9: point is in the bottom left area
				Double xDistance = point.getX() - shape.getVisioBounds().getUpperLeft().getX();
				Double yDistance = point.getY() - shape.getVisioBounds().getLowerRight().getY();
				return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
			}
		}
	}


}
