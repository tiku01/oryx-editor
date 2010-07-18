package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.data.VisioDocument;
import de.hpi.visio.data.XForm;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.VisioShapeDistanceUtil;

/**
 * The VisioDataCleaner prepares the xmappr-generated Java classes to be mapped
 * to the Diagram Api of Oryx. 
 * - Resolves MasterIds, when in *.vdx-data the nameU (type) was only set by a reference to a master-shape. 
 * - Checks diagrams and all shapes for bounds (only shapes with bounds will be added) 
 * - Merges markers and shapes to be oryx-conform shapes (e.g. task with a loop-marker) 
 * - Sets properties from shape'S nameU values
 * 
 * @author Lauritz Thamsen
 */
public class VisioDataPreparator {

	private ImportConfigurationUtil importUtil;
	private VisioShapeDistanceUtil shapeUtil;

	public VisioDataPreparator(ImportConfigurationUtil importUtil, VisioShapeDistanceUtil shapeUtil) {
		this.importUtil = importUtil;
		this.shapeUtil = shapeUtil;
	}

	public Page checkAndCleanVisioData(VisioDocument visioData) {
		Page page = resolveMasterReferences(visioData);
		Page checkedPage = checkPageAndShapesForBounds(page);
		Page pageWithNamedShapes = normalizeNames(checkedPage);
		Page checkedAndCleanedPage = interpretStencilsOfPage(pageWithNamedShapes);
		return checkedAndCleanedPage;
	}

	private Page resolveMasterReferences(VisioDocument visioData) {
		Map<String, String> masterIdToNameMapping = visioData.getMasterIdToNameMapping();
		Page firstPage = visioData.getFirstPage();
		for (Shape shape : firstPage.getShapes()) {
			if (shape.getMasterId() != null && !shape.getMasterId().equals("")) {
				String newName = masterIdToNameMapping.get(shape.getMasterId());
				shape.setName(newName);
			}
		}
		return firstPage;
	}

	private Page checkPageAndShapesForBounds(Page visioPage) {
		Page halfCheckedPage = checkDiagramForBounds(visioPage);
		Page fullCheckedPage = checkAllShapesForBounds(halfCheckedPage);
		return fullCheckedPage;
	}

	private Page interpretStencilsOfPage(Page visioPage) {
		Page resultingPage = handleShapesWithoutNameAndWithoutLabel(visioPage); 
		resultingPage = removeInvalidShapeNames(resultingPage);
		resultingPage = convertMarkersToTypeWithProperties(resultingPage);
		resultingPage = convertSpecialTypesToTypesWithProperties(resultingPage);
		resultingPage = convertTaskWithMarkerToSubprocesses(resultingPage);
		return resultingPage;
	}
	
	private Page handleShapesWithoutNameAndWithoutLabel(Page visioPage) {
		List<Shape> handledShapes = new ArrayList<Shape>();
		Boolean shouldSkipUnnamedWithoutLabel = Boolean.valueOf(importUtil.getHeuristic("skipUnknownNameUAndWithoutLabel"));
		String defaultTypeWithoutLabel = importUtil.getStencilSetConfig("unknownNameUAndWithoutLabelType");
		for (Shape shape : visioPage.getShapes()) {
			if ((shape.getName() == null || shape.getName().equals("")) &&
					(shape.getLabel() == null || shape.getLabel().equals(""))) {
				if (!shouldSkipUnnamedWithoutLabel) {
					shape.setName(defaultTypeWithoutLabel);
					handledShapes.add(shape);
				}
			} else {
				handledShapes.add(shape);
			}
		}
		visioPage.setShapes(handledShapes);
		return visioPage;
	}

	private Page removeInvalidShapeNames(Page visioPage) {
		List<Shape> shapesWithValidNames = new ArrayList<Shape>();
		for (Shape shape : visioPage.getShapes()) {
			if ("NOT_VALID_SHAPE_NAME".equalsIgnoreCase(shape.getName()))
				shape.setName("");
			shapesWithValidNames.add(shape);
		}
		visioPage.setShapes(shapesWithValidNames);
		return visioPage;
	}

