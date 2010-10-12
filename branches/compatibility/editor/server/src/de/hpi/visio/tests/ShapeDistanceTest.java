package de.hpi.visio.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Shape;
import de.hpi.visio.util.DistanceToShapeComparator;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.VisioShapeDistanceUtil;

/**
 * Test for the main parts of the VisioShapeDistanceUtil and also
 * a test if the distanceToShapeComparator works right.
 * 
 * @author Thamsen
 */
public class ShapeDistanceTest {
	
	private ImportConfigurationUtil importUtil;
	private VisioShapeDistanceUtil distanceUtil;
	private Shape shape1;
	private Shape shape2;
	
	@Before
	public void setUp() {
		setUpDistanceUtil();
		setUpShape1();
		setUpShape2();
	}

	private void setUpDistanceUtil() {
		String path = System.getProperty("user.dir");
		importUtil = new ImportConfigurationUtil(path + "/editor/data/execution/", "bpmn");
		distanceUtil = new VisioShapeDistanceUtil(importUtil);
	}
	
	private void setUpShape1() {
		shape1 = new Shape();
		shape1.setCentralPin(new Point(5.0, 5.0));
		shape1.setHeight(5.0);
		shape1.setWidth(5.0);
	}
	
	private void setUpShape2() {
		shape2 = new Shape();
		shape2.setCentralPin(new Point(5.0, 5.0));
		shape2.setHeight(5.0);
		shape2.setWidth(5.0);
	}
	
	@Test
	public void testDistanceBetweenAShapeAndAPoint() {
		Map<Shape, Double> distanceMap = new HashMap<Shape, Double>();
		Double distance1 = distanceUtil.getDistanceToShapeBorderFromPoint(shape1, new Point(15.0, 20.0));
		distanceMap.put(shape1, distance1);		
		Double distance2 = distanceUtil.getDistanceToShapeBorderFromPoint(shape2, new Point(15.0, 10.0));
		distanceMap.put(shape2, distance2);
		List<Map.Entry<Shape, Double>> distanceList = new ArrayList<Map.Entry<Shape, Double>>(distanceMap.entrySet());
		Collections.sort(distanceList, new DistanceToShapeComparator());
		assertTrue(Math.abs(distance1 - 14.577) < 0.1);
		assertTrue(Math.abs(distance2 - 7.90) < 0.1);
		assertTrue(distanceList.get(0).getKey().equals(shape2));
	}

}
