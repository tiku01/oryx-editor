package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.VisioShapeDistanceUtil;

/**
 * HeuristicVisioEdgeInterpreter interprets all shapes, that are defined as edges - flow.
 * Within thresholds the nearest and preferred shape will be set as source or target to
 * the flows start or end.
 * 
 * @author Thamsen
 */
public class HeuristicVisioEdgeInterpreter {
	
	private ImportConfigurationUtil importUtil;
	private VisioShapeDistanceUtil shapeUtil;

	public HeuristicVisioEdgeInterpreter(ImportConfigurationUtil importUtil, VisioShapeDistanceUtil shapeUtil) {
		this.importUtil = importUtil;
		this.shapeUtil = shapeUtil;
	}
	
	public Page interpretEdges(Page visioPage) {
		List<Shape> correctedChildShapes = assignAllEdges(visioPage);
		visioPage.setShapes(correctedChildShapes);
		return visioPage;
	}
	
	private List<Shape> assignAllEdges(Page page) {
		List<Shape> shapes = page.getShapes();
		List<Shape> edges = getAllEdges(shapes);
		for (Shape edge : edges) {
			Shape source = shapeUtil.getNearestPreferedShapeToPointWithinThreshold(edge, edge.getStartPoint(), shapes);
			Shape target = shapeUtil.getNearestPreferedShapeToPointWithinThreshold(edge, edge.getEndPoint(), shapes);
			if (source != null) {
				edge.setSource(source);
				source.addOutgoing(edge);
				edge.addIncoming(edge);
			}
			if (target != null) {
				target.addIncoming(edge);
				edge.addOutgoing(target);
				edge.setTarget(target);
			}
		}
		return shapes;
	}

	private List<Shape> getAllEdges(List<Shape> shapes) {
		String[] edgeStencils = importUtil.getStencilSetConfig("Edges").split(",");
		List<Shape> edges = new ArrayList<Shape>();
		for (Shape shape : shapes) {
			for (String edgeStencil : edgeStencils) {
				if (edgeStencil.equalsIgnoreCase(importUtil.getStencilIdForName(shape.getName())))
					edges.add(shape);
			}
		}
		return edges;
	}

}
