package de.hpi.visio.util;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Shape;
import de.hpi.visio.data.Page;
import java.util.Collections;

public class ShapeUtil {
	
	private ImportConfigurationUtil importUtil;
	private String actualFixedSizeCategory = null;
	
	private Integer visioPointUnitFactor;
	private Double maxDistanceThresholdInVisioUnit;
	
	public ShapeUtil(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
		 visioPointUnitFactor = Integer.valueOf(importUtil.getValueForHeuristic("Unit_To_Pixel_Exchange"));
		 maxDistanceThresholdInVisioUnit = Double.valueOf(importUtil.getValueForHeuristic("maxEdgeToShapeDistance"));
	}
		
	public Bounds getCorrectedShapeBounds(Shape shape, Page page) {
		Bounds correctedAndCheckedBounds = null;
		Bounds shapeBounds = shape.getBoundsOnPage(page);
		Bounds correctedBounds = correctPointsOfBounds(shapeBounds);
		if(isShapeOfFixedSize(shape,page)) {
			correctedAndCheckedBounds = getFixedBoundsSizeForShapeOnPage(correctedBounds);
		} else {
			if (isShapeResizableWithMinimumSize(shape)) {
				correctedAndCheckedBounds = checkBoundsForShapesMinimumSize(correctedBounds, shape);
			} else {
				// edges are resizable without a minimum size
				correctedAndCheckedBounds = correctedBounds; 
			}
		}
		return correctedAndCheckedBounds;
	}

	private boolean isShapeOfFixedSize(Shape shape, Page page) {
		Boolean isFixedSize = false;
		String stencilId = importUtil.getStencilIdForName(shape.getName());
		String[] fixedSizeShapeCategories = importUtil.getOryxBPMNConfig("fixedSizeShapeCategories").split(",");
		for (String category : fixedSizeShapeCategories) {
			String[] fixedSizeShapes = importUtil.getOryxBPMNConfig(category).split(",");
			for (String fixedSizeShape : fixedSizeShapes) {
				if (fixedSizeShape.equalsIgnoreCase(stencilId)) {
					isFixedSize = true;
					actualFixedSizeCategory = category;
				}
			}
		}
		return isFixedSize;
	}
	
	private Bounds getFixedBoundsSizeForShapeOnPage(Bounds bounds) {
		if (actualFixedSizeCategory == null)
			throw new IllegalStateException("Also a shape should have fixed boundaries, " +
					"it wasn't possible to set those.");
		String actualCategory = importUtil.getOryxBPMNConfig(actualFixedSizeCategory + ".category");
		Double fixedWidth = Double.valueOf(importUtil.getOryxBPMNConfig(actualCategory + ".fixedWidth"));
		Double fixedHeight = Double.valueOf(importUtil.getOryxBPMNConfig(actualCategory + ".fixedHeight"));
		// json interpretation at the client works better with rounded values
		bounds.setUpperLeft(new Point(Double.valueOf(bounds.getUpperLeft().getX().intValue()), (Double.valueOf(bounds.getUpperLeft().getY().intValue()))));
		bounds.setLowerRight(new Point(bounds.getUpperLeft().getX().intValue() + fixedWidth, bounds.getLowerRight().getY()));
		bounds.setLowerRight(new Point(bounds.getLowerRight().getX(), bounds.getUpperLeft().getY().intValue() + fixedHeight));
		return bounds;
	}

	private Bounds checkBoundsForShapesMinimumSize(Bounds bounds, Shape shape) {
		String stencilId = importUtil.getStencilIdForName(shape.name);
		Double minHeight = Double.valueOf(importUtil.getOryxBPMNConfig(stencilId + ".minHeight"));
		Double minWidth = Double.valueOf(importUtil.getOryxBPMNConfig(stencilId + ".minWidth"));
		Double actualWidth = bounds.getLowerRight().getX() - bounds.getUpperLeft().getX();
		Double actualHeight = bounds.getLowerRight().getY() - bounds.getUpperLeft().getY();
		if (actualHeight < minHeight)
			bounds.setLowerRight(new Point(bounds.getLowerRight().getX(), bounds.getUpperLeft().getY() + minHeight));
		if (actualWidth < minWidth)
			bounds.setLowerRight(new Point(bounds.getUpperLeft().getX() + minWidth, bounds.getLowerRight().getY()));
		return bounds;
	}
	
	public Bounds correctPointsOfBounds(Bounds bounds) {
		Point correctedLowerRight = getCorrectedPoint(bounds.getLowerRight());
		Point correctedUpperLeft = getCorrectedPoint(bounds.getUpperLeft());
		Bounds result = new Bounds(correctedLowerRight, correctedUpperLeft);
		return result;
	}
	
	private Point getCorrectedPoint(Point point) {
		Point correctedPoint = new Point(point.getX() * visioPointUnitFactor,
				point.getY() * visioPointUnitFactor);
		return correctedPoint;
	}
	
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
	
