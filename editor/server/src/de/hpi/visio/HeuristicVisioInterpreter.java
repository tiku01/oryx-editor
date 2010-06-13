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
		// TODO implement fency logic
		return page;
	}
	
	private Page interpreteShapeNames(Page visioPage) {
		List<Shape> allShapes = visioPage.getShapes();
		List<Shape> shapesWithNames = new ArrayList<Shape>();
		for (Shape shape : allShapes) {
			if (shape.name == null || shape.name.equals("")) {
				if (shape.getLabel() != null && shape.getLabel() != "") {
					String shouldSkip = importUtil.getValueForHeuristic("Skip_unknown_NameU_But_With_Label");
					if (Boolean.valueOf(shouldSkip)) 
						continue;
					String defaultType = importUtil.getValueForHeuristic("Unknown_NameU_But_With_Label_is");
					shape.setName(defaultType);
				} else {
					String shouldSkip = importUtil.getValueForHeuristic("Skip_unknown_NameU_And_Without_Label");
					if (Boolean.valueOf(shouldSkip)) 
						continue;
					String defaultType = importUtil.getValueForHeuristic("Unknown_NameU_And_Without_Label_is");
					shape.setName(defaultType);
				}
			}
			shapesWithNames.add(shape);
		}
		visioPage.setShapes(shapesWithNames);
		return visioPage;
	}

}
