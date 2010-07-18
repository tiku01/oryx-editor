package de.hpi.visio.util;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;

/**
 * Utility class to convert the bounds and points of a visio shape to oryx
 * shapes. This also converts from a metric way to express sizes to express them
 * to pixel bounds.
 * 
 * Also gets dockers for these shapes.
 * 
 * Also there are differences between the canvas and what defines bounds at
 * visio and in oryx. See end of class file for a sketch of this differences.
 * 
 * @author Thamsen
 */
public class VisioShapeBoundsUtil {

	public ImportConfigurationUtil importUtil;
	public Integer visioPointUnitFactor;

	public VisioShapeBoundsUtil(ImportConfigurationUtil importUtil) {
		this.importUtil = importUtil;
		visioPointUnitFactor = Integer.valueOf(importUtil.getHeuristic("Unit_To_Pixel_Exchange"));
	}

	/**
	 * Returns shapes bounds as pixels, so that the returned bounds object can
	 * be used with oryx shapes
	 */
	public Bounds getCorrectOryxShapeBounds(Bounds bounds) {
		Point correctedLowerRight = convertToOryxPoint(bounds.getLowerRight());
		Point correctedUpperLeft = convertToOryxPoint(bounds.getUpperLeft());
		Bounds result = new Bounds(correctedLowerRight, correctedUpperLeft);
		return result;
	}

	/**
	 * Converts a point of the visio metric unit canvas to the oryx pixel
	 * canvas.
	 */
	public Point convertToOryxPoint(Point point) {
		Point correctedPoint = new Point(point.getX() * visioPointUnitFactor, point.getY() * visioPointUnitFactor);
		return correctedPoint;
	}

	/**
	 * Returns shape bounds to use as bounds in a oryx shape. Sizes will be in
	 * pixel and also resizing of bounds is done, according to the
	 * size-properties of that shape. Mind that there are shapes, that can be
	 * resized, that can be resized but have minimal size and some with
	 * completely fixed size.
	 */
	public Bounds getCorrectOryxShapeBoundsWithResizing(Shape shape, Page page) {
		Bounds correctedAndCheckedBounds = null;
		Bounds shapeBounds = shape.getBoundsOnPage(page);
		Bounds correctedBounds = getCorrectOryxShapeBounds(shapeBounds);
		correctedAndCheckedBounds = resizeIfNecessary(shape, page, correctedBounds);
		return correctedAndCheckedBounds;
	}

	private Bounds resizeIfNecessary(Shape shape, Page page, Bounds correctedBounds) {
		Bounds correctedAndCheckedBounds;
		String fixedSizeCategory = getShapeCategory(shape, page);
		if (fixedSizeCategory != null) {
			correctedAndCheckedBounds = getFixedBoundsSizeForShapeOnPage(correctedBounds, fixedSizeCategory);
		} else {
			if (isShapeResizableWithMinimumSize(shape)) {
				correctedAndCheckedBounds = checkBoundsForShapesMinimumSize(correctedBounds, shape);
			} else {
				// edges are resizable without a minimum size
				correctedAndCheckedBounds = correctedBounds;
			}
		}
		return correctedAndCheckedBounds;
	}

	private Boolean isShapeResizableWithMinimumSize(Shape shape) {
		Boolean isResizable = false;
		String elementsWithMinimumString = importUtil.getStencilSetConfig("ElementsWithMinimum");
		if (elementsWithMinimumString != null && !"".equals(elementsWithMinimumString)) {
			String[] resizableElements = elementsWithMinimumString.split(",");
			for (int i = 0; i < resizableElements.length; i++) {
				String stencilId = importUtil.getStencilIdForName(shape.name);
				if (resizableElements[i].equalsIgnoreCase(stencilId))
					isResizable = true;
			}
		}
		return isResizable;
	}

