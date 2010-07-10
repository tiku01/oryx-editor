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
		ArrayList<Shape> correctedChildShapes = correctPools(diagram.getChildShapes());
		correctedChildShapes = correctContainment(correctedChildShapes);
		diagram.setChildShapes(correctedChildShapes);
		return diagram;
	}

	private ArrayList<Shape> correctPools(ArrayList<Shape> childShapes) {
		List<Shape> lanes = new ArrayList<Shape>();
		for (Shape pool : childShapes) {
			if (pool.getStencilId().equalsIgnoreCase("Pool")) {
				Shape lane = getBestFittingLane(pool, childShapes);
				if (lane != null) {
					lanes.add(lane);
					ArrayList<Shape> poolsLanes = new ArrayList<Shape>();
					poolsLanes.add(lane);
					pool.setChildShapes(poolsLanes);
					correctPoolBounds(pool,lane);
				}
			}
		}
		childShapes.removeAll(lanes);
		return childShapes;
	}

	private void correctPoolBounds(Shape pool, Shape lane) {
		Double poolHeaderWidth = Double.valueOf(importUtil.getOryxBPMNConfig("PoolHeaderWidth"));
		Double poolUpperleftX = lane.getUpperLeft().getX() - poolHeaderWidth;
		Double poolUpperLeftY = lane.getUpperLeft().getY();
		Point poolUpperLeft = new Point(poolUpperleftX, poolUpperLeftY);
		Point poolLowerRight = lane.getLowerRight();
		Bounds poolBounds = new Bounds(poolLowerRight, poolUpperLeft);
		pool.setBounds(poolBounds);
	}

	private Shape getBestFittingLane(Shape pool, ArrayList<Shape> allShapes) {
		List<Shape> lanes = new ArrayList<Shape>();
		for (Shape shape : allShapes) {
			if (shapeOverlapsAnotherShape(pool, shape) && shape.getStencilId().equalsIgnoreCase("Lane"))
				lanes.add(shape);
		}
		return mostRightLane(lanes);
	}
	
	private Shape mostRightLane(List<Shape> shapes) {
		Shape rightest = null;
		if (shapes.size() > 0)
			rightest = shapes.get(0);
		for (Shape lane : shapes) {
			if (lane.getUpperLeft().getX() < rightest.getUpperLeft().getX())
				rightest = lane;
		}
		return rightest;
	}

	private boolean shapeOverlapsAnotherShape(Shape pool, Shape shape) {
		Double rightMiddlePinX = pool.getLowerRight().getX();
		Double rightMiddlePinY = pool.getUpperLeft().getY() + (pool.getHeight() / 2);
		Point rightMiddlePin = new Point(rightMiddlePinX, rightMiddlePinY);
		return pointIsOnShape(rightMiddlePin, shape);
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
		makeChildrenShapeFormsRelative(shapes);
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
		return pointIsOnShape(centralPin, container);
	}
	
	private boolean pointIsOnShape(Point point, Shape container) {
		if (container.getUpperLeft().getX().doubleValue() <= point.getX().doubleValue() &&
				point.getX().doubleValue() <= container.getLowerRight().getX().doubleValue())
			if (container.getUpperLeft().getY().doubleValue() <= point.getY().doubleValue() &&
					point.getY().doubleValue() <= container.getLowerRight().getY().doubleValue())
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
	
	private void makeChildrenShapeFormsRelative(ArrayList<Shape> shapes) {
		for (Shape shape : shapes) {
			if (shape.getChildShapes() != null && shape.getChildShapes().size() > 0) {
				makeChildrenShapeFormsRelative(shape.getChildShapes());
				for (Shape childShape : shape.getChildShapes()) {
					correctShapeBoundsToBeRelativeToContainer(childShape, shape);
					correctShapeDockerToBeRelativeToContainer(childShape, shape);
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
	
	private void correctShapeDockerToBeRelativeToContainer(Shape childShape, Shape container) {
		// edge-dockers are relative to source and target and therefore not relative to the container
		if (!isBPMNEdge(childShape)) {
			Double childDockerX = childShape.getDockers().get(0).getX().doubleValue() - container.getUpperLeft().getX().doubleValue();
			Double childDockerY = childShape.getDockers().get(0).getY().doubleValue() - container.getUpperLeft().getY().doubleValue();
			Point centralDocker = new Point(childDockerX, childDockerY);
			ArrayList<Point> dockers = new ArrayList<Point>();
			dockers.add(centralDocker);
			childShape.setDockers(dockers);
		}
	}

	
	private Boolean isBPMNEdge(Shape shape) {
		for (String edgeId : importUtil.getOryxBPMNConfig("Edges").split(",")) {
			if (edgeId.equalsIgnoreCase(shape.getStencilId())) {
				return true;
			}
		}
		return false;
	}
}
