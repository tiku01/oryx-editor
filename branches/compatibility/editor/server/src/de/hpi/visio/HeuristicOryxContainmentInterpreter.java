package de.hpi.visio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.ShapesLowerRightXComparator;
import de.hpi.visio.util.ShapesLowerRightYComparator;
import de.hpi.visio.util.ShapesUpperLeftXComparator;
import de.hpi.visio.util.ShapesUpperLeftYComparator;

import java.util.Collections;

/**
 * HeuristicOryxContainmentInterpreter is used to correct the containment
 * relations for newly imported visio shapes of diagram. 
 * - in BPMN: Pools that are build by modeling pool head and pool body separately will be merged to a
 * 		single pool. 
 * - Container shapes (e.g. pools) can contain containable shapes (e.g. tasks) 
 * 		and in oryx this relation is explicit, while visio does not care.
 * 		This relation is build from shapes type and graphical information.
 * 
 * @author Thamsen
 */
public class HeuristicOryxContainmentInterpreter {

	private List<String> CONTAINER_SHAPE_IDS = new ArrayList<String>();;
	private List<String> NOT_CONTAINABLE_SHAPE_IDS = new ArrayList<String>();;

	private ImportConfigurationUtil importUtil;

	public HeuristicOryxContainmentInterpreter(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
		for (String containerShape : importUtil.getStencilSetConfig("ContainerShapes").split(",")) {
			CONTAINER_SHAPE_IDS.add(containerShape);
		}
		for (String containerShape : importUtil.getStencilSetConfig("NotContainableShapes").split(",")) {
			NOT_CONTAINABLE_SHAPE_IDS.add(containerShape);
		}
	}

	public Diagram interpretDiagram(Diagram diagram) {
		if (Boolean.valueOf(importUtil.getStencilSetConfig("HasContainingShapes"))) {
			ArrayList<Shape> correctedChildShapes = correctPools(diagram.getChildShapes());
			correctedChildShapes = correctContainment(correctedChildShapes);
			diagram.setChildShapes(correctedChildShapes);
		}
		return diagram;
	}

	private ArrayList<Shape> correctPools(ArrayList<Shape> childShapes) {
		List<Shape> assignedlanes = new ArrayList<Shape>();
		for (Shape pool : getAllPoolsSortedFromRightToLeft(childShapes)) {
			List<Shape> swimlanes = getFittingLanesAndPools(pool, childShapes);
			ArrayList<Shape> poolsLanes = new ArrayList<Shape>();
			for (Shape swimlane : swimlanes) {
				// only swimlanes that arent yet contained can be contained
				if (swimlane != null && !assignedlanes.contains(swimlane)) {
					swimlane.setStencil(new StencilType("Lane"));
					assignedlanes.add(swimlane);
					poolsLanes.add(swimlane);
				}
			}
			if (poolsLanes.size() > 0) {
				pool.setChildShapes(poolsLanes);
				correctPoolBounds(pool);
			}
		}
		childShapes.removeAll(assignedlanes);
		return childShapes;
	}

	private ArrayList<Shape> getAllPoolsSortedFromRightToLeft(ArrayList<Shape> shapes) {
		ArrayList<Shape> pools = new ArrayList<Shape>();
		for (Shape shape : shapes) {
			if (shape.getStencilId().equalsIgnoreCase("Pool")) {
				pools.add(shape);
			}
		}
		Collections.sort(pools, new ShapesUpperLeftXComparator());
		Collections.reverse(pools); // from left to right, to handle right pools
									// before left: left pools can contain more
									// right pools or lanes
		return pools;
	}

	private void correctPoolBounds(Shape pool) {
		setPoolHeaderWidthToStandardWidth(pool);
		Set<Shape> allChildShapesSet = getAllChildShapes(pool);
		List<Shape> allChildShapes = new ArrayList<Shape>(allChildShapesSet);
		Collections.sort(allChildShapes, new ShapesUpperLeftYComparator());
		Double upperLeftY = allChildShapes.get(0).getUpperLeft().getY();
		Collections.sort(allChildShapes, new ShapesUpperLeftXComparator());
		Double upperLeftX = allChildShapes.get(0).getUpperLeft().getX();
		Collections.sort(allChildShapes, new ShapesLowerRightYComparator());
		Collections.reverse(allChildShapes);
		Double lowerRightY = allChildShapes.get(0).getLowerRight().getY();
		Collections.sort(allChildShapes, new ShapesLowerRightXComparator());
		Collections.reverse(allChildShapes);
		Double lowerRightX = allChildShapes.get(0).getLowerRight().getX();
		Bounds poolBounds = new Bounds(new Point(lowerRightX, lowerRightY), new Point(upperLeftX, upperLeftY));
		pool.setBounds(poolBounds);
	}

