package de.hpi.util;

import java.awt.Point;

public class Bounds {
	
	private double x1, x2, y1, y2;

	/**
	 * if x1 > x2, then switch x1 and x2
	 * if y1 > y2, then switch y1 and y2
	 */
	public Bounds(double x1, double y1, double x2,  double y2) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		correctBounds();
	}
	
	private void correctBounds() {
		if (x1 > x2) {
			this.x1 = x2;
			this.x2 = x1;
		}
		if (y1 > y2) {
			this.y1 = y2;
			this.y2 = y1;
		}
	}

	public Bounds(Point point1, Point point2) {
		this(point1.x, point1.y, point2.x, point2.y);
	}
	
	public String toString(){
		return toString(",");
	}
	
	public String toString(String delimiter){
		return String.valueOf(x1)+delimiter+String.valueOf(y1)+delimiter+String.valueOf(x2)+delimiter+String.valueOf(y2);
	}
	
	public double getX1() {
		return x1;
	}

	public double getX2() {
		return x2;
	}

	public double getY1() {
		return y1;
	}

	public double getY2() {
		return y2;
	}

	/**
	 * Calculates center point relatively to bounds.
	 * E.g. new Bounds(1, 5, 3, 9).getCenterRelative() => new Point(2, 3)
	 * @return center point relatively to bounds
	 */
	public Point getCenterRelative(){
		return new Point((int)Math.round((x2 - x1))/2, (int)Math.round((y2 - y1)/2));
	}
}