	private String getShapeCategory(Shape shape, Page page) {
		String stencilId = importUtil.getStencilIdForName(shape.getName());
		String fixedSizeShapeCategoriesString = importUtil.getStencilSetConfig("fixedSizeShapeCategories");
		if (fixedSizeShapeCategoriesString != null && !"".equals(fixedSizeShapeCategoriesString)) {
			String[] fixedSizeShapeCategories = fixedSizeShapeCategoriesString.split(",");
			for (String category : fixedSizeShapeCategories) {
				String[] fixedSizeShapes = importUtil.getStencilSetConfig(category).split(",");
				for (String fixedSizeShape : fixedSizeShapes) {
					if (fixedSizeShape.equalsIgnoreCase(stencilId)) {
						return category;
					}
				}
			}
		}
		return null;
	}

	private Bounds getFixedBoundsSizeForShapeOnPage(Bounds bounds, String fixedSizeCategory) {
		String actualCategory = importUtil.getStencilSetConfig(fixedSizeCategory + ".category");
		Double fixedWidth = Double.valueOf(importUtil.getStencilSetConfig(actualCategory + ".fixedWidth"));
		Double fixedHeight = Double.valueOf(importUtil.getStencilSetConfig(actualCategory + ".fixedHeight"));
		Double centralPinX = bounds.getUpperLeft().getX()
				+ ((bounds.getLowerRight().getX() - bounds.getUpperLeft().getX()) / 2.0);
		Double centralPinY = bounds.getUpperLeft().getY()
				+ ((bounds.getLowerRight().getY() - bounds.getUpperLeft().getY()) / 2.0);
		bounds.setUpperLeft(new Point(centralPinX - fixedWidth / 2, centralPinY - fixedHeight / 2));
		bounds.setLowerRight(new Point(centralPinX + fixedWidth / 2, centralPinY + fixedHeight / 2));
		return bounds;
	}

	private Bounds checkBoundsForShapesMinimumSize(Bounds bounds, Shape shape) {
		String stencilId = importUtil.getStencilIdForName(shape.name);
		Double minHeight = Double.valueOf(importUtil.getStencilSetConfig(stencilId + ".minHeight"));
		Double minWidth = Double.valueOf(importUtil.getStencilSetConfig(stencilId + ".minWidth"));
		Double actualWidth = bounds.getLowerRight().getX() - bounds.getUpperLeft().getX();
		Double actualHeight = bounds.getLowerRight().getY() - bounds.getUpperLeft().getY();
		if (actualHeight < minHeight) {
			resizeToMinimalHeight(bounds, minHeight);
		}
		if (actualWidth < minWidth) {
			resizeToMinimalWidth(bounds, minWidth);
		}
		return bounds;
	}

	private void resizeToMinimalHeight(Bounds bounds, Double minHeight) {
		Double centralPinY = bounds.getUpperLeft().getY()
				+ ((bounds.getLowerRight().getY() - bounds.getUpperLeft().getY()) / 2.0);
		bounds.setLowerRight(new Point(bounds.getLowerRight().getX(), centralPinY + (minHeight / 2)));
		bounds.setUpperLeft(new Point(bounds.getUpperLeft().getX(), centralPinY - (minHeight / 2)));
	}

	private void resizeToMinimalWidth(Bounds bounds, Double minWidth) {
		Double centralPinX = bounds.getUpperLeft().getX()
				+ ((bounds.getLowerRight().getX() - bounds.getUpperLeft().getX()) / 2.0);
		bounds.setLowerRight(new Point(centralPinX + (minWidth / 2), bounds.getLowerRight().getY()));
		bounds.setUpperLeft(new Point(centralPinX - (minWidth / 2), bounds.getUpperLeft().getY()));
	}

}


/*	Interpreting Visio XForm values	
 * 
 * 	@ Visio:
 * 
 * 	Visio canvas:
 * 	
 * 	y increases
 *  ^
 *	|
 *	|
 *	|
 *	-----------> x increases
 *
 *	Shapes:
 *	defined by: centralPin, width, height
 *	PIN: *
 *	-----------------
 *	|				|
 *	|				|
 *	|		* 		|
 *	|				|
 *	|				|
 *	-----------------
 *	
 *
 *
 *	@ ORYX:
 *
 *	The oryx canvas:
 *	defined by: upperLeftPoint, lowerRightPoint
 *	
 *	------------> x increases
 *	|
 *	|
 *	|
 *	|
 *	v y increases
 * 
 */