package de.hpi.visio.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.oryxeditor.server.diagram.Point;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.VisioShapeBoundsUtil;
import de.hpi.visio.util.VisioShapeDockerUtil;

/**
 * Test whether the VisioShapeDockerUtil is working right for
 * edge-typed shapes and non-edge-typed-shapes.
 * 
 * @author Thamsen
 */
public class VisioShapeDockerUtilTest {
	
	Page testPage;
	Shape nonEdge;
	Shape edge;
	VisioShapeDockerUtil dockerUtil;
	ImportConfigurationUtil importUtil;
	
	@Before
	public void setUp() {
		setUpTestPage();
		setUpEdgeShape();
		setUpNonEdgeShape();
		setUpDockerUtil();
	}

	private void setUpDockerUtil() {
		String path = System.getProperty("user.dir");
		importUtil = new ImportConfigurationUtil(path + "/editor/data/execution/", "bpmn");
		VisioShapeBoundsUtil boundsUtil = new VisioShapeBoundsUtil(importUtil);
		dockerUtil = new VisioShapeDockerUtil(importUtil, boundsUtil);
	}

	private void setUpTestPage() {
		testPage = new Page();
		testPage.setHeight(30.0);
		testPage.setWidth(30.0);
	}

	private void setUpEdgeShape() {
		edge = new Shape();
		edge.setCentralPin(new Point(10.0,10.0));
		edge.setStartPoint(new Point(5.0,5.0));
		edge.setEndPoint(new Point(15.0,15.0));
		edge.setWidth(10.0);
		edge.setHeight(10.0);
		edge.setName("Sequence Flow");
	}

	private void setUpNonEdgeShape() {
		nonEdge = new Shape();
		nonEdge.setCentralPin(new Point(10.0,10.0));
		nonEdge.setWidth(5.0);
		nonEdge.setHeight(5.0);
		nonEdge.setName("Task");
	}
	
	@Test
	public void testCorrectedEdgeDockers() {
		List<Point> dockers = dockerUtil.getCorrectedDockersForShape(edge, testPage);
		assertTrue(dockers.size() == 0);	// zero is right since there is neither source nor target shape
	}
	
	@Test 
	public void testCorrectedNonEdgeDockers() {
		List<Point> dockers = dockerUtil.getCorrectedDockersForShape(nonEdge, testPage);
		assertTrue(dockers.size() == 1);
		Double pixelFactor = new Double(importUtil.getHeuristic("Unit_To_Pixel_Exchange"));
		Double xValue = 10 * pixelFactor;
		Double yValue = 20 * pixelFactor;
		assertEquals(xValue, dockers.get(0).getX());
		assertEquals(yValue, dockers.get(0).getY());
	}
	
	

}
