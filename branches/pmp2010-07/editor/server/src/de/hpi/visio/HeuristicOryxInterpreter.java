package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.visio.util.ImportConfigurationUtil;

public class HeuristicOryxInterpreter {
	
	private final List<String> CONTAINER_SHAPE_IDS;
	private final List<String> NOT_CONTAINABLE_SHAPE_IDS;
	
	private ImportConfigurationUtil importUtil;

	public HeuristicOryxInterpreter(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
		CONTAINER_SHAPE_IDS = new ArrayList<String>();
		CONTAINER_SHAPE_IDS.add("Pool");
		CONTAINER_SHAPE_IDS.add("Lane");
		CONTAINER_SHAPE_IDS.add("Subprocess");
		NOT_CONTAINABLE_SHAPE_IDS = new ArrayList<String>();
		NOT_CONTAINABLE_SHAPE_IDS.add("Pool");
		NOT_CONTAINABLE_SHAPE_IDS.add("Group");
	}

	public Diagram interpret(Diagram diagram) {
		ArrayList<Shape> correctedChildShapes = correctContainment(diagram.getChildShapes());
		diagram.setChildShapes(correctedChildShapes);
		return diagram;
	}

	private ArrayList<Shape> correctContainment(ArrayList<Shape> shapes) {
		List<Shape> containedShapes = new ArrayList<Shape>();
		for (Shape containableShape : shapes) {
			if (NOT_CONTAINABLE_SHAPE_IDS.contains(containableShape.getStencilId()))
				continue;
			Shape container = searchBestFittingContainers(containableShape, shapes);
			if (container != null) {
				containedShapes.add(containableShape);
				ArrayList<Shape> containerChildren = container.getChildShapes();
				containerChildren.add(containableShape);
				container.setChildShapes(containerChildren);
			}
		}
		shapes.removeAll(containedShapes);
		makeChildrenShapeBoundsRelative(shapes);
		return shapes;
	}

	private Shape searchBestFittingContainers(Shape containableShape, ArrayList<Shape> shapes) {
		List<Shape> allFittingContainers = new ArrayList<Shape>();
		for (Shape possibleContainer : shapes) {
			Double shapeSizeThresholdFactor = Double.valueOf(importUtil.getStencilSetConfig("containerShouldHaveAtLeastXTimesTheSizeThreshold"));
			if (CONTAINER_SHAPE_IDS.contains(possibleContainer.getStencilId())) {
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
		if (container.getUpperLeft().getX().doubleValue() <= centralPin.getX().doubleValue() &&
				centralPin.getX().doubleValue() <= container.getLowerRight().getX().doubleValue())
			if (container.getUpperLeft().getY().doubleValue() <= centralPin.getY().doubleValue() &&
					centralPin.getY().doubleValue() <= container.getLowerRight().getY().doubleValue())
				return true;
		return false;
	}

	private Double getShapeSize(Shape shape) {
		return shape.getHeight() * shape.getWidth();
	}

	private Point getCentralPinOfShape(Shape shape) {
		Double x = shape.getUpperLeft().getX().doubleValue() + (shape.getWidth() / 2);
		Double y = shape.getUpperLeft().getY().doubleValue() + (shape.getHeight() / 2);
		return new Point(x,y);
	}
	
	private void makeChildrenShapeBoundsRelative(ArrayList<Shape> shapes) {
		for (Shape shape : shapes) {
			if (shape.getChildShapes() != null && shape.getChildShapes().size() > 0) {
				makeChildrenShapeBoundsRelative(shape.getChildShapes());
				for (Shape childShape : shape.getChildShapes()) {
					correctShapeBoundsToBeRelativeToContainer(childShape, shape);
				}
			}
		}
	}
	
	private void correctShapeBoundsToBeRelativeToContainer(Shape child, Shape container) {
		Double relativeUpperLeftX = child.getUpperLeft().getX().doubleValue() - container.getUpperLeft().getX().doubleValue();
		Double relativeUpperLeftY = child.getUpperLeft().getY().doubleValue() - container.getUpperLeft().getY().doubleValue();
		Double relativeLowerRightX = child.getLowerRight().getX().doubleValue() - container.getUpperLeft().getX().doubleValue();
		Double relativeLowerRightY = child.getLowerRight().getY().doubleValue() - container.getUpperLeft().getY().doubleValue();
		Point upperLeftPoint = new Point(relativeUpperLeftX, relativeUpperLeftY);
		Point lowerRightPoint = new Point(relativeLowerRightX,relativeLowerRightY);
		child.setBounds(new Bounds(lowerRightPoint, upperLeftPoint));
	}

}
