package de.hpi.visio.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.visio.util.ShapesLowerRightXComparator;
import de.hpi.visio.util.ShapesLowerRightYComparator;
import de.hpi.visio.util.ShapesUpperLeftXComparator;
import de.hpi.visio.util.ShapesUpperLeftYComparator;


/**
 * Test for all created comparators that are used for sorting oryx shape
 * due to their positions.
 * 
 * @author Thamsen
 */
public class OryxShapeComparatorTest {
	
	private List<Shape> shapes = new ArrayList<Shape>();
	private Shape lowerLeftShape;
	private Shape centralShape;
	private Shape upperRightShape;
	
	@Before 
	public void setUp() {
		setUpLowerLeftShape();
		setUpCentralShape();
		setUpUpperRightShape();
	}

	private void setUpLowerLeftShape() {
		lowerLeftShape = new Shape(null);
		Point upperLeft = new Point(0.0, 10.0);
		Point lowerRight = new Point(10.0, 0.0);
		Bounds bounds = new Bounds(upperLeft, lowerRight);
		lowerLeftShape.setBounds(bounds);
		shapes.add(lowerLeftShape);
	}

	private void setUpCentralShape() {
		centralShape = new Shape(null);
		Point upperLeft = new Point(5.0, 15.0);
		Point lowerRight = new Point(15.0, 5.0);
		Bounds bounds = new Bounds(upperLeft, lowerRight);
		centralShape.setBounds(bounds);
		shapes.add(centralShape);
	}

	private void setUpUpperRightShape() {
		upperRightShape = new Shape(null);
		Point upperLeft = new Point(10.0, 20.0);
		Point lowerRight = new Point(20.0, 10.0);
		Bounds bounds = new Bounds(upperLeft, lowerRight);
		upperRightShape.setBounds(bounds);
		shapes.add(upperRightShape);
	}

	@Test
	public void testShapesLowerRightXComparator() {
		Collections.sort(shapes, new ShapesLowerRightXComparator());
		assertEquals(upperRightShape, shapes.get(0));
		assertEquals(centralShape, shapes.get(1));
		assertEquals(lowerLeftShape, shapes.get(2));
	}
	
	@Test
	public void testShapesLowerRightYComparator() {
		Collections.sort(shapes, new ShapesLowerRightYComparator());
		assertEquals(lowerLeftShape, shapes.get(0));
		assertEquals(centralShape, shapes.get(1));
		assertEquals(upperRightShape, shapes.get(2));
	}
	
	@Test
	public void testShapesUpperLeftXComparator() {
		Collections.sort(shapes, new ShapesUpperLeftXComparator());
		assertEquals(lowerLeftShape, shapes.get(0));
		assertEquals(centralShape, shapes.get(1));
		assertEquals(upperRightShape, shapes.get(2));
	}
	
	@Test
	public void testShapesUpperLeftYComparator() {
		Collections.sort(shapes, new ShapesUpperLeftYComparator());
		assertEquals(upperRightShape, shapes.get(0));
		assertEquals(centralShape, shapes.get(1));
		assertEquals(lowerLeftShape, shapes.get(2));
	}
	
}
