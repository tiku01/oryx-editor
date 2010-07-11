package de.hpi.visio.util;

import java.util.Comparator;

import org.oryxeditor.server.diagram.Shape;

public class ShapesUpperLeftYComparator implements Comparator<Shape> {

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
		if (o1.getUpperLeft() == null) {
			if (o2.getUpperLeft() == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2.getUpperLeft() == null) {
			return -1;
		}
		if (o1.getUpperLeft().getY() == null) {
			if (o2.getUpperLeft().getY() == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2.getUpperLeft().getY() == null) {
			return -1;
		}
		return o1.getUpperLeft().getY().compareTo(o2.getUpperLeft().getY());
	}


	
}