	private Page checkDiagramForBounds(Page visioPage) {
		if (visioPage.getWidth() == null) {
			String heuristicValue = importUtil.getHeuristic("Default_Page_Width");
			visioPage.setWidth(Double.valueOf(heuristicValue));
		}
		if (visioPage.getHeight() == null) {
			String heuristicValue = importUtil.getHeuristic("Default_Page_Height");
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
			shape.setName(normalizeName(shape.getName()));
			shapesWithNormalizedNames.add(shape);
		}
		visioPage.setShapes(shapesWithNormalizedNames);
		return visioPage;
	}

	private String normalizeName(String name) {
		if (name != null && name.contains(".")) {
			int end = name.indexOf(".");
			String cleanedName = name.substring(0, end);
			return cleanedName;
		}
		return name;
	}

	private Page convertMarkersToTypeWithProperties(Page page) {
		String propertyElementsString = importUtil.getMappingConfig("areOnlyProperties");
		if (propertyElementsString != null && !"".equals(propertyElementsString)) {
			String[] propertyElements = propertyElementsString.split(",");
			Shape resultingShape = null;
			for (String propertyElementName : propertyElements) {
				List<Shape> propertyShapes = page.getShapesByName(propertyElementName);
				for (Shape propertyShape : propertyShapes) {
					Shape containingShape = shapeUtil.getFirstShapeOfStencilThatContainsTheGivenShape(page.getShapes(), propertyShape, "Task");
					if (containingShape == null)
						containingShape = shapeUtil.getFirstShapeOfStencilThatContainsTheGivenShape(page.getShapes(), propertyShape, "Subprocess");
					if (containingShape == null)
						containingShape = shapeUtil.getFirstShapeOfStencilThatContainsTheGivenShape(page.getShapes(), propertyShape, "CollapsedSubprocess");
					if (containingShape != null) {
						resultingShape = containingShape;
					}
					page.removeShape(propertyShape);
					String propertyKey = importUtil.getMappingConfig("Properties." + propertyElementName + ".key");
					String propertyValue = importUtil.getMappingConfig("Properties." + propertyElementName + ".value");
					if (resultingShape != null && propertyKey != null && propertyValue != null) {
						resultingShape.putProperty(propertyKey, propertyValue);
					}
				}
			}
		}
		return page;
	}

	private Page convertSpecialTypesToTypesWithProperties(Page page) {
		String specialTypesString = importUtil.getMappingConfig("specialTypes");
		if (specialTypesString != null && !"".equals(specialTypesString)) {
			String[] nameUTypesIncludingProperties = specialTypesString.split(",");
			for (Shape shape : page.getShapes()) {
				for (String specialType : nameUTypesIncludingProperties) {
					if (specialType.equals(shape.getName())) {
						String[] keys = importUtil.getMappingConfig(shape.getName() + ".keys").split(",");
						String[] values = importUtil.getMappingConfig(shape.getName() + ".values").split(",");
						for (int i = 0; i < keys.length; i++) {
							shape.putProperty(keys[i], values[i]);
						}
					}
				}
			}
		}
		return page;
	}

	private Page convertTaskWithMarkerToSubprocesses(Page page) {
		List<Shape> subprocessMarkers = page.getShapesByName("Collapsed Subprocess Marker");
		for (Shape marker : subprocessMarkers) {
			Shape containingShape = 
				shapeUtil.getFirstShapeOfStencilThatContainsTheGivenShape(page.getShapes(), marker, "Task");
			if (containingShape != null) {
				containingShape.setName(importUtil.getMappingConfig("taskWithSubprocessMarker"));
			}
			page.removeShape(marker);
		}
		return page;
	}

	private boolean hasCompleteBoundaries(Shape shape) {
		XForm xForm = shape.xForm;
		if (xForm.height != null && xForm.width != null && xForm.positionX != null && xForm.positionY != null) {
			return true;
		} else {
			return false;
		}
	}

}
