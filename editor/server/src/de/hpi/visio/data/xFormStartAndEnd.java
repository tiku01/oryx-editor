package de.hpi.visio.data;

import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

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
		this.startX.setX(beginX);
	}

	public Double getStartY() {
		return startY.getY();
	}

	public void setStartY(Double beginY) {
		this.startY.setY(beginY);
	}

	public Double getEndX() {
		return endX.getX();
	}

	public void setEndX(Double endX) {
		this.endX.setX(endX);
	}

	public Double getEndY() {
		return endY.getY();
	}

	public void setEndY(Double endY) {
		this.endY.setY(endY);
	}
	
	public Point getStartPoint() {
		return new Point(startX.getX(),startY.getY());
	}
	
	public Point getEndPoint() {
		return new Point(endX.getX(),endY.getY());
	}

	public void setStartPoint(Point startPoint) {
		setStartX(startPoint.getX());
		setStartY(startPoint.getY());
	}
	
	public void setEndPoint(Point endPoint) {
		setEndX(endPoint.getX());
		setEndY(endPoint.getY());
	}

}