	private Boolean isShapeResizableWithMinimumSize(Shape shape) {
		String[] resizableElements = importUtil.getOryxBPMNConfig("resizableElements").split(",");
		Boolean isResizable = false;
		for (int i=0; i < resizableElements.length; i++) {
			String stencilId = importUtil.getStencilIdForName(shape.name);
			if (resizableElements[i].equalsIgnoreCase(stencilId))
				isResizable = true;
		}
		return isResizable;
	}
	
	public ArrayList<Point> getCorrectedDockersForShape(Shape shape, Page page) {
		String stencilId = importUtil.getStencilIdForName(shape.getName());
		for (String edgeId : importUtil.getOryxBPMNConfig("Edges").split(",")) {
			if (edgeId.equalsIgnoreCase(stencilId)) {
				return getCorrectedEdgeDockers(shape, page);
			}
		}
		return getCorrectedCentralDockerAsList(shape, page);
	}
	
	private ArrayList<Point> getCorrectedCentralDockerAsList(Shape shape, Page page) {
		ArrayList<Point> correctedCentalDockerList = new ArrayList<Point>();
		correctedCentalDockerList.add(getCorrectedCentralDocker(shape, page));
		return correctedCentalDockerList;
	}
	
	private Point getCorrectedCentralDocker(Shape shape, Page page) {
		Point centralDocker = shape.getCentralPinForPage(page);
		Point correctedDocker = getCorrectedPoint(centralDocker);
		return correctedDocker;
	}
	
	private ArrayList<Point> getCorrectedEdgeDockers(Shape shape, Page page) {
		ArrayList<Point> correctedDockers = new ArrayList<Point>();
		if (shape.getSource() != null) {
			Bounds correctedSourceBounds = getCorrectedShapeBounds(shape.getSource(),page);
			Point sourceMiddlePoint = getCentralPinOfCorrectedBounds(correctedSourceBounds);
			correctedDockers.add((sourceMiddlePoint));
		}
		if (shape.getTarget() != null) {
			Bounds correctedTargetBounds = getCorrectedShapeBounds(shape.getTarget(),page);
			Point targetMiddlePoint = getCentralPinOfCorrectedBounds(correctedTargetBounds);
			correctedDockers.add((targetMiddlePoint));
		}
		return correctedDockers;
	}
	
	private Point getCentralPinOfCorrectedBounds(Bounds bounds) {
		Double x = (bounds.getLowerRight().getX() - bounds.getUpperLeft().getX()) / 2;
		Double y = (bounds.getLowerRight().getY() - bounds.getUpperLeft().getY()) / 2;
		return new Point(x,y);
	}

	public Shape getSmallestShapeToPointWithinThreshold(Shape self, Point point, List<Shape> shapes) {
		if (shapes.size() > 0) {
			List<Shape> shapesWithinThreshold = new ArrayList<Shape>();
			for (Shape shape : shapes) {
				if (shape == self)
					continue;
				if (getDistanceToShapeBorderFromPoint(shape, point) < maxDistanceThresholdInVisioUnit) {
					shapesWithinThreshold.add(shape);
				}
			}
			if (shapesWithinThreshold.size() > 0) {
				Collections.sort(shapesWithinThreshold);	// the smallest
				return shapesWithinThreshold.get(0);		// will be returned
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
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX(), shape1.getCentralPin().getY() + shape1.getHeight()/2)));
		// 3
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX(), shape1.getCentralPin().getY() - shape1.getHeight()/2)));
		// 4
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX() + shape1.getWidth()/2, shape1.getCentralPin().getY())));
		// 5
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX() + shape1.getWidth()/2, shape1.getCentralPin().getY() + shape1.getHeight()/2)));
		// 6
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX() + shape1.getWidth()/2, shape1.getCentralPin().getY() - shape1.getHeight()/2)));
		// 7
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX() - shape1.getWidth()/2, shape1.getCentralPin().getY())));
		// 8
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX() - shape1.getWidth()/2, shape1.getCentralPin().getY() + shape1.getHeight()/2)));
		// 9
		distancesFromCorners.add(getDistanceToShapeBorderFromPoint(shape2, new Point(shape1.getCentralPin().getX() - shape1.getWidth()/2, shape1.getCentralPin().getY() - shape1.getHeight()/2)));
		
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
						return 0.0; // 1: point is inside of the shape bounds
					} else {
						return point.getY() - shape.getVisioBounds().getUpperLeft().getY(); // 2: point directly above
					}
				} else {
					return shape.getVisioBounds().getLowerRight().getY() - point.getY(); // 3: point directly under
				} 
			} else {
				if (shape.getVisioBounds().getLowerRight().getY() <= point.getY()) {
					if (point.getY() <= shape.getVisioBounds().getUpperLeft().getY()) { 
						return point.getX() - shape.getVisioBounds().getLowerRight().getX(); // 4: point directly right
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
