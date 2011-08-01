package de.hpi.visio.data;

import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping. xForm: For all visio shapes there are
 * information about their boundaries (defined by width, height and a central
 * point). Also the angle - rotation - of the shape.
 * 
 * @author Thamsen
 */
@RootElement("XForm")
public class XForm {

	@Element("PinX")
	public PinX positionX;

	@Element("PinY")
	public PinY positionY;

	@Element("Width")
	public Width width;

	@Element("Height")
	public Height height;

	@Element("Angle")
	public Angle angle;

	public Point getUpperLeftPointForPage(Page visioPage) {
		swapWidthAndHeightIfThereIsAnAngle();
		Double upperLeftX = getX() - (getWidth() / 2);
		Double upperLeftY = visioPage.getHeight() - (getY() + (getHeight() / 2));
		return new Point(upperLeftX, upperLeftY);
	}

	public Point getUpperLeftVisioPoint() {
		swapWidthAndHeightIfThereIsAnAngle();
		Double upperLeftX = getX() - (getWidth() / 2);
		Double upperLeftY = getY() + (getHeight() / 2);
		return new Point(upperLeftX, upperLeftY);
	}

	public Point getLowerRightPointForPage(Page visioPage) {
		swapWidthAndHeightIfThereIsAnAngle();
		Double lowerRightX = getX() + (getWidth() / 2);
		Double lowerRightY = visioPage.getHeight() - (getY() - (getHeight() / 2));
		return new Point(lowerRightX, lowerRightY);
	}

	public Point getLowerRightVisioPoint() {
		swapWidthAndHeightIfThereIsAnAngle();
		Double lowerRightX = getX() + (getWidth() / 2);
		Double lowerRightY = getY() - (getHeight() / 2);
		return new Point(lowerRightX, lowerRightY);
	}

	public Double getHeight() {
		swapWidthAndHeightIfThereIsAnAngle();
		return height.getHeight();
	}

	public Double getWidth() {
		swapWidthAndHeightIfThereIsAnAngle();
		return width.getWidth();
	}

	public Double getX() {
		return positionX.getX();
	}

	public Double getY() {
		return positionY.getY();
	}

	public Point getCentralPin() {
		return new Point(positionX.getX(), positionY.getY());
	}

	public Point getCentralPinForPage(Page visioPage) {
		swapWidthAndHeightIfThereIsAnAngle();
		return new Point(getX(), visioPage.getHeight() - getY());
	}

	public void setWidth(Double width) {
		if (this.width == null)
			this.width = new Width();
		this.width.setWidth(width);
	}

	public void setHeight(Double height) {
		if (this.height == null)
			this.height = new Height();
		this.height.setHeight(height);
	}

	public void setCentralPin(Point newPin) {
		if (positionX == null)
			positionX = new PinX();
		if (positionY == null)
			positionY = new PinY();
		positionX.setX(newPin.getX());
		positionY.setY(newPin.getY());
	}

	private void swapWidthAndHeightIfThereIsAnAngle() {
		// angle around 90 degrees? pool headers...
		if (angle != null && angle.getAngle() != null && angle.getAngle() > 1 && angle.getAngle() < 2) {
			Double height = this.height.getHeight();
			Double width = this.width.getWidth();
			this.height.setHeight(width);
			this.width.setWidth(height);
			angle.setAngle(0.0);
		}
	}

}
