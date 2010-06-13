package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.visio.util.ImportConfigurationUtil;

public class HeuristicOryxInterpreter {
	
	private final List<String> CONTAINER_SHAPE_NAMES;
	private final List<String> NOT_CONTAINABLE_SHAPE_NAMES;
	
	private ImportConfigurationUtil importUtil;

	public HeuristicOryxInterpreter(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
		CONTAINER_SHAPE_NAMES = new ArrayList<String>();
		CONTAINER_SHAPE_NAMES.add("Pool");
		CONTAINER_SHAPE_NAMES.add("Lane");
		CONTAINER_SHAPE_NAMES.add("Subprocess");
		NOT_CONTAINABLE_SHAPE_NAMES = new ArrayList<String>();
		NOT_CONTAINABLE_SHAPE_NAMES.add("Pool");
		NOT_CONTAINABLE_SHAPE_NAMES.add("Group");
	}

	public Diagram interpret(Diagram diagram) {
		ArrayList<Shape> correctedChildShapes = correctContainment(diagram.getChildShapes());
		diagram.setChildShapes(correctedChildShapes);
		return diagram;
	}

	private ArrayList<Shape> correctContainment(ArrayList<Shape> shapes) {
		List<Shape> containedShapes = new ArrayList<Shape>();
		for (Shape containableShape : shapes) {
			if (NOT_CONTAINABLE_SHAPE_NAMES.contains(containableShape.getStencilId()))
				continue;
			Shape container = searchBestFittingContainers(containableShape, shapes);
			if (container != null) {
				containedShapes.add(containableShape);
				ArrayList<Shape> containerChildren = container.getChildShapes();
				containerChildren.add(containableShape);
				correctShapeBoundsToBeRelativeToContainer(containableShape, container);
				container.setChildShapes(containerChildren);
			}
		}
		shapes.removeAll(containedShapes);
		return shapes;
	}
	
	private Shape searchBestFittingContainers(Shape containableShape, ArrayList<Shape> shapes) {
		List<Shape> allFittingContainers = new ArrayList<Shape>();
		for (Shape possibleContainer : shapes) {
			Double shapeSizeThresholdFactor = Double.valueOf(importUtil.getStencilSetConfig("containerShouldHaveAtLeastXTimesTheSizeThreshold"));
			if (CONTAINER_SHAPE_NAMES.contains(possibleContainer.getStencilId())) {
				if (getShapeSize(possibleContainer) > (getShapeSize(containableShape) * shapeSizeThresholdFactor)) {
					if (firstShapeIsOnSecoundShape(containableShape, possibleContainer))
						allFittingContainers.add(possibleContainer);
				} 
			}
		}
		if (allFittingContainers.size() == 0) {
			return null;
		} else {
			return smallestShape(allFittingContainers);
		}
			
	}
	
	private Shape smallestShape(List<Shape> shapes) {
		Shape smallestShape = shapes.get(0);
		for (Shape shape : shapes) {
			if (getShapeSize(shape) < getShapeSize(smallestShape)) {
				smallestShape = shape;
			}
		}
		return smallestShape;
	}

	private boolean firstShapeIsOnSecoundShape(Shape containableShape, Shape container) {
		Point centralPin = getCentralPinOfShape(containableShape);
		if (container.getUpperLeft().getX() <= centralPin.getX() && centralPin.getX() <= container.getLowerRight().getX())
			if (container.getUpperLeft().getY() <= centralPin.getY() && centralPin.getY() <= container.getLowerRight().getY())
				return true;
		return false;
	}

	private Double getShapeSize(Shape shape) {
		return shape.getHeight() * shape.getWidth();
	}

	private Point getCentralPinOfShape(Shape shape) {
		Double x = shape.getUpperLeft().getX() + (shape.getWidth() / 2);
		Double y = shape.getUpperLeft().getY() + (shape.getHeight() / 2);
		return new Point(x,y);
	}
	
	private void correctShapeBoundsToBeRelativeToContainer(Shape child, Shape container) {
		Double relativeUpperLeftX = child.getUpperLeft().getX() - container.getUpperLeft().getX();
		Double relativeUpperLeftY = child.getUpperLeft().getY() - container.getUpperLeft().getY();
		Double relativeLowerRightX = child.getLowerRight().getX() - container.getUpperLeft().getX();
		Double relativeLowerRightY = child.getLowerRight().getY() - container.getUpperLeft().getY();
		Point upperLeftPoint = new Point(relativeUpperLeftX, relativeUpperLeftY);
		Point lowerRightPoint = new Point(relativeLowerRightX,relativeLowerRightY);
		child.setBounds(new Bounds(lowerRightPoint, upperLeftPoint));
	}

}
