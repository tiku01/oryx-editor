package de.hpi.visio.data;

import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping. Additional graphical information for
 * edges. Start and end points that can be used to calculate the source and
 * target for a given edge and a page with several shapes.
 * 
 * @author Thamsen
 */
@RootElement("XForm1D")
public class xFormStartAndEnd {

	@Element("BeginX")
	public StartX startX;

	@Element("BeginY")
	public StartY startY;

	@Element("EndX")
	public EndX endX;

	@Element("EndY")
	public EndY endY;

	public Double getStartX() {
		return startX.getX();
	}

	public void setStartX(Double beginX) {
		if (this.startX == null)
			this.startX = new StartX();
		this.startX.setX(beginX);
	}

	public Double getStartY() {
		return startY.getY();
	}

	public void setStartY(Double beginY) {
		if (this.startY == null)
			this.startY = new StartY();
		this.startY.setY(beginY);
	}

	public Double getEndX() {
		return endX.getX();
	}

	public void setEndX(Double endX) {
		if (this.endX == null)
			this.endX = new EndX();
		this.endX.setX(endX);
	}

	public Double getEndY() {
		return endY.getY();
	}

	public void setEndY(Double endY) {
		if (this.endY == null)
			this.endY = new EndY();
		this.endY.setY(endY);
	}

	public Point getStartPoint() {
		return new Point(startX.getX(), startY.getY());
	}

	public Point getEndPoint() {
		return new Point(endX.getX(), endY.getY());
	}

	public void setStartPoint(Point startPoint) {
		setStartX(startPoint.getX());
		setStartY(startPoint.getY());
	}

	public void setEndPoint(Point endPoint) {
		setEndX(endPoint.getX());
		setEndY(endPoint.getY());
	}

	public Point getStartPointForPage(Page visioPage) {
		return new Point(getStartX(), visioPage.getHeight() - getStartY());
	}

	public Point getEndPointForPage(Page visioPage) {
		return new Point(getEndX(), visioPage.getHeight() - getEndY());
	}

}
