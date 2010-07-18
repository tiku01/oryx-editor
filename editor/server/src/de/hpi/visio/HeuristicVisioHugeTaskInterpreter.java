package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.ImportConfigurationUtil;

/**
 * HeuristicVisioHugeTaskInterpreter interprets tasks with a size that exceeds
 * given thresholds as a subprocess instead of a task, because there are visio
 * stencil sets without an extra shape for subprocess. 
 * Instead task size's just increased to have a subprocess.
 * 
 * @author Thamsen
 */
public class HeuristicVisioHugeTaskInterpreter {

	private ImportConfigurationUtil importUtil;

	public HeuristicVisioHugeTaskInterpreter(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
	}

	public Page interpreteHugeTasksAsSubprocesses(Page page) {
		Boolean hugeTasksAreSubprocesses = 
			Boolean.valueOf(importUtil.getStencilSetHeuristic("interpreteHugeTasksAsSubprocesses"));
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
		Double widthThreshold = Double.valueOf(importUtil.getStencilSetHeuristic("taskToSubprocessThresholdWidth"));
		Double heightThreshold = Double.valueOf(importUtil.getStencilSetHeuristic("taskToSubprocessThresholdHeight"));
		if (task.getWidth() >= widthThreshold || task.getHeight() >= heightThreshold) {
			return true;
		} else {
			return false;
		}
	}

}
