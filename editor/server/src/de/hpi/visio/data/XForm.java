package de.hpi.visio.data;

import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("XForm")
public class XForm {
	
	/*	Interpreting Visio XForm values	
	 * 
	 * 	y increases
	 *  ^
	 *	|
	 *	|
	 *	|
	 *	-----------> x increases
	 *
	 *	PINs are at the center: *
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
	 *	The oryx canvas
	 *	
	 *	------------> x increas
	 *	|
	 *	|
	 *	|
	 *	|
	 *	v y increases
	 *
	 */
	
	@Element("PinX")
	public PinX positionX;
	
	@Element("PinY")
	public PinY positionY;
	
	@Element("Width")
	public Width width;
	
	@Element("Height")
	public Height height;
	
	public Point getUpperLeftPointForPage(Page visioPage) {
		Double upperLeftX = getX() - (getWidth() / 2);
		Double upperLeftY = visioPage.getHeight() - (getCorrectedY() + (getHeight() / 2));
		return new Point(upperLeftX, upperLeftY);
	}
	
	public Point getLowerRightPointForPage(Page visioPage) {
		Double lowerRightX = getX() + (getWidth() / 2);
		Double lowerRightY = visioPage.getHeight() - (getCorrectedY() - (getHeight() / 2));
		return new Point(lowerRightX, lowerRightY);
	}
	
	public Double getHeight() {
		return height.getHeight();
	}
	
	public Double getWidth() {
		return width.getWidth();
	}
	
	public Double getX() {
		return positionX.getX();
	}
	
	public Double getCorrectedY() {
		return positionY.getY();
	}
	
//	public double cutAfterTheComma(Double value) {
//		Integer intValue = value.intValue();
//		Double cuttedValue = intValue.doubleValue();
//		return cuttedValue;
//	}

}
