package de.hpi.visio.util;

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
		Bounds shapeBounds = shape.getBoundsForPage(page);
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

}
