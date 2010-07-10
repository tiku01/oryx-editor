package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.ShapeUtil;

public class HeuristicVisioInterpreter {
	
	private ImportConfigurationUtil importUtil;
	private ShapeUtil shapeUtil;

	public HeuristicVisioInterpreter(ImportConfigurationUtil importUtil, ShapeUtil shapeUtil) {
		this.importUtil = importUtil;
		this.shapeUtil = shapeUtil;
	}

	public Page interpret(Page visioPage) {
		Page page = interpreteShapeNames(visioPage);
		page = interpreteTaskBounds(page);
		List<Shape> correctedChildShapes = correctAllEdges(page.getShapes());
		page.setShapes(correctedChildShapes);
		return page;
	}

	

	private List<Shape> correctAllEdges(List<Shape> shapes) {
		List<Shape> edges = getAllEdges(shapes);
		for (Shape edge : edges) {
			Shape source = shapeUtil.getSmallestShapeToPointWithinThreshold(edge, edge.getStartPoint(), shapes);
			Shape target = shapeUtil.getSmallestShapeToPointWithinThreshold(edge, edge.getEndPoint(), shapes);
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
		String[] edgeStencils = importUtil.getOryxBPMNConfig("Edges").split(",");
		List<Shape> edges = new ArrayList<Shape>();
		for (Shape shape : shapes) {
			for (String edgeStencil : edgeStencils) {
				if (edgeStencil.equalsIgnoreCase(importUtil.getStencilIdForName(shape.getName())))
					edges.add(shape);
			}
		}
		return edges;
	}
	
	private Page interpreteShapeNames(Page visioPage) {
		List<Shape> allShapes = visioPage.getShapes();
		List<Shape> shapesWithNames = new ArrayList<Shape>();
		for (Shape shape : allShapes) {
			if (shape.name == null || shape.name.equals("")) {
				if (shape.getLabel() != null && shape.getLabel() != "") {
					String shouldSkip = importUtil.getStencilSetConfig("Skip_unknown_NameU_But_With_Label");
					if (Boolean.valueOf(shouldSkip)) 
						continue;
					String defaultType = importUtil.getStencilSetConfig("Unknown_NameU_But_With_Label_is");
					shape.setName(defaultType);
				} else {
					String shouldSkip = importUtil.getStencilSetConfig("Skip_unknown_NameU_And_Without_Label");
					if (Boolean.valueOf(shouldSkip)) 
						continue;
					String defaultType = importUtil.getStencilSetConfig("Unknown_NameU_And_Without_Label_is");
					shape.setName(defaultType);
				}
			}
			shapesWithNames.add(shape);
		}
		visioPage.setShapes(shapesWithNames);
		return visioPage;
	}
	
	private Page interpreteTaskBounds(Page page) {
		Boolean hugeTasksAreSubprocesses = Boolean.valueOf(importUtil.getStencilSetConfig("interpreteHugeTasksAsSubprocesses"));
		if (hugeTasksAreSubprocesses) {
			List<Shape> tasks = getAllTaskShapes(page);
			for (Shape task : tasks) {
				if (taskIsAsHugeAsASubprocess(task)) 
					task.setName("Subprocess");
			}
		}
		return page;
	}

	private List<Shape> getAllTaskShapes(Page page) {
		List<Shape> tasks = new ArrayList<Shape>();
		for (Shape shape : page.getShapes()) {
			if ("Task".equals(importUtil.getStencilIdForName(shape.getName())))
				tasks.add(shape);
		}
		return tasks;
	}
	
	private boolean taskIsAsHugeAsASubprocess(Shape task) {
		Double widthThreshold = Double.valueOf(importUtil.getStencilSetConfig("taskToSubprocessThresholdWidth"));
		Double heightThreshold = Double.valueOf(importUtil.getStencilSetConfig("taskToSubprocessThresholdHeight"));
		if (task.getWidth() >= widthThreshold || task.getHeight() >= heightThreshold) {
			return true;
		} else {
			return false;
		}
	}

}