	private void setPoolHeaderWidthToStandardWidth(Shape pool) {
		Double poolHeaderWidth = Double.valueOf(importUtil.getStencilSetConfig("PoolHeaderWidth"));
		Point poolsLowerRight = new Point(pool.getBounds().getUpperLeft().getX() + poolHeaderWidth, pool
				.getLowerRight().getY());
		pool.setBounds(new Bounds(poolsLowerRight, pool.getBounds().getUpperLeft()));
	}

	private Set<Shape> getAllChildShapes(Shape shape) {
		Set<Shape> allChildShapes = new HashSet<Shape>();
		for (Shape childShape : shape.getChildShapes()) {
			allChildShapes.addAll(getAllChildShapes(childShape));
		}
		allChildShapes.add(shape);
		return allChildShapes;
	}

	private List<Shape> getFittingLanesAndPools(Shape pool, ArrayList<Shape> allShapes) {
		List<Shape> lanes = new ArrayList<Shape>();
		for (Shape shape : allShapes) {
			if (shape == pool)
				continue;
			if (shapeIsSwimlaneOfPool(pool, shape))
				lanes.add(shape);
		}
		return lanes;
	}

	private boolean shapeIsSwimlaneOfPool(Shape pool, Shape shape) {
		if ((shape.getStencilId().equalsIgnoreCase("Lane") || shape.getStencilId().equalsIgnoreCase("Pool"))) {
			if (pool.getUpperLeft().getX() < shape.getUpperLeft().getX()
					&& (shape.getUpperLeft().getX() < pool.getLowerRight().getX()
							+ Double.valueOf(importUtil.getStencilSetHeuristic("maxDistanceBetweenPoolAndContainedSwimlane")))) {
				Point shapesCentralLeftBorderPoint = new Point(shape.getUpperLeft().getY(), 
						shape.getUpperLeft().getY() + shape.getHeight() / 2);
				if (shapesCentralLeftBorderPoint.getY() >= pool.getUpperLeft().getY()
						&& shapesCentralLeftBorderPoint.getY() <= pool.getLowerRight().getY()) {
					return true;
				}
			}
		}
		return false;
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
		Double shapeSizeThresholdFactor = Double.valueOf(importUtil
				.getStencilSetHeuristic("containerShouldHaveAtLeastXTimesTheSizeThreshold"));
		for (Shape possibleContainer : shapes) {
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
		if (container.getUpperLeft().getX().doubleValue() <= point.getX().doubleValue()
				&& point.getX().doubleValue() <= container.getLowerRight().getX().doubleValue())
			if (container.getUpperLeft().getY().doubleValue() <= point.getY().doubleValue()
					&& point.getY().doubleValue() <= container.getLowerRight().getY().doubleValue())
				return true;
		return false;
	}

	private Double getShapeSize(Shape shape) {
		return shape.getHeight() * shape.getWidth();
	}

	private Point getCentralPinOfShape(Shape shape) {
		Double x = shape.getUpperLeft().getX().doubleValue() + (shape.getWidth() / 2);
		Double y = shape.getUpperLeft().getY().doubleValue() + (shape.getHeight() / 2);
		return new Point(x, y);
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
		Double relativeUpperLeftX = child.getUpperLeft().getX().doubleValue()
				- container.getUpperLeft().getX().doubleValue();
		Double relativeUpperLeftY = child.getUpperLeft().getY().doubleValue()
				- container.getUpperLeft().getY().doubleValue();
		Double relativeLowerRightX = child.getLowerRight().getX().doubleValue()
				- container.getUpperLeft().getX().doubleValue();
		Double relativeLowerRightY = child.getLowerRight().getY().doubleValue()
				- container.getUpperLeft().getY().doubleValue();
		Point upperLeftPoint = new Point(relativeUpperLeftX, relativeUpperLeftY);
		Point lowerRightPoint = new Point(relativeLowerRightX, relativeLowerRightY);
		child.setBounds(new Bounds(lowerRightPoint, upperLeftPoint));
	}

	private void correctShapeDockerToBeRelativeToContainer(Shape childShape, Shape container) {
		// edge-dockers are relative to source and target and therefore not
		// relative to the container
		if (!isBPMNEdge(childShape)) {
			Double childDockerX = childShape.getDockers().get(0).getX().doubleValue()
					- container.getUpperLeft().getX().doubleValue();
			Double childDockerY = childShape.getDockers().get(0).getY().doubleValue()
					- container.getUpperLeft().getY().doubleValue();
			Point centralDocker = new Point(childDockerX, childDockerY);
			ArrayList<Point> dockers = new ArrayList<Point>();
			dockers.add(centralDocker);
			childShape.setDockers(dockers);
		}
	}

	private Boolean isBPMNEdge(Shape shape) {
		for (String edgeId : importUtil.getStencilSetConfig("Edges").split(",")) {
			if (edgeId.equalsIgnoreCase(shape.getStencilId())) {
				return true;
			}
		}
		return false;
	}
}
