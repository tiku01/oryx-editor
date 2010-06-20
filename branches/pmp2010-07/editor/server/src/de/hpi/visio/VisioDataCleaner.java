package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.data.VisioDocument;
import de.hpi.visio.data.XForm;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.ShapeUtil;

public class VisioDataCleaner {
	
	private ImportConfigurationUtil importUtil;
	private ShapeUtil shapeUtil;
	
	public VisioDataCleaner(ImportConfigurationUtil importUtil, ShapeUtil shapeUtil) {
		this.importUtil = importUtil;
		this.shapeUtil = shapeUtil;
	}

	public Page checkAndCleanVisioData(VisioDocument visioData) {
		Page page = resolveMasterReferences(visioData);
		Page checkedPage = checkPage(page);
		Page checkedAndCleanedPage = cleanPage(checkedPage);
		return checkedAndCleanedPage;
	}

	private Page resolveMasterReferences(VisioDocument visioData) {
		Map<String, String>masterIdToNameMapping = visioData.getMasterIdToNameMapping();
		Page firstPage = visioData.getFirstPage();
		for (Shape shape : firstPage.getShapes()) {
			if (shape.getName() == null || shape.getName().equals(""))
				if (shape.getMasterId() != null && !shape.getMasterId().equals("")) {
					String newName = masterIdToNameMapping.get(shape.getMasterId());
					shape.setName(newName);
				}
		}
		return firstPage;
	}

	private Page checkPage(Page visioPage) {
		Page halfCheckedPage = checkDiagramForBounds(visioPage);
		Page fullCheckedPage = checkAllShapesForBounds(halfCheckedPage);
		return fullCheckedPage;
	}

	private Page cleanPage(Page visioPage) {
		Page pageWithStandardShapes = normalizeNames(visioPage);
		Page pageWithConvertedProperties = convertTaskProperties(pageWithStandardShapes);
		Page pageWithRealSubprocesses = convertTaskWithMarkerToSubprocesses(pageWithConvertedProperties);
		return pageWithRealSubprocesses;
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
	
	private Page convertTaskProperties(Page page) {
		String propertyElementsString = importUtil.getStencilSetConfig("areOnlyTaskProperties");
		String[] propertyElements = propertyElementsString.split(",");
		Shape resultingShape = null;
		for (String propertyElementName : propertyElements) {
			List<Shape> propertyShapes = page.getShapesByName(propertyElementName);
			for (Shape propertyShape : propertyShapes) {
				Boolean isOnlyAMarkerElement = Boolean.valueOf(importUtil.getStencilSetConfig("taskProperties." + propertyElementName + ".isMarker"));
				if (isOnlyAMarkerElement) {
					Shape containingShape = shapeUtil.getFirstShapeOfStencilThatContainsTheGivenShape(page.getShapes(), propertyShape, "Task");
					if (containingShape != null) { 
						resultingShape = containingShape;
					}
					page.removeShape(propertyShape);
				} else {
					resultingShape = propertyShape;
				}
				String propertyKey = importUtil.getStencilSetConfig("taskProperties." + propertyElementName + ".key");
				String propertyValue = importUtil.getStencilSetConfig("taskProperties." + propertyElementName + ".value");
				if (resultingShape != null && propertyKey != null && propertyValue != null) {
					resultingShape.putProperty(propertyKey, propertyValue);
				}
			}
		}
		return page;
	}
	
	private Page convertTaskWithMarkerToSubprocesses(Page page) {
		List<Shape> subprocessMarkers = page.getShapesByName("Collapsed Subprocess Marker");
		for (Shape marker : subprocessMarkers) {
			Shape containingShape = shapeUtil.getFirstShapeOfStencilThatContainsTheGivenShape(page.getShapes(), marker, "Task");
			if (containingShape != null) {
				containingShape.setName(importUtil.getStencilSetConfig("taskWithSubprocessMarker"));
			}	
			page.removeShape(marker);
		}
		return page;
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
