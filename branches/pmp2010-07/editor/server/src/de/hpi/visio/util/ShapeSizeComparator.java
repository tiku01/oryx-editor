package de.hpi.visio.util;

import java.util.Comparator;

import de.hpi.visio.data.Shape;

public class ShapeSizeComparator implements Comparator<Shape> {

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
		if (o1.getArea() == null) {
			if (o2.getArea() == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2.getArea() == null) {
			return -1;
		}
		return o1.getArea().compareTo(o2.getArea());
	}

}
