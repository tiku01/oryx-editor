package de.hpi.visio.util;

import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Shape;
import de.hpi.visio.data.Page;

public class ShapeUtil {
	
	ImportConfigurationUtil importUtil;
	
	public ShapeUtil(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
	}
		
	public Bounds getCorrectedShapeBounds(Shape shape, Page page) {
		Bounds shapeBounds = shape.getBoundsOnPage(page);
		Bounds correctedBounds = correctPointsOfBounds(shapeBounds);
		return correctedBounds;
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
	
	public Shape getFirstTaskShapeThatContainsTheGivenShape(List<Shape> shapes, Shape givenShape) {
		for (Shape currentShape : shapes) {
			if (isFirstShapeOnSecondShape(givenShape, currentShape) && givenShape != currentShape) {
				if (currentShape.getName() == null)
					continue;
				if (importUtil.getStencilIdForName(currentShape.getName()).equals("Task")) {
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



}
