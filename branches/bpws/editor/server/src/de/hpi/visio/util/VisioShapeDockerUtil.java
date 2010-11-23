package de.hpi.visio.util;

import java.util.ArrayList;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;

/**
 * Utility class that generates dockers for oryx shapes from data of visio
 * shapes.
 * 
 * @author Thamsen
 */
public class VisioShapeDockerUtil {

	private ImportConfigurationUtil importUtil;
	private VisioShapeBoundsUtil boundsUtil;

	public VisioShapeDockerUtil(ImportConfigurationUtil importUtil, VisioShapeBoundsUtil boundsUtil) {
		this.importUtil = importUtil;
		this.boundsUtil = boundsUtil;
	}

	/**
	 * Handles edges and other shapes different. While other shapes get a
	 * central docker, shapes get a starting and an end docker to be attached to
	 * source and target. Also for every change of direction there's another
	 * docker for the edges.
	 */
	public ArrayList<Point> getCorrectedDockersForShape(Shape shape, Page page) {
		String stencilId = importUtil.getStencilIdForName(shape.getName());
		for (String edgeId : importUtil.getStencilSetConfig("Edges").split(",")) {
			if (edgeId.equalsIgnoreCase(stencilId)) {
				return getCorrectedEdgeDockers(shape, page);
			}
		}
		return getCorrectedCentralDockerAsList(shape, page);
	}

	private ArrayList<Point> getCorrectedEdgeDockers(Shape shape, Page page) {
		ArrayList<Point> correctedDockers = new ArrayList<Point>();
		if (shape.getSource() != null) {
			Bounds correctedSourceBounds = boundsUtil.getCorrectOryxShapeBoundsWithResizing(shape.getSource(), page);
			Point sourceMiddlePoint = getCentralPinOfCorrectedBounds(correctedSourceBounds);
			correctedDockers.add((sourceMiddlePoint));
		}
		if (!isStraightException(shape)) {
			for (Point dockerPoint : shape.getDockerPoints()) {
				Point correctedDocker = boundsUtil.convertToOryxPoint(dockerPoint);
				Point correctEdgeStartPoint = boundsUtil.convertToOryxPoint(shape.getStartPointForPage(page));
				Double correctedAndFromSourceX = correctEdgeStartPoint.getX() + correctedDocker.getX();
				Double correctedAndFromSourceY = correctEdgeStartPoint.getY() - correctedDocker.getY();
				correctedDockers.add(new Point(correctedAndFromSourceX, correctedAndFromSourceY));
			}
		}
		if (shape.getTarget() != null) {
			Bounds correctedTargetBounds = boundsUtil.getCorrectOryxShapeBoundsWithResizing(shape.getTarget(), page);
			Point targetMiddlePoint = getCentralPinOfCorrectedBounds(correctedTargetBounds);
			correctedDockers.add((targetMiddlePoint));
		}
		return correctedDockers;
	}

	/**
	 * 	In some stencils (e.g., visio epc) there is a weird docker, when the edge goes straight.
	 * 	Also the edge should go straight in the xml too, it contains a docker
	 * 	that is somewhat around 1.25 to the right or around 1.25 up and therefore the 
	 * 	layout isn't quit right, then.
	 */
	private boolean isStraightException(Shape shape) {
		if (shape.getStartPoint().getX().equals(shape.getEndPoint().getX()) ||
				shape.getStartPoint().getY().equals(shape.getEndPoint().getY())) {
			if (shape.getDockerPoints().size() == 1) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<Point> getCorrectedCentralDockerAsList(Shape shape, Page page) {
		Point centralDocker = shape.getCentralPinForPage(page);
		Point correctedDocker = boundsUtil.convertToOryxPoint(centralDocker);
		ArrayList<Point> correctedCentalDockerList = new ArrayList<Point>();
		correctedCentalDockerList.add(correctedDocker);
		return correctedCentalDockerList;
	}

	private Point getCentralPinOfCorrectedBounds(Bounds bounds) {
		Double x = (bounds.getLowerRight().getX() - bounds.getUpperLeft().getX()) / 2;
		Double y = (bounds.getLowerRight().getY() - bounds.getUpperLeft().getY()) / 2;
		return new Point(x, y);
	}

}
