package de.hpi.visio.util;

import java.util.Comparator;

import org.oryxeditor.server.diagram.Shape;

public class ShapesLowerRightXComparator implements Comparator<Shape> {
	
	@Override
	public int compare(Shape o1, Shape o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2 == null) {
			return -1;
		}
		if (o1.getLowerRight() == null) {
			if (o2.getLowerRight() == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2.getLowerRight() == null) {
			return -1;
		}
		if (o1.getLowerRight().getX() == null) {
			if (o2.getLowerRight().getX() == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2.getLowerRight().getX() == null) {
			return -1;
		}
		return o1.getLowerRight().getX().compareTo(o2.getLowerRight().getX());
	}

}
