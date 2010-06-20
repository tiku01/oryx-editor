package de.hpi.visio.util;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Shape;
import de.hpi.visio.data.Page;

public class ShapeUtil {
	
	private ImportConfigurationUtil importUtil;
	private String actualFixedSizeCategory = null;
	
	public ShapeUtil(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
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

	public Bounds getCorrectedDiagramBounds(Bounds bounds) {
		Bounds correctedBounds = correctPointsOfBounds(bounds);
		return correctedBounds;
	}
	
	private Bounds correctPointsOfBounds(Bounds bounds) {
		String heuristicValue = importUtil.getValueForHeuristic("Unit_To_Pixel_Exchange");
		Integer factor = Integer.valueOf(heuristicValue);
		Point correctedLowerRight = new Point(bounds.getLowerRight().getX() * factor, bounds.getLowerRight().getY() * factor);
		Point correctedUpperLeft = new Point(bounds.getUpperLeft().getX() * factor, bounds.getUpperLeft().getY() * factor);
		Bounds result = new Bounds(correctedLowerRight, correctedUpperLeft);
		return result;
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

	public ArrayList<Point> getCorrectedDockers(Shape shape, Page page) {
		ArrayList<Point> dockers = new ArrayList<Point>();
		Bounds bounds = getCorrectedShapeBounds(shape, page);
		Point centralDocker = new Point(bounds.getUpperLeft().getX() + ((bounds.getLowerRight().getX() - bounds.getUpperLeft().getX()) / 2), 
				bounds.getUpperLeft().getY() + ((bounds.getLowerRight().getY() - bounds.getUpperLeft().getY()) / 2));
		dockers.add(centralDocker);
		return dockers;
	}

	public Shape getNearestShapeToPointInsideThreshold(Shape self, Point point, List<Shape> shapes) {
		if (shapes.size() > 0) {
			int start = 0;
			if (self != shapes.get(start)) 
				start = 1;
			Shape nearestShape = shapes.get(start);
			Double minDistance = getDistanceToShapeBorderFromPoint(nearestShape, point);
			for (Shape shape : shapes) {
				if (shape == self)
					continue;
				if (getDistanceToShapeBorderFromPoint(shape, point) < minDistance) {
					nearestShape = shape;
					minDistance = getDistanceToShapeBorderFromPoint(shape, point);
				}
			}
			Double maxDistanceThresholdInVisioUnit = Double.valueOf(importUtil.getValueForHeuristic("maxEdgeToShapeDistance"));
			if (minDistance < maxDistanceThresholdInVisioUnit)
				return nearestShape;
		} 
		return null;
	}
	
	
	/*	Getting of a point to a shape's nearest border
	 * 	All 8 distance cases are: 
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
