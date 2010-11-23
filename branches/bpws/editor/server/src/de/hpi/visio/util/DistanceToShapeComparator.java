package de.hpi.visio.util;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import de.hpi.visio.data.Shape;

/**
 * Comparator for imported visio shapes with a given distance to some point or
 * stencil. The shape with the lowest distance will be sorted to the head of the
 * list.
 * 
 * @author Thamsen
 */
public class DistanceToShapeComparator implements Comparator<Map.Entry<Shape, Double>> {

	@Override
	public int compare(Entry<Shape, Double> o1, Entry<Shape, Double> o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2 == null) {
			return -1;
		}
		if (o1.getValue() == null) {
			if (o2.getValue() == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (o2.getValue() == null) {
			return -1;
		}
		return o1.getValue().compareTo(o2.getValue());
	}

}
