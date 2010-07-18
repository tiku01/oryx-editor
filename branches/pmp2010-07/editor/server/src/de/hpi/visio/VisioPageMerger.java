package de.hpi.visio;

import java.util.List;

import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.data.VisioDocument;

/**
 * In visio it's possible to create several pages within a .vdx-document, each
 * page can contain stencil-element. Due to a direct import into the editor
 * instead into the explorer or into diagram-files, those pages are currently
 * merged into one page, that has the summed height and the max width.
 * 
 * @author Thamsen
 */
public class VisioPageMerger {

	/**
	 * Merges all pages of a visio-document to a single page of added size, so
	 * that all shapes will imported into one model at oryx.
	 * 
	 * @return
	 */
	public VisioDocument mergeAllPages(VisioDocument visioData) {
		List<Page> allPages = visioData.getPages();
		if (allPages.size() > 1) {
			visioData = mergeAllPagesIntoOne(visioData);
		}
		return visioData;
	}

	private VisioDocument mergeAllPagesIntoOne(VisioDocument visioData) {
		List<Page> pages = visioData.getPages();
		Page singlePage = createNewPageWithRightWidth(pages);
		addAllPagesToSinglePage(pages, singlePage);
		visioData.setFirstAndOnlyPage(singlePage);
		return visioData;
	}

	private Page createNewPageWithRightWidth(List<Page> pages) {
		Page singlePage = new Page();
		Double maxWidth = getMaxWidth(pages);
		singlePage.setWidth(maxWidth);
		return singlePage;
	}

	private void addAllPagesToSinglePage(List<Page> pages, Page singlePage) {
		while (!pages.isEmpty()) {
			Page lastPage = pages.get(pages.size() - 1);
			addAllShapesToSinglePage(lastPage.getShapes(), singlePage);
			singlePage.setHeight(singlePage.getHeight() + lastPage.getHeight());
			pages.remove(lastPage);
		}
	}

	private void addAllShapesToSinglePage(List<Shape> shapes, Page singlePage) {
		for (Shape newShape : shapes) {
			Double newX = newShape.getCentralPin().getX();
			Double newY = newShape.getCentralPin().getY() + singlePage.getHeight();
			Point newPin = new Point(newX, newY);
			newShape.setCentralPin(newPin);
			singlePage.addShape(newShape);
		}
	}

	private Double getMaxWidth(List<Page> pages) {
		Double max = 0.0;
		for (Page page : pages) {
			if (page.getWidth() > max)
				max = page.getWidth();
		}
		return max;
	}

}
