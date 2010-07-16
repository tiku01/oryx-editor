package de.hpi.visio.util;

import java.util.ArrayList;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;

/** 
 * Utility class that generates dockers for oryx shapes from data
 * of visio shapes.
 * @author Thamsen
 */
public class VisioShapeDockerUtil {
	
	public ImportConfigurationUtil importUtil;
	public VisioShapeBoundsUtil boundsUtil;

	public VisioShapeDockerUtil(ImportConfigurationUtil importUtil, VisioShapeBoundsUtil boundsUtil) {
		this.importUtil = importUtil;
		this.boundsUtil = boundsUtil;
	}
	
	/**
	 * Handles edges and other shapes different. While other shapes get a central
	 * docker, shapes get a starting and an end docker to be attached to source and target.
	 * Also for every change of direction there's another docker for the edges.
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
			Bounds correctedSourceBounds = boundsUtil.getCorrectOryxShapeBoundsWithResizing(shape.getSource(),page);
			Point sourceMiddlePoint = getCentralPinOfCorrectedBounds(correctedSourceBounds);
			correctedDockers.add((sourceMiddlePoint));
		}
		for (Point dockerPoint : shape.getDockerPoints()) {
			Point correctedDocker = boundsUtil.convertToOryxPoint(dockerPoint);
			Point correctEdgeStartPoint = boundsUtil.convertToOryxPoint(shape.getStartPointForPage(page));
			Double correctedAndFromSourceX = correctEdgeStartPoint.getX() + correctedDocker.getX();
			Double correctedAndFromSourceY = correctEdgeStartPoint.getY() - correctedDocker.getY();
			correctedDockers.add(new Point(correctedAndFromSourceX, correctedAndFromSourceY));
		}
		if (shape.getTarget() != null) {
			Bounds correctedTargetBounds = boundsUtil.getCorrectOryxShapeBoundsWithResizing(shape.getTarget(),page);
			Point targetMiddlePoint = getCentralPinOfCorrectedBounds(correctedTargetBounds);
			correctedDockers.add((targetMiddlePoint));
		}
		return correctedDockers;
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
		return new Point(x,y);
	}

}
