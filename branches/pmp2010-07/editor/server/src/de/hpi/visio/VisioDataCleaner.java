package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.data.XForm;
import de.hpi.visio.util.ImportConfigurationUtil;

public class VisioDataCleaner {
	
	private ImportConfigurationUtil importUtil;
	
	public VisioDataCleaner(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
	}

	public Page checkAndCleanVisioData(Page visioPage) {
		Page checkedPage = checkPage(visioPage);
		Page checkedAndCleanedPage = cleanPage(checkedPage);
		return checkedAndCleanedPage;
	}

	private Page checkPage(Page visioPage) {
		Page halfCheckedPage = checkDiagramForBounds(visioPage);
		Page fullCheckedPage = checkAllShapesForBounds(halfCheckedPage);
		return fullCheckedPage;
	}

	private Page cleanPage(Page visioPage) {
		Page pageWithStandardShapes = normalizeNames(visioPage);
		return pageWithStandardShapes;
	}
	
	private Page checkDiagramForBounds(Page visioPage) {
		if (visioPage.getWidth() == null) {
			String heuristicValue = importUtil.getValueForHeuristic("Default_Page_Width");
			visioPage.setWidth(Double.valueOf(heuristicValue));
		}
		if (visioPage.getHeight() == null) {
			String heuristicValue = importUtil.getValueForHeuristic("Default_Page_Height");
			visioPage.setHeight(Double.valueOf(heuristicValue));
		}
		return visioPage;
	}
	
	private Page checkAllShapesForBounds(Page page) {
		List<Shape> allShapes = page.getShapes();
		List<Shape> shapesWithBounds = new ArrayList<Shape>();
		for (Shape shape : allShapes) {
			if (hasCompleteBoundaries(shape)) 
				shapesWithBounds.add(shape);
		}
		page.setShapes(shapesWithBounds);
		return page;
	}

	private Page normalizeNames(Page visioPage) {
		List<Shape> shapes = visioPage.getShapes();
		List<Shape> shapesWithNormalizedNames = new ArrayList<Shape>();
		for (Shape shape : shapes) {
			if (shape.getName() != null && shape.getName().contains(".")) {
				int end = shape.getName().indexOf(".");
				String cleanedName = shape.getName().substring(0,end);
				shape.setName(cleanedName);
			} 
			shapesWithNormalizedNames.add(shape);
		}
		visioPage.setShapes(shapesWithNormalizedNames);
		return visioPage;
	}

	private boolean hasCompleteBoundaries(Shape shape) {
		XForm xForm = shape.xForm;
		if ( xForm.height != null && xForm.width != null && xForm.positionX != null && xForm.positionY != null) {
			return true;
		} else {
			return false;
		}
	}
	
}
